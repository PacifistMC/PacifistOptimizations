package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.math.fastutil;

import net.minecraft.core.EnumDirection;
import net.minecraft.world.phys.AxisAlignedBB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AxisAlignedBB.class)
public class BoxMixin {
    @Shadow @Final public double a;

    @Shadow @Final public double b;

    @Shadow @Final public double c;

    /**
     * @reason Simplify the code to better help the JVM optimize it
     * @author JellySquid
     */
    @Overwrite
    public double a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        switch (enumdirection_enumaxis) {
            case a:
                return this.a;
            case b:
                return this.b;
            case c:
                return this.c;
        }

        throw new IllegalArgumentException();
    }
}
