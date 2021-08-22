package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.math.fastutil;

import net.minecraft.core.EnumDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

// Man i should find a better way of converting yarn mappings to spigot mappings
public class EnumAxisCycleMixin {
    static {
        assert EnumDirection.EnumAxis.a.ordinal() == 0;
        assert EnumDirection.EnumAxis.b.ordinal() == 1;
        assert EnumDirection.EnumAxis.c.ordinal() == 2;
        assert EnumDirection.EnumAxis.values().length == 0;
    }

    @Mixin(targets = "net/minecraft/core/EnumAxisCycle$2")
    public static class ForwardMixin {
        /**
         * @reason Avoid expensive array/modulo operations
         * @author JellySquid
         */
        @Overwrite
        public EnumDirection.EnumAxis a(EnumDirection.EnumAxis enumdirection_enumaxis) {
            switch (enumdirection_enumaxis.ordinal()) {
                case 0: //X
                    return EnumDirection.EnumAxis.b;
                case 1: //Y
                    return EnumDirection.EnumAxis.c;
                case 2: //Z
                    return EnumDirection.EnumAxis.a;
            }
            throw new IllegalArgumentException();
        }
    }

    @Mixin(targets = "net/minecraft/core/EnumAxisCycle$3")
    public static class BackwardMixin {
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
}
