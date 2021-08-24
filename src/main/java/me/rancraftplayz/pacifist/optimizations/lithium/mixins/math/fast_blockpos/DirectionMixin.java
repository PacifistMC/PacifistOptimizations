package me.rancraftplayz.pacifist.optimizations.lithium.mixins.math.fast_blockpos;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(Direction.class)
public class DirectionMixin {
    private int offsetX, offsetY, offsetZ;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void reinit(String enumName, int ordinal, int var2, int var3, int var4, String var5, Direction.AxisDirection var6, Direction.Axis var7, Vec3i var8, CallbackInfo ci) {
        this.offsetX = var8.getX();
        this.offsetY = var8.getY();
        this.offsetZ = var8.getZ();
    }

    /**
     * @reason Avoid indirection to aid inlining
     * @author JellySquid
     */
    @Overwrite
    public int getAdjacentX() {
        return this.offsetX;
    }

    /**
     * @reason Avoid indirection to aid inlining
     * @author JellySquid
     */
    @Overwrite
    public int getAdjacentY() {
        return this.offsetY;
    }

    /**
     * @reason Avoid indirection to aid inlining
     * @author JellySquid
     */
    @Overwrite
    public int getAdjacentZ() {
        return this.offsetZ;
    }
}
