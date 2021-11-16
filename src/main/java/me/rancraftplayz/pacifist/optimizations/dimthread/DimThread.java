package me.rancraftplayz.pacifist.optimizations.dimthread;

import gg.essential.api.utils.Multithreading;
import me.rancraftplayz.pacifist.optimizations.common.config.PacifistConfig;
import me.rancraftplayz.pacifist.optimizations.dimthread.thread.IMutableMainThread;
import me.rancraftplayz.pacifist.optimizations.dimthread.utils.ServerManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import wearblackallday.util.ThreadPool;

/**
 * Original code from DimensionalThreading (https://github.com/WearBlackAllDay/DimensionalThreading)
 * Licensed under the MIT License
 */

public class DimThread {
    public static final String MOD_ID = "DimThread";
    public static final ServerManager MANAGER = new ServerManager();

    public static void autoUpdate() {
        // TODO: Have a good implementation of this without wasting computer's resources
        Multithreading.runAsync(() -> {
            try {
                while(true) {
                    if (Bukkit.getServer().getWorlds().size() >= 1 && Bukkit.getServer().getWorlds().size() != PacifistConfig.dimthread_threadcount) {
                        if (!MANAGER.threadPools.isEmpty()) {
                            MANAGER.setThreadCountAll(Bukkit.getServer().getWorlds().size());
                        }
                    }
                }
            } catch (Exception nothingToSeeHere) {
                autoUpdate();
            }
        });
    }

    public static ThreadPool getThreadPool(MinecraftServer server) {
        return MANAGER.getThreadPool(server);
    }

    public static void swapThreadsAndRun(Runnable task, Object... threadedObjects) {
        Thread currentThread = Thread.currentThread();
        Thread[] oldThreads = new Thread[threadedObjects.length];

        for(int i = 0; i < oldThreads.length; i++) {
            oldThreads[i] = ((IMutableMainThread)threadedObjects[i]).getMainThread();
            ((IMutableMainThread)threadedObjects[i]).setMainThread(currentThread);
        }

        task.run();

        for(int i = 0; i < oldThreads.length; i++) {
            ((IMutableMainThread)threadedObjects[i]).setMainThread(oldThreads[i]);
        }
    }

    /**
     * Makes it easy to understand what is happening in crash reports and helps identify dimthread workers.
     * */
    public static void attach(Thread thread, String name) {
        thread.setName(MOD_ID + "_" + name);
    }

    public static void attach(Thread thread, ServerLevel world) {
        attach(thread, world.dimension().location().getPath());
    }

    /**
     * Checks if the given thread is a dimthread worker by checking the name. Probably quite fragile...
     * */
    public static boolean owns(Thread thread) {
        return thread.getName().startsWith(MOD_ID);
    }
}
