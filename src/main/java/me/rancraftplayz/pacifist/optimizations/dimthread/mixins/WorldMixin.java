package me.rancraftplayz.pacifist.optimizations.dimthread.mixins;

import me.rancraftplayz.pacifist.optimizations.dimthread.thread.IMutableMainThread;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Original code from DimensionalThreading (https://github.com/WearBlackAllDay/DimensionalThreading)
 * Licensed under the MIT License
 */

@Mixin(Level.class)
public abstract class WorldMixin implements IMutableMainThread {
    @Mutable @Shadow @Final private Thread thread;

    @Override
    public Thread getMainThread() {
        return this.thread;
    }

    @Override
    public void setMainThread(Thread thread) {
        this.thread = thread;
    }
}
