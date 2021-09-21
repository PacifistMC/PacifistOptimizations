package me.rancraftplayz.pacifist.optimizations.dimthread.utils;

import me.rancraftplayz.pacifist.optimizations.common.config.PacifistConfig;
import net.minecraft.server.MinecraftServer;
import wearblackallday.util.ThreadPool;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Original code from DimensionalThreading (https://github.com/WearBlackAllDay/DimensionalThreading)
 * Licensed under the MIT License
 */

public class ServerManager {
    private final Map<MinecraftServer, Boolean> actives = Collections.synchronizedMap(new WeakHashMap<>());
    public final Map<MinecraftServer, ThreadPool> threadPools = Collections.synchronizedMap(new WeakHashMap<>());

    public boolean isActive(MinecraftServer server) {
        PacifistConfig.getOrCreateConfig();
        return this.actives.computeIfAbsent(server, s -> PacifistConfig.dimthread);
    }

    public void setActive(MinecraftServer server, boolean value) {
        this.actives.put(server, value);
    }

    public ThreadPool getThreadPool(MinecraftServer server) {
        return this.threadPools.computeIfAbsent(server, s -> new ThreadPool(PacifistConfig.dimthread_threadcount));
    }

    public void setThreadCount(MinecraftServer server, int value) {
        ThreadPool current = this.threadPools.get(server);

        if(current.getActiveCount() != 0) {
            throw new ConcurrentModificationException("Setting the thread count in wrong phase");
        }

        this.threadPools.put(server, new ThreadPool(value));
        current.shutdown();
    }

    public void setThreadCountAll(int value) {
        for (MinecraftServer server : this.threadPools.keySet()) {
            setThreadCount(server, value);
        }
        PacifistConfig.dimthread_threadcount = value;
    }
}
