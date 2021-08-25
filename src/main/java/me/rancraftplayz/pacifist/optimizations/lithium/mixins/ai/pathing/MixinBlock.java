package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.pathing;

import me.rancraftplayz.pacifist.optimizations.lithium.api.BlockPathingBehavior;
import me.rancraftplayz.pacifist.optimizations.lithium.common.PathNodeDefaults;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(Block.class)
public class MixinBlock implements BlockPathingBehavior {
    @Override
    public BlockPathTypes getPathNodeType(BlockState state) {
        return PathNodeDefaults.getNodeType(state);
    }

    @Override
    public BlockPathTypes getPathNodeTypeAsNeighbor(BlockState state) {
        return PathNodeDefaults.getNeighborNodeType(state);
    }
}
