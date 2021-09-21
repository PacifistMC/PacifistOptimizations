package me.rancraftplayz.pacifist.optimizations.dimthread.mixins;

import me.rancraftplayz.pacifist.optimizations.dimthread.DimThread;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Original code from DimensionalThreading (https://github.com/WearBlackAllDay/DimensionalThreading)
 * Licensed under the MIT License
 */

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Entity teleportTo(ServerLevel worldserver, BlockPos location);

    /**
     * Schedules moving entities between dimensions to the server thread. Once all the world finish ticking,
     * {@code moveToWorld()} is processed in a safe manner avoiding concurrent modification exceptions.
     *
     * For example, the entity list is not thread-safe and modifying it from multiple threads will cause
     * a crash. Additionally, loading chunks from another thread will cause a deadlock in the server chunk manager.
     */
    @Inject(method = "teleportTo", at = @At("HEAD"), cancellable = true)
    public void moveToWorld(ServerLevel worldserver, BlockPos location, CallbackInfoReturnable<Entity> ci) {
        if(!DimThread.MANAGER.isActive(worldserver.getServer()))return;

        if(DimThread.owns(Thread.currentThread())) {
            worldserver.getServer().execute(() -> this.teleportTo(worldserver, location));
            ci.setReturnValue(null);
        }
    }
}
