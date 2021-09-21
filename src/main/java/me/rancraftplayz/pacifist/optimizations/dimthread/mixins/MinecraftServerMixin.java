package me.rancraftplayz.pacifist.optimizations.dimthread.mixins;

import me.rancraftplayz.pacifist.optimizations.dimthread.DimThread;
import me.rancraftplayz.pacifist.optimizations.dimthread.utils.CrashInfo;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wearblackallday.util.ThreadPool;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

/**
 * Original code from DimensionalThreading (https://github.com/WearBlackAllDay/DimensionalThreading)
 * Licensed under the MIT License
 */

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow private int tickCount;
    @Shadow private PlayerList playerList;
    @Shadow public abstract Iterable<ServerLevel> getAllLevels();

    /**
     * Returns an empty iterator to stop {@code MinecraftServer#tickWorlds} from ticking
     * dimensions. This behaviour is overwritten below.
     *
     * @see MinecraftServerMixin#tickWorlds(BooleanSupplier, CallbackInfo)
     * */
    @ModifyVariable(method = "tickChildren", at = @At(value = "INVOKE_ASSIGN",
            target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;", ordinal = 0))
    public Iterator<?> tickWorlds(Iterator<?> oldValue) {
        return DimThread.MANAGER.isActive((MinecraftServer)(Object)this) ? Collections.emptyIterator() : oldValue;
    }

    /**
     * Distributes world ticking over 3 worker threads (one for each dimension) and waits until
     * they are all complete.
     * */
    @Inject(method = "tickChildren", at = @At(value = "INVOKE",
            target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;"))
    public void tickWorlds(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(!DimThread.MANAGER.isActive((MinecraftServer)(Object)this))return;

        AtomicReference<CrashInfo> crash = new AtomicReference<>();
        ThreadPool pool = DimThread.getThreadPool((MinecraftServer)(Object)this);

        pool.execute(this.getAllLevels().iterator(), serverWorld -> {
            DimThread.attach(Thread.currentThread(), serverWorld);

            if(this.tickCount % 20 == 0) {
                ClientboundSetTimePacket timeUpdatePacket = new ClientboundSetTimePacket(
                        serverWorld.getGameTime(), serverWorld.getDayTime(),
                        serverWorld.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT));

                this.playerList.broadcastAll(timeUpdatePacket, serverWorld.dimension());
            }

            DimThread.swapThreadsAndRun(() -> {
                try {
                    serverWorld.tick(shouldKeepTicking);
                } catch(Throwable throwable) {
                    crash.set(new CrashInfo(serverWorld, throwable));
                }
            }, serverWorld, serverWorld.getChunkProvider());
        });

        pool.awaitCompletion();

        if(crash.get() != null) {
            crash.get().crash("Exception ticking world");
        }
    }

    /**
     * Shutdown all threadpools when the server stop.
     * Prevent server hang when stopping the server.
     * */
    @Inject(method = "stopServer", at = @At("HEAD"))
    public void shutdownThreadpool(CallbackInfo ci) {
        DimThread.MANAGER.threadPools.forEach((server, pool) -> pool.shutdown());
    }
}
