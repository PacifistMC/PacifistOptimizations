package me.rancraftplayz.pacifist.optimizations.lithium.common;

import net.minecraft.world.level.entity.EntitySectionStorage;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

public interface EntityTrackerSection {
    void addListener(NearbyEntityListener listener);

    void removeListener(EntitySectionStorage<?> sectionedEntityCache, NearbyEntityListener listener);

    void addListener(SectionedEntityMovementTracker<?, ?> listener);

    void removeListener(EntitySectionStorage<?> sectionedEntityCache, SectionedEntityMovementTracker<?, ?> listener);

    void updateMovementTimestamps(int notificationMask, long time);

    long[] getMovementTimestampArray();

    void setPos(long chunkSectionPos);

    long getPos();
}
