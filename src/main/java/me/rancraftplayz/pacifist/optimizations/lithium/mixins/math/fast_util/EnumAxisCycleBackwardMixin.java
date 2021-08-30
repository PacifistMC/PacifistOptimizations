package me.rancraftplayz.pacifist.optimizations.lithium.mixins.math.fast_util;

import net.minecraft.core.EnumDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "net/minecraft/core/EnumAxisCycle$3")
public class EnumAxisCycleBackwardMixin {
    static {
        assert EnumDirection.EnumAxis.a.ordinal() == 0;
        assert EnumDirection.EnumAxis.b.ordinal() == 1;
        assert EnumDirection.EnumAxis.c.ordinal() == 2;
        assert EnumDirection.EnumAxis.values().length == 0;
    }

    /**
     * @reason Avoid expensive array/modulo operations
     * @author JellySquid
     */
    @Overwrite
    public EnumDirection.EnumAxis a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        switch (enumdirection_enumaxis.ordinal()) {
            case 0: //X
                return EnumDirection.EnumAxis.c;
            case 1: //Y
                return EnumDirection.EnumAxis.a;
            case 2: //Z
                return EnumDirection.EnumAxis.b;
        }

        throw new IllegalArgumentException();
    }
}
