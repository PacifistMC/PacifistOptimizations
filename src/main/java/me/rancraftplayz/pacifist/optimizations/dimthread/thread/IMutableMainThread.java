package me.rancraftplayz.pacifist.optimizations.dimthread.thread;

/**
 * Original code from DimensionalThreading (https://github.com/WearBlackAllDay/DimensionalThreading)
 * Licensed under the MIT License
 */

public interface IMutableMainThread {
    Thread getMainThread();

    void setMainThread(Thread thread);
}
