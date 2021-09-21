package me.rancraftplayz.pacifist.optimizations.dimthread.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * Original code from DimensionalThreading (https://github.com/WearBlackAllDay/DimensionalThreading)
 * Licensed under the MIT License
 */

@Mixin(RedStoneWireBlock.class)
public abstract class RedstoneWireBlockMixin {
    @Shadow @Final
    public static IntegerProperty POWER;

    @Shadow @Final
    public static Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION;

    @Shadow
    protected abstract BlockState getConnectionState(BlockGetter iblockaccess, BlockState iblockdata, BlockPos blockposition);

    /**
     * {@code RedstoneWireBlock#wiresGivePower} is not thread-safe since it's a global flag. To ensure
     * no interference between threads the field is replaced with this thread local one.
     *
     * @see RedStoneWireBlock#isSignalSource(BlockState)
     * */
    private final ThreadLocal<Boolean> wiresGivePowerSafe = ThreadLocal.withInitial(() -> true);

    @Inject(method = "calculateTargetStrength", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBestNeighborSignal(Lnet/minecraft/core/BlockPos;)I",
            shift = At.Shift.BEFORE))
    private void getReceivedRedstonePowerBefore(Level world, BlockPos pos, CallbackInfoReturnable<Integer> ci) {
        this.wiresGivePowerSafe.set(false);
    }

    @Inject(method = "calculateTargetStrength", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBestNeighborSignal(Lnet/minecraft/core/BlockPos;)I",
            shift = At.Shift.AFTER))
    private void getReceivedRedstonePowerAfter(Level world, BlockPos pos, CallbackInfoReturnable<Integer> ci) {
        this.wiresGivePowerSafe.set(true);
    }

    /**
     * @author DimensionalThreading (WearBlackAllDay)
     * @reason Made redstone thread-safe, please inject in the caller.
     */
    @Overwrite
    public boolean isPowerSource(BlockState state) {
        return this.wiresGivePowerSafe.get();
    }

    /**
     * @author DimensionalThreading (WearBlackAllDay)
     * @reason Made redstone thread-safe, please inject in the caller.
     */
    @Overwrite
    public int getDirectSignal(BlockState iblockdata, BlockGetter iblockaccess, BlockPos blockposition, Direction enumdirection) {
        return !this.wiresGivePowerSafe.get() ? 0 : iblockdata.getSignal(iblockaccess, blockposition, enumdirection);
    }

    /**
     * @author DimensionalThreading (WearBlackAllDay)
     * @reason Made redstone thread-safe, please inject in the caller.
     */
    @Overwrite
    public int getSignal(BlockState iblockdata, BlockGetter iblockaccess, BlockPos blockposition, Direction enumdirection) {
        if(!this.wiresGivePowerSafe.get() || enumdirection == Direction.DOWN) {
            return 0;
        }

        int i = iblockdata.getValue(POWER);
        if(i == 0)return 0;
        return enumdirection != Direction.UP && !this.getConnectionState(iblockaccess, iblockdata, blockposition)
                .getValue(PROPERTY_BY_DIRECTION.get(enumdirection.getOpposite())).isConnected() ? 0 : i;
    }
}
