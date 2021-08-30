package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.nearby_entity_tracking;

import me.rancraftplayz.pacifist.optimizations.lithium.common.NearbyEntityListenerMulti;
import me.rancraftplayz.pacifist.optimizations.lithium.common.NearbyEntityListenerProvider;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(PersistentEntitySectionManager.class)
public abstract class ServerEntityManagerMixin<T extends EntityAccess> {
    @Shadow
    @Final
    EntitySectionStorage<T> f;

    @Inject(method = "a(Lnet/minecraft/world/level/entity/EntityAccess;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityAccess;a(Lnet/minecraft/world/level/entity/EntityInLevelCallback;)V", shift = At.Shift.AFTER))
    private void onAddEntity(T var0, boolean var1, CallbackInfoReturnable<Boolean> cir) {
        NearbyEntityListenerMulti listener = ((NearbyEntityListenerProvider) var0).getListener();
        if (listener != null) {
            listener.forEachChunkInRangeChange(
                    this.f,
                    null,
                    SectionPos.of(var0.blockPosition())
            );
        }
    }
}
