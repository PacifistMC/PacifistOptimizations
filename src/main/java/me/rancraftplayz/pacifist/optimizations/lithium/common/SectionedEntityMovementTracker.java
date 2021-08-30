package me.rancraftplayz.pacifist.optimizations.lithium.common;

import me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.nearby_entity_tracking.ServerEntityManagerAccessor;
import me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.nearby_entity_tracking.ServerWorldAccessor;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;

import java.util.ArrayList;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

public abstract class SectionedEntityMovementTracker<E extends EntityAccess, S> {
    final WorldSectionBox trackedWorldSections;
    final Class<S> clazz;
    private final int trackedClass;
    ArrayList<EntitySection<E>> sortedSections;
    boolean[] sectionVisible;
    private int timesRegistered;
    private ArrayList<long[]> sectionChangeCounters;

    private long maxChangeTime;

    public SectionedEntityMovementTracker(WorldSectionBox interactionChunks, Class<S> clazz) {
        this.clazz = clazz;
        this.trackedWorldSections = interactionChunks;
        this.trackedClass = EntityTrackerEngine.MOVEMENT_NOTIFYING_ENTITY_CLASSES.indexOf(clazz);
        assert this.trackedClass != -1;
    }

    /**
     * Method to quickly check whether any relevant entities moved inside the relevant entity sections after
     * the last interaction attempt.
     *
     * @param lastCheckedTime time of the last interaction attempt
     * @return whether any relevant entity moved in the tracked area
     */
    public boolean isUnchangedSince(long lastCheckedTime) {
        if (lastCheckedTime <= this.maxChangeTime) {
            return false;
        }
        ArrayList<long[]> sectionChangeCounters = this.sectionChangeCounters;
        int trackedClass = this.trackedClass;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, numCounters = sectionChangeCounters.size(); i < numCounters; i++) {
            // >= instead of > is required here, as changes may occur in the same tick but after the last check
            long sectionChangeTime = sectionChangeCounters.get(i)[trackedClass];
            if (lastCheckedTime <= sectionChangeTime) {
                this.setChanged(sectionChangeTime);
                return false;
            }
        }
        return true;
    }

    public void register(ServerLevel world) {
        assert world == this.trackedWorldSections.world();

        if (this.timesRegistered == 0) {
            //noinspection unchecked
            EntitySectionStorage<E> cache = ((ServerEntityManagerAccessor<E>) ((ServerWorldAccessor) world).getEntityManager()).getf();

            this.sectionChangeCounters = new ArrayList<>();
            WorldSectionBox trackedSections = this.trackedWorldSections;
            int size = trackedSections.numSections();
            assert size > 0;
            this.sortedSections = new ArrayList<>(size);
            this.sectionVisible = new boolean[size];

            //vanilla iteration order in SectionedEntityCache is xzy
            //WorldSectionBox upper coordinates are exclusive
            for (int x = trackedSections.chunkX1(); x < trackedSections.chunkX2(); x++) {
                for (int z = trackedSections.chunkZ1(); z < trackedSections.chunkZ2(); z++) {
                    for (int y = trackedSections.chunkY1(); y < trackedSections.chunkY2(); y++) {
                        EntitySection<E> section = cache.getOrCreateSection(SectionPos.asLong(x, y, z));
                        EntityTrackerSection sectionAccess = (EntityTrackerSection) section;
                        this.sortedSections.add(section);
                        sectionAccess.addListener(this);
                    }
                }
            }
            this.setChanged(world.getGameTime());
        }

        this.timesRegistered++;
    }

    public void unRegister(ServerLevel world) {
        assert world == this.trackedWorldSections.world();
        if (--this.timesRegistered > 0) {
            return;
        }
        assert this.timesRegistered == 0;
        //noinspection unchecked
        EntitySectionStorage<E> cache = ((ServerEntityManagerAccessor<E>) ((ServerWorldAccessor) world).getEntityManager()).getf();
        ArrayList<EntitySection<E>> sections = this.sortedSections;
        for (int i = sections.size() - 1; i >= 0; i--) {
            EntitySection<E> section = sections.get(i);
            EntityTrackerSection sectionAccess = (EntityTrackerSection) section;
            sectionAccess.removeListener(cache, this);
        }
        this.setChanged(world.getGameTime());
    }

    /**
     * Register an entity section to this listener, so this listener can look for changes in the section.
     */
    public void onSectionEnteredRange(EntityTrackerSection section) {
        this.setChanged(this.trackedWorldSections.world().getGameTime());
        //noinspection SuspiciousMethodCalls
        this.sectionVisible[this.sortedSections.lastIndexOf(section)] = true;
        this.sectionChangeCounters.add(section.getMovementTimestampArray());
    }

    public void onSectionLeftRange(EntityTrackerSection section) {
        this.setChanged(this.trackedWorldSections.world().getGameTime());
        //noinspection SuspiciousMethodCalls
        this.sectionVisible[this.sortedSections.indexOf(section)] = false;
        this.sectionChangeCounters.remove(section.getMovementTimestampArray());
    }

    /**
     * Method that marks that new entities might have appeared or moved in the tracked chunk sections.
     */
    private void setChanged(long atTime) {
        if (atTime > this.maxChangeTime) {
            this.maxChangeTime = atTime;
        }
    }
}
