package me.rancraftplayz.pacifist.optimizations.lithium.mixins.math.fastutil;

import net.minecraft.core.EnumDirection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(EnumDirection.class)
public abstract class EnumDirectionMixin {
    @Final
    @Shadow
    private int h;

    @Shadow
    public static EnumDirection[] values() {
        return new EnumDirection[0];
    }

    /**
     * @reason Avoid the modulo/abs operations
     * @author JellySquid
     */
    @Overwrite
    public EnumDirection opposite() {
        return values()[this.h];
    }

    /**
     * @reason Do not allocate an excessive number of Direction arrays
     * @author JellySquid
     */
    @Overwrite
    public static EnumDirection a(Random random) {
        return values()[random.nextInt(values().length)];
    }
}
