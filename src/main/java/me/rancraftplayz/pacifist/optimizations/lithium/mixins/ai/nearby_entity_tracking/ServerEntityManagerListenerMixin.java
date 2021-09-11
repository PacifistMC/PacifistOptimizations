package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.nearby_entity_tracking;

import me.rancraftplayz.pacifist.optimizations.lithium.common.EntityTrackerEngine;
import me.rancraftplayz.pacifist.optimizations.lithium.common.EntityTrackerSection;
import me.rancraftplayz.pacifist.optimizations.lithium.common.NearbyEntityListenerMulti;
import me.rancraftplayz.pacifist.optimizations.lithium.common.NearbyEntityListenerProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.entity.Visibility;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(targets = "net/minecraft/world/level/entity/PersistentEntitySectionManager$a")
public class ServerEntityManagerListenerMixin<T extends EntityAccess> {
    @Shadow(aliases = "e")
    private EntitySection<T> section;
    @Shadow(aliases = "c")
    @Final
    private T entity;

    net.minecraft.world.level.entity.PersistentEntitySectionManager<T> manager;

    @Shadow(aliases = "d")
    private long sectionPos;

    private int notificationMask;

    // me when spigot is annoying
    @Inject(method = "<init>", at = @At("TAIL"))
    private void spipgot(PersistentEntitySectionManager<T> outer, EntityAccess var1, long var2, EntitySection<?> var4, CallbackInfo ci) {
        this.manager = outer;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(net.minecraft.world.level.entity.PersistentEntitySectionManager<?> outer, EntityAccess var1, long var2, EntitySection<T> var4, CallbackInfo ci) {
        this.notificationMask = EntityTrackerEngine.getNotificationMask(this.entity.getClass());
    }

    @ModifyVariable(method = "a()V", at = @At("RETURN"))
    private long updateEntityTrackerEngine(long sectionPos) {
        if (this.notificationMask != 0) {
            ((EntityTrackerSection) this.section).updateMovementTimestamps(this.notificationMask, ((Entity) this.entity).level.getGameTime());
        }
        return sectionPos;
    }

    @Inject(
            method = "a()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/entity/EntitySection;a(Ljava/lang/Object;)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onAddEntity(CallbackInfo ci, BlockPos blockPos, long newPos, Visibility entityTrackingStatus, EntitySection<T> entityTrackingSection) {
        NearbyEntityListenerMulti listener = ((NearbyEntityListenerProvider) this.entity).getListener();
        if (listener != null) {
            //noinspection unchecked
            listener.forEachChunkInRangeChange(
                    ((ServerEntityManagerAccessor<T>) this.manager).getf(),
                    SectionPos.of(this.sectionPos),
                    SectionPos.of(newPos)
            );
        }
        if (this.notificationMask != 0) {
            ((EntityTrackerSection) this.section).updateMovementTimestamps(this.notificationMask, ((Entity) this.entity).level.getGameTime());
        }
    }

    @Inject(method = "a(Lnet/minecraft/world/entity/Entity$RemovalReason;)V", at = @At("HEAD"))
    private void onRemoveEntity(Entity.RemovalReason reason, CallbackInfo ci) {
        NearbyEntityListenerMulti listener = ((NearbyEntityListenerProvider) this.entity).getListener();
        if (listener != null) {
            //noinspection unchecked
            listener.forEachChunkInRangeChange(
                    ((ServerEntityManagerAccessor<T>) this.manager).getf(),
                    SectionPos.of(this.sectionPos),
                    null
            );
        }
        if (this.notificationMask != 0) {
            ((EntityTrackerSection) this.section).updateMovementTimestamps(this.notificationMask, ((Entity) this.entity).level.getGameTime());
        }
    }
}