package me.rancraftplayz.pacifist.optimizations.lithium.common;

import java.util.stream.Stream;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

public interface RegionBasedStorageSectionAccess<R> {
    /**
     * Fast-path for retrieving all items in a chunk column. This avoids needing to retrieve items for each sub-chunk
     * individually.
     *
     * @param chunkX The x-coordinate of the chunk column
     * @param chunkZ The z-coordinate of the chunk column
     */
    Stream<R> getWithinChunkColumn(int chunkX, int chunkZ);

    /**
     * Fast-path for collecting all items in a chunk column. This avoids needing to retrieve items for each sub-chunk
     * individually.
     *
     * @param chunkX The x-coordinate of the chunk column
     * @param chunkZ The z-coordinate of the chunk column
     * @return False if collection was interrupted by a downstream collector, otherwise true if all items were collected
     */
    boolean collectWithinChunkColumn(int chunkX, int chunkZ, Collector<R> consumer);
}
