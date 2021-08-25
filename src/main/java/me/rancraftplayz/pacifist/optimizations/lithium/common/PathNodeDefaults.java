package me.rancraftplayz.pacifist.optimizations.lithium.common;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.bukkit.block.data.BlockData;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

public class PathNodeDefaults {
    public static BlockPathTypes getNeighborNodeType(BlockBehaviour.BlockStateBase state) {
        if (state.isAir()) {
            return BlockPathTypes.OPEN;
        }

        // [VanillaCopy] LandPathNodeMaker#getNodeTypeFromNeighbors
        // Determine what kind of obstacle type this neighbor is
        if (state.is(Blocks.CACTUS)) {
            return BlockPathTypes.DANGER_CACTUS;
        } else if (state.is(Blocks.SWEET_BERRY_BUSH)) {
            return BlockPathTypes.DANGER_OTHER;
        } else if (WalkNodeEvaluator.isBurningBlock((BlockState) state)) {
            return BlockPathTypes.DANGER_FIRE;
        } else if (state.getFluidState().is(FluidTags.WATER)) {
            return BlockPathTypes.WATER_BORDER;
        } else {
            return BlockPathTypes.OPEN;
        }
    }

    public static BlockPathTypes getNodeType(BlockBehaviour.BlockStateBase state) {
        if (state.isAir()) {
            return BlockPathTypes.OPEN;
        }

        Block block = state.getBlock();
        Material material = state.getMaterial();

        if (state.is(BlockTags.TRAPDOORS) || state.is(Blocks.LILY_PAD) || state.is(Blocks.BIG_DRIPLEAF)) {
            return BlockPathTypes.TRAPDOOR;
        }

        if (state.is(Blocks.POWDER_SNOW)) {
            return BlockPathTypes.POWDER_SNOW;
        }

        if (state.is(Blocks.CACTUS)) {
            return BlockPathTypes.DAMAGE_CACTUS;
        }

        if (state.is(Blocks.SWEET_BERRY_BUSH)) {
            return BlockPathTypes.DAMAGE_OTHER;
        }

        if (state.is(Blocks.HONEY_BLOCK)) {
            return BlockPathTypes.STICKY_HONEY;
        }

        if (state.is(Blocks.COCOA)) {
            return BlockPathTypes.COCOA;
        }

        FluidState fluidState = state.getFluidState();
        if (fluidState.is(FluidTags.LAVA)) {
            return BlockPathTypes.LAVA;
        }

        if (WalkNodeEvaluator.isBurningBlock((BlockState) state)) {
            return BlockPathTypes.DAMAGE_FIRE;
        }

        if (DoorBlock.isWoodenDoor((BlockState) state) && !((BlockBase.BlockData) (Object) state).get(BlockDoor.b)) {
            return BlockPathTypes.DOOR_WOOD_CLOSED;
        }

        if ((block instanceof DoorBlock) && (material == Material.METAL) && !((BlockBase.BlockData) (Object) state).get(BlockDoor.b)) {
            return BlockPathTypes.DOOR_IRON_CLOSED;
        }

        if ((block instanceof DoorBlock) && ((BlockBase.BlockData) (Object) state).get(BlockDoor.b)) {
            return BlockPathTypes.DOOR_OPEN;
        }

        if (block instanceof BaseRailBlock) {
            return BlockPathTypes.RAIL;
        }

        if (block instanceof LeavesBlock) {
            return BlockPathTypes.LEAVES;
        }

        if (((Tag) BlockTags.FENCES).contains(block) || ((Tag) BlockTags.WALLS).contains(block) || ((block instanceof FenceGateBlock) && !((BlockBase.BlockData) (Object) state).get(BlockFenceGate.b))) {
            return BlockPathTypes.FENCE;
        }

        if (fluidState.is(FluidTags.WATER)) {
            return BlockPathTypes.WATER;
        }

        return BlockPathTypes.OPEN;
    }
}
