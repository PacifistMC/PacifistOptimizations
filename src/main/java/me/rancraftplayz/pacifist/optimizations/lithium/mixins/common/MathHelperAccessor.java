package me.rancraftplayz.pacifist.optimizations.lithium.mixins.common;

import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mth.class)
public interface MathHelperAccessor {
    @Accessor("o")
    public static float[] getSIN() {
        throw new AssertionError();
    }
}
