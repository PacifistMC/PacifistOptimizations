package me.rancraftplayz.pacifist.optimizations.lithium.common;

import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

/**
 * Allows for multiple listeners on an entity to be grouped under one logical listener. No guarantees are made about the
 * order of which each sub-listener will be notified.
 */
public class NearbyEntityListenerMulti implements NearbyEntityListener{
    private final List<NearbyEntityListener> listeners = new ArrayList<>(4);
    private Range6Int range = null;

    public void addListener(NearbyEntityListener listener) {
        if (this.range != null) {
            throw new IllegalStateException("Cannot add sublisteners after listening range was set!");
        }
        this.listeners.add(listener);
    }

    public void removeListener(NearbyEntityListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public Range6Int getChunkRange() {
        if (this.range != null) {
            return this.range;
        }
        return calculateRange();
    }
    private Range6Int calculateRange() {
        if (this.listeners.isEmpty()) {
            return this.range = EMPTY_RANGE;
        }
        int positiveX = -1;
        int positiveY = -1;
        int positiveZ = -1;
        int negativeX = 0;
        int negativeY = 0;
        int negativeZ = 0;

        for (NearbyEntityListener listener : this.listeners) {
            Range6Int chunkRange = listener.getChunkRange();
            positiveX = Math.max(chunkRange.positiveX(), positiveX);
            positiveY = Math.max(chunkRange.positiveY(), positiveY);
            positiveZ = Math.max(chunkRange.positiveZ(), positiveZ);
            negativeX = Math.max(chunkRange.negativeX(), negativeX);
            negativeY = Math.max(chunkRange.negativeY(), negativeY);
            negativeZ = Math.max(chunkRange.negativeZ(), negativeZ);

        }
        return this.range = new Range6Int(positiveX, positiveY, positiveZ, negativeX, negativeY, negativeZ);
    }

    @Override
    public void onEntityEnteredRange(Entity entity) {
        for (NearbyEntityListener listener : this.listeners) {
            listener.onEntityEnteredRange(entity);
        }
    }

    @Override
    public void onEntityLeftRange(Entity entity) {
        for (NearbyEntityListener listener : this.listeners) {
            listener.onEntityLeftRange(entity);
        }
    }

    @Override
    public String toString() {
        StringBuilder sublisteners = new StringBuilder();
        String comma = "";
        for (NearbyEntityListener listener : this.listeners) {
            sublisteners.append(comma).append(listener.toString());
            comma = ","; //trick to drop the first comma
        }

        return super.toString() + " with sublisteners: [" + sublisteners + "]";
    }
}
