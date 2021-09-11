package me.rancraftplayz.pacifist.optimizations.lithium.common;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

public interface Collector<T> {
    /**
     * Collects the passed object and performs additional processing on it, returning a flag as to whether or not
     * collection should continue.
     *
     * @return True if collection should continue, otherwise false.
     */
    boolean collect(T obj);
}
