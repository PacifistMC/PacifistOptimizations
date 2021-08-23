package me.rancraftplayz.pacifist.optimizations.lithium.mixins.math.fastutil;

import net.minecraft.core.Direction;
import net.minecraft.core.EnumDirection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(Direction.class)
public class DirectionMixin {
    @Final
    @Shadow
    private int h;

    @Shadow
    @Final
    private static Direction[] o;

    /**
     * @reason Avoid the modulo/abs operations
     * @author JellySquid
     */
    @Overwrite
    public Direction opposite() {
        return o[h];
    }

    /**
     * @reason Do not allocate an excessive number of Direction arrays
     * @author JellySquid
     */
    @Overwrite
    public static Direction a(Random rand) {
        return o[rand.nextInt(o.length)];
    }
}
