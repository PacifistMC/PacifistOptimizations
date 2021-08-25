package me.rancraftplayz.pacifist.optimizations.lithium.common;

import net.minecraft.world.level.pathfinder.BlockPathTypes;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

public interface BlockStatePathingCache {
    BlockPathTypes getPathNodeType();

    BlockPathTypes getNeighborPathNodeType();
}
