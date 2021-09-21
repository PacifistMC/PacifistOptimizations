package me.rancraftplayz.pacifist.optimizations.lithium.mixins.common;

import net.minecraft.world.entity.ai.goal.Goal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.EnumSet;

@Mixin(Goal.class)
public interface GoalAccessorMixinPaperMC {
    @Accessor("a")
    EnumSet<Goal.Flag> getGoalTypes();
}
