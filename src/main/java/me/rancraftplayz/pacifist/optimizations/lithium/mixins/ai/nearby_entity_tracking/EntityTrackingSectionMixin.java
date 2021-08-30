package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.nearby_entity_tracking;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import me.rancraftplayz.pacifist.optimizations.lithium.common.EntityTrackerEngine;
import me.rancraftplayz.pacifist.optimizations.lithium.common.EntityTrackerSection;
import me.rancraftplayz.pacifist.optimizations.lithium.common.NearbyEntityListener;
import me.rancraftplayz.pacifist.optimizations.lithium.common.SectionedEntityMovementTracker;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.Visibility;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(EntitySection.class)
public abstract class EntityTrackingSectionMixin<T> implements EntityTrackerSection {
    @Shadow(aliases = "c")
    private Visibility status;

    @Shadow(aliases = "b")
    @Final
    private ClassInstanceMultiMap<T> collection;

    @Shadow
    public abstract boolean a();

    private final ReferenceOpenHashSet<NearbyEntityListener> nearbyEntityListeners = new ReferenceOpenHashSet<>(0);
    private final ReferenceOpenHashSet<SectionedEntityMovementTracker<?, ?>> sectionVisibilityListeners = new ReferenceOpenHashSet<>(0);
    private final long[] lastEntityMovementByType = new long[EntityTrackerEngine.NUM_MOVEMENT_NOTIFYING_CLASSES];
    private long pos;

    @Override
    public void addListener(NearbyEntityListener listener) {
        this.nearbyEntityListeners.add(listener);
        if (this.status.isAccessible()) {
            listener.onSectionEnteredRange(this, this.collection);
        }
    }

    @Override
    public void removeListener(EntitySectionStorage<?> sectionedEntityCache, NearbyEntityListener listener) {
        boolean removed = this.nearbyEntityListeners.remove(listener);
        if (this.status.isAccessible() && removed) {
            listener.onSectionLeftRange(this, this.collection);
        }
        if (this.a()) {
            sectionedEntityCache.remove(this.pos);
        }
    }

    @Override
    public void addListener(SectionedEntityMovementTracker<?, ?> listener) {
        this.sectionVisibilityListeners.add(listener);
        if (this.status.isAccessible()) {
            listener.onSectionEnteredRange(this);
        }
    }

    @Override
    public void removeListener(EntitySectionStorage<?> sectionedEntityCache, SectionedEntityMovementTracker<?, ?> listener) {
        boolean removed = this.sectionVisibilityListeners.remove(listener);
        if (this.status.isAccessible() && removed) {
            listener.onSectionLeftRange(this);
        }
        if (this.a()) {
            sectionedEntityCache.remove(this.pos);
        }
    }

    @Override
    public void updateMovementTimestamps(int notificationMask, long time) {
        long[] lastEntityMovementByType = this.lastEntityMovementByType;
        int size = lastEntityMovementByType.length;
        int mask;
        for (int i = Integer.numberOfTrailingZeros(notificationMask); i < size; ) {
            lastEntityMovementByType[i] = time;
            mask = 0xffff_fffe << i;
            i = Integer.numberOfTrailingZeros(notificationMask & mask);
        }
    }

    public long[] getMovementTimestampArray() {
        return this.lastEntityMovementByType;
    }

    public void setPos(long chunkSectionPos) {
        this.pos = chunkSectionPos;
    }

    public long getPos() {
        return this.pos;
    }

    @Inject(method = "a()Z", at = @At("HEAD"), cancellable = true)
    public void isEmpty(CallbackInfoReturnable<Boolean> cir) {
        if (!this.nearbyEntityListeners.isEmpty() || !this.sectionVisibilityListeners.isEmpty()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "a(Ljava/lang/Object;)V", at = @At("RETURN"))
    private void onEntityAdded(T var0, CallbackInfo ci) {
        if (!this.status.isAccessible() || this.nearbyEntityListeners.isEmpty()) {
            return;
        }
        if (var0 instanceof Entity entity) {
            for (NearbyEntityListener nearbyEntityListener : this.nearbyEntityListeners) {
                nearbyEntityListener.onEntityEnteredRange(entity);
            }
        }
    }

    @ModifyVariable(method = "b(Ljava/lang/Object;)Z", at = @At("RETURN"))
    private T onEntityRemoved(final T entityLike) {
        if (this.status.isAccessible() && !this.nearbyEntityListeners.isEmpty() && entityLike instanceof Entity entity) {
            for (NearbyEntityListener nearbyEntityListener : this.nearbyEntityListeners) {
                nearbyEntityListener.onEntityLeftRange(entity);
            }
        }
        return entityLike;
    }

    @ModifyVariable(method = "a(Lnet/minecraft/world/level/entity/Visibility;)Lnet/minecraft/world/level/entity/Visibility;", at = @At(value = "HEAD"), argsOnly = true)
    public Visibility swapStatus(final Visibility newStatus) {
        if (this.status.isAccessible() != newStatus.isAccessible()) {
            if (!newStatus.isAccessible()) {
                if (!this.nearbyEntityListeners.isEmpty()) {
                    for (NearbyEntityListener nearbyEntityListener : this.nearbyEntityListeners) {
                        nearbyEntityListener.onSectionLeftRange(this, this.collection);
                    }
                }
                if (!this.sectionVisibilityListeners.isEmpty()) {
                    for (SectionedEntityMovementTracker<?, ?> listener : this.sectionVisibilityListeners) {
                        listener.onSectionLeftRange(this);
                    }
                }
            } else {
                if (!this.nearbyEntityListeners.isEmpty()) {
                    for (NearbyEntityListener nearbyEntityListener : this.nearbyEntityListeners) {
                        nearbyEntityListener.onSectionEnteredRange(this, this.collection);
                    }
                }
                if (!this.sectionVisibilityListeners.isEmpty()) {
                    for (SectionedEntityMovementTracker<?, ?> listener : this.sectionVisibilityListeners) {
                        listener.onSectionEnteredRange(this);
                    }
                }
            }
        }
        return newStatus;
    }
}
