package me.rancraftplayz.pacifist.optimizations.lithium.mixins.chunk.block_counting;

import me.rancraftplayz.pacifist.optimizations.lithium.common.BlockStateFlagHolder;
import me.rancraftplayz.pacifist.optimizations.lithium.common.BlockStateFlags;
import me.rancraftplayz.pacifist.optimizations.lithium.common.IndexedBlockStatePredicate;
import me.rancraftplayz.pacifist.optimizations.lithium.common.SectionFlagHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Keep track of how many blocks that meet certain criteria are in this chunk section.
 * E.g. if no over-sized blocks are there, collision code can skip a few blocks.
 *
 * @author 2No2Name
 */

@Mixin(LevelChunkSection.class)
public abstract class ChunkSectionMixin implements SectionFlagHolder {

    @Unique
    private short[] countsByFlag = new short[BlockStateFlags.NUM_FLAGS];

    @Override
    public boolean getFlag(IndexedBlockStatePredicate indexedBlockStatePredicate) {
        return this.countsByFlag[indexedBlockStatePredicate.getIndex()] != 0;
    }

    @Redirect(method = "recalcBlockCounts", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/DataPaletteBlock;a(Lnet/minecraft/world/level/chunk/DataPaletteBlock$a;)V"))
    private void initFlagCounters(PalettedContainer<BlockState> palettedContainer, PalettedContainer.CountConsumer<BlockState> consumer) {
        palettedContainer.count((state, count) -> {
            consumer.accept(state, count);

            int flags = ((BlockStateFlagHolder) state).getAllFlags();
            int size = this.countsByFlag.length;
            for (int i = 0; i < size && flags != 0; i++) {
                if ((flags & 1) != 0) {
                    this.countsByFlag[i] += count;
                }
                flags = flags >>> 1;
            }
        });
    }

    @Inject(method = "recalcBlockCounts", at = @At("HEAD"))
    private void resetFlagCounters(CallbackInfo ci) {
        this.countsByFlag = new short[BlockStateFlags.NUM_FLAGS];
    }

    @Inject(method = "setType(IIILnet/minecraft/world/level/block/state/IBlockData;Z)Lnet/minecraft/world/level/block/state/IBlockData;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/IBlockData;getFluid()Lnet/minecraft/world/level/material/Fluid;", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void updateFlagCounters(int x, int y, int z, BlockState newState, boolean lock, CallbackInfoReturnable<BlockState> cir, BlockState oldState) {
        int prevFlags = ((BlockStateFlagHolder) oldState).getAllFlags();
        int flags = ((BlockStateFlagHolder) newState).getAllFlags();

        //no need to update indices that did not change
        int flagsXOR = prevFlags ^ flags;
        int i;
        while ((i = Integer.numberOfTrailingZeros(flagsXOR)) < 32) {
            //either count up by one (prevFlag not set) or down by one (prevFlag set)
            this.countsByFlag[i] += 1 - (((prevFlags >>> i) & 1) << 1);
            flagsXOR &= ~(1 << i);
        }
    }
}
