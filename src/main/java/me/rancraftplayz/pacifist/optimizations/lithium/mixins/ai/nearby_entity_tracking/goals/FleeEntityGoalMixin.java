package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.nearby_entity_tracking.goals;

import me.rancraftplayz.pacifist.optimizations.lithium.common.NearbyEntityListenerProvider;
import me.rancraftplayz.pacifist.optimizations.lithium.common.NearbyEntityTracker;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(PathfinderGoalAvoidTarget.class)
public class FleeEntityGoalMixin<T extends LivingEntity> {
    @Shadow
    @Final
    protected PathfinderMob a;

    @Shadow
    @Final
    protected float c;

    private NearbyEntityTracker<T> tracker;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityCreature;Ljava/lang/Class;Ljava/util/function/Predicate;FDDLjava/util/function/Predicate;)V", at = @At("RETURN"))
    private void init(PathfinderMob var0, Class<T> var1, Predicate<LivingEntity> var2, float var3, double var4, double var6, Predicate<LivingEntity> var8, CallbackInfo ci) {
        EntityDimensions dimensions = ((EntityType<?>) ((Entity) this.a).getType()).getDimensions();
        double adjustedRange = dimensions.width * 0.5D + this.c + 2D;
        int horizontalRange = Mth.ceil(adjustedRange);
        this.tracker = new NearbyEntityTracker<>(var1, var0, new Vec3i(horizontalRange, Mth.ceil(dimensions.height + 3 + 2), horizontalRange));

        ((NearbyEntityListenerProvider) var0).addListener(this.tracker);
    }

    @Redirect(method = "a", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/World;a(Ljava/util/List;Lnet/minecraft/world/entity/ai/targeting/PathfinderTargetCondition;Lnet/minecraft/world/entity/EntityLiving;DDD)Lnet/minecraft/world/entity/EntityLiving;"))
    private T redirectGetNearestEntity(Level world, List<? extends T> var0, TargetingConditions var1, LivingEntity var2, double var3, double var5, double var7) {
        return this.tracker.getClosestEntity(this.a.getBoundingBoxForCulling().expandTowards(this.c, 3.0D, this.c), var1, var3, var5, var7);
    }

    @Redirect(method = "a", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/World;a(Ljava/lang/Class;Lnet/minecraft/world/phys/AxisAlignedBB;Ljava/util/function/Predicate;)Ljava/util/List;"))
    private <R extends Entity> List<R> redirectGetEntities(Level world, Class<T> var0, AABB var1, Predicate<? super T> var2) {
        return null;
    }
}
