package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.pathing;

import me.rancraftplayz.pacifist.optimizations.lithium.api.BlockPathingBehavior;
import me.rancraftplayz.pacifist.optimizations.lithium.common.BlockStatePathingCache;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin implements BlockStatePathingCache {
    @Shadow protected abstract BlockState q();

    @Shadow public abstract Block getBlock();

    private BlockPathTypes pathNodeType = BlockPathTypes.OPEN;
    private BlockPathTypes pathNodeTypeNeighbor = BlockPathTypes.OPEN;

    @Inject(method = "a", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        BlockState state = this.q();
        BlockPathingBehavior behavior = (BlockPathingBehavior) this.getBlock();

        this.pathNodeType = notNull(behavior.getPathNodeType(state));
        this.pathNodeTypeNeighbor = notNull(behavior.getPathNodeTypeAsNeighbor(state));
    }

    @Override
    public BlockPathTypes getPathNodeType() {
        return this.pathNodeType;
    }

    @Override
    public BlockPathTypes getNeighborPathNodeType() {
        return this.pathNodeTypeNeighbor;
    }

    private static <T> T notNull(T object) {
        return notNull(object, "The validated object is null");
    }

    private static <T> T notNull(T object, String message, Object... values) {
        if (object == null) {
            throw new NullPointerException(String.format(message, values));
        } else {
            return object;
        }
    }
}
