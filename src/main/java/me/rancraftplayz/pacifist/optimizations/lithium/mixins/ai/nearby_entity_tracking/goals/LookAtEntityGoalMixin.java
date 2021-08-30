package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.nearby_entity_tracking.goals;

import me.rancraftplayz.pacifist.optimizations.lithium.common.NearbyEntityListenerProvider;
import me.rancraftplayz.pacifist.optimizations.lithium.common.NearbyEntityTracker;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
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

@Mixin(PathfinderGoalLookAtPlayer.class)
public class LookAtEntityGoalMixin {
    @Shadow
    @Final
    protected Mob b;
    @Shadow
    @Final
    protected float d;
    private NearbyEntityTracker<? extends LivingEntity> tracker;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityInsentient;Ljava/lang/Class;FFZ)V", at = @At("RETURN"))
    private void init(Mob var0, Class<? extends LivingEntity> var1, float var2, float var3, boolean var4, CallbackInfo ci) {
        EntityDimensions dimensions = ((EntityType<?>) ((Entity) this.b).getType()).getDimensions();
        double adjustedRange = dimensions.width * 0.5D + this.d + 2D;
        int horizontalRange = Mth.ceil(adjustedRange);
        this.tracker = new NearbyEntityTracker<>(var1, var0, new Vec3i(horizontalRange, Mth.ceil(dimensions.height + 3 + 2), horizontalRange));

        ((NearbyEntityListenerProvider) var0).addListener(this.tracker);
    }

    @Redirect(method = "a", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/World;a(Ljava/util/List;Lnet/minecraft/world/entity/ai/targeting/PathfinderTargetCondition;Lnet/minecraft/world/entity/EntityLiving;DDD)Lnet/minecraft/world/entity/EntityLiving;"))
    private LivingEntity redirectGetNearestEntity(Level world, List<LivingEntity> var0, TargetingConditions var1, LivingEntity var2, double var3, double var5, double var7) {
        return this.tracker.getClosestEntity(this.b.getBoundingBox().expandTowards(this.d, 3.0D, this.d), var1, var3, var5, var7);
    }

    @Redirect(method = "a", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/World;a(Ljava/lang/Class;Lnet/minecraft/world/phys/AxisAlignedBB;Ljava/util/function/Predicate;)Ljava/util/List;"))
    private <R extends Entity> List<R> redirectGetEntities(Level world, Class<LivingEntity> var0, AABB var1, Predicate<? super R> var2) {
        return null;
    }

    @Redirect(method = "a", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/World;a(Lnet/minecraft/world/entity/ai/targeting/PathfinderTargetCondition;Lnet/minecraft/world/entity/EntityLiving;DDD)Lnet/minecraft/world/entity/player/EntityHuman;"))
    private Player redirectGetClosestPlayer(Level world, TargetingConditions var0, LivingEntity var1, double var2, double var4, double var6) {
        return (Player) this.tracker.getClosestEntity(null, var0, var2, var4, var6);
    }
}
