package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.pathing;

import me.rancraftplayz.pacifist.optimizations.common.config.PacifistConfig;
import net.minecraft.world.level.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(PathNavigationRegion.class)
public abstract class ChunkCacheMixin implements BlockGetter {
    private static final BlockState DEFAULT_BLOCK = ((Block) Blocks.AIR).defaultBlockState();

    @Shadow
    @Final
    protected ChunkAccess[][] c; //chunks

    @Shadow
    @Final
    protected int a; //centerX

    @Shadow
    @Final
    protected int b; //centerZ

    @Shadow
    @Final
    protected Level e; //level

    // A 1D view of the chunks available to this cache
    private ChunkAccess[] chunksFlat;

    // The x/z length of this cache
    private int xLen, zLen;

    private int bottomY, topY;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Level world, BlockPos minPos, BlockPos maxPos, CallbackInfo ci) {
        this.xLen = 1 + (maxPos.getX() >> 4) - (minPos.getX() >> 4);
        this.zLen = 1 + (maxPos.getZ() >> 4) - (minPos.getZ() >> 4);

        this.chunksFlat = new ChunkAccess[this.xLen * this.zLen];

        // Flatten the 2D chunk array into our 1D array
        for (int x = 0; x < this.xLen; x++) {
            System.arraycopy(this.c[x], 0, this.chunksFlat, x * this.zLen, this.zLen);
        }

        this.bottomY = ((IBlockAccess) (Object) this).getMinBuildHeight();
        this.topY = ((IBlockAccess) (Object) this).getHeight();
    }

    /**
     * @reason Use optimized function
     * @author JellySquid
     */
    @Overwrite
    public BlockState getType(BlockPos pos) {
        int y = pos.getY();

        if (!(y < this.bottomY || y >= this.topY)) {
            int x = pos.getX();
            int z = pos.getZ();

            int chunkX = (x >> 4) - this.a;
            int chunkZ = (z >> 4) - this.b;

            if (chunkX >= 0 && chunkX < this.xLen && chunkZ >= 0 && chunkZ < this.zLen) {
                ChunkAccess chunk = this.chunksFlat[(chunkX * this.zLen) + chunkZ];

                // Avoid going through Chunk#getBlockState
                if (chunk != null) {
                    ChunkSection section = ((IChunkAccess) (Object) chunk).getSections()[y >> 4];

                    if (section != null) {
                        return ((LevelChunkSection) (Object) section).getBlockState(x & 15, y & 15, z & 15);
                    }
                }
            }
        }

        return DEFAULT_BLOCK;
    }

    /**
     * @reason Use optimized function
     * @author JellySquid
     */
    @Overwrite
    public FluidState getFluid(BlockPos pos) {
        return this.getType(pos).getFluidState();
    }
}
