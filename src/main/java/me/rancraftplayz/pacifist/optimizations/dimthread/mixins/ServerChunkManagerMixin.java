package me.rancraftplayz.pacifist.optimizations.dimthread.mixins;

import me.rancraftplayz.pacifist.optimizations.dimthread.DimThread;
import me.rancraftplayz.pacifist.optimizations.dimthread.thread.IMutableMainThread;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Original code from DimensionalThreading (https://github.com/WearBlackAllDay/DimensionalThreading)
 * Licensed under the MIT License
 */

@Mixin(value = ServerChunkCache.class, priority = 1001)
public abstract class ServerChunkManagerMixin extends ChunkSource implements IMutableMainThread {
    @Shadow @Mutable Thread mainThread;
    @Shadow @Final ServerLevel level;

    @Override
    public Thread getMainThread() {
        return this.mainThread;
    }

    @Override
    public void setMainThread(Thread thread) {
        this.mainThread = thread;
    }

    @Redirect(method = "getChunkAt", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
    private Thread currentThread(int i, int j, ChunkStatus chunkstatus, boolean flag) {
        Thread thread = Thread.currentThread();

        if(DimThread.MANAGER.isActive(((WorldServer) (Object) this.level).getMinecraftServer()) && DimThread.owns(thread)) {
            return this.mainThread;
        }

        return thread;
    }
}
