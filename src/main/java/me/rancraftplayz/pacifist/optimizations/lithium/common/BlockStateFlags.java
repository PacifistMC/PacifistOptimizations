package me.rancraftplayz.pacifist.optimizations.lithium.common;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

public class BlockStateFlags {
    public static final boolean ENABLED = SectionFlagHolder.class.isAssignableFrom(LevelChunkSection.class);
    public static final int NUM_FLAGS = 2; //Update this number when adding a new flag!

    public static final IndexedBlockStatePredicate OVERSIZED_SHAPE = new IndexedBlockStatePredicate() {
        @Override
        public boolean test(BlockState operand) {
            return ((IBlockData) (Object) operand).d();
        }
    };
    public static final IndexedBlockStatePredicate PATH_NOT_OPEN = new IndexedBlockStatePredicate() {
        @Override
        public boolean test(BlockState operand) {
            return PathNodeCache.getNeighborPathNodeType(operand) != BlockPathTypes.OPEN;
        }
    };
}
