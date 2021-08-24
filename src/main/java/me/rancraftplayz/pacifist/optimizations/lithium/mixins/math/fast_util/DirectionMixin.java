package me.rancraftplayz.pacifist.optimizations.lithium.mixins.math.fast_util;

import net.minecraft.core.Direction;
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
    @Shadow(aliases = "h")
    private int oppositeIndex;

    @Shadow(aliases = "o")
    @Final
    private static Direction[] VALUES;

    /**
     * @reason Avoid the modulo/abs operations
     * @author JellySquid
     */
    @Overwrite
    public Direction opposite() {
        return VALUES[oppositeIndex];
    }

    /**
     * @reason Do not allocate an excessive number of Direction arrays
     * @author JellySquid
     */
    @Overwrite
    public static Direction a(Random rand) {
        return VALUES[rand.nextInt(VALUES.length)];
    }
}
