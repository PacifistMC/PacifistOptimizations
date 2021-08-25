package me.rancraftplayz.pacifist.optimizations.lithium.common;

import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

public abstract class PathNodeCache {
    private static boolean isChunkSectionDangerousNeighbor(LevelChunkSection section) {
        return section.getStates().maybeHas(state -> getNeighborPathNodeType(state) != BlockPathTypes.OPEN);
    }

    public static BlockPathTypes getPathNodeType(BlockState state) {
        return ((BlockStatePathingCache) state).getPathNodeType();
    }

    public static BlockPathTypes getNeighborPathNodeType(BlockBehaviour.BlockStateBase state) {
        return ((BlockStatePathingCache) state).getNeighborPathNodeType();
    }

    /**
     * Returns whether or not a chunk section is free of dangers. This makes use of a caching layer to greatly
     * accelerate neighbor danger checks when path-finding.
     *
     * @param section The chunk section to test for dangers
     * @return True if this neighboring section is free of any dangers, otherwise false if it could
     * potentially contain dangers
     */
    public static boolean isSectionSafeAsNeighbor(LevelChunkSection section) {
        // Empty sections can never contribute a danger
        if (LevelChunkSection.isEmpty(section)) {
            return true;
        }

        if (BlockStateFlags.ENABLED) {
            return !((SectionFlagHolder) section).getFlag(BlockStateFlags.PATH_NOT_OPEN);
        }
        return !isChunkSectionDangerousNeighbor(section);
    }
}
