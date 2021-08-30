package me.rancraftplayz.pacifist.optimizations.lithium.mixins.chunk.block_counting;

import me.rancraftplayz.pacifist.optimizations.lithium.common.BlockStateFlagHolder;
import me.rancraftplayz.pacifist.optimizations.lithium.common.IndexedBlockStatePredicate;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin implements BlockStateFlagHolder {
    private int flags;

    @Inject(method = "a", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        this.initFlags();
    }

    private void initFlags() {
        IndexedBlockStatePredicate.FULLY_INITIALIZED.set(true);

        int flags = 0;

        for (int i = 0; i < IndexedBlockStatePredicate.ALL_FLAGS.length; i++) {
            if (IndexedBlockStatePredicate.ALL_FLAGS[i].test((BlockState) (Object) this)) {
                flags |= 1 << i;
            }
        }

        this.flags = flags;
    }

    @Override
    public int getAllFlags() {
        return this.flags;
    }
}
