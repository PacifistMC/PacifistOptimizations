package me.rancraftplayz.pacifist.optimizations.lithium.mixins.math.fast_util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(AABB.class)
public class BoxMixin {
    static {
        assert Direction.Axis.X.ordinal() == 0;
        assert Direction.Axis.Y.ordinal() == 1;
        assert Direction.Axis.Z.ordinal() == 2;
        assert Direction.Axis.values().length == 3;
    }

    @Shadow
    @Final
    public double a;

    @Shadow
    @Final
    public double b;

    @Shadow
    @Final
    public double c;

    @Shadow
    @Final
    public double d;

    @Shadow
    @Final
    public double e;

    @Shadow
    @Final
    public double f;

    /**
     * @reason Simplify the code to better help the JVM optimize it
     * @author JellySquid
     */
    @Overwrite
    public double a(Direction.Axis axis) {
        switch (axis.ordinal()) {
            case 0: //X
                return this.a;
            case 1: //Y
                return this.b;
            case 2: //Z
                return this.c;
        }

        throw new IllegalArgumentException();
    }

    /**
     * @reason Simplify the code to better help the JVM optimize it
     * @author JellySquid
     */
    @Overwrite
    public double b(Direction.Axis axis) {
        switch (axis.ordinal()) {
            case 0: //X
                return this.d;
            case 1: //Y
                return this.e;
            case 2: //Z
                return this.f;
        }

        throw new IllegalArgumentException();
    }
}
