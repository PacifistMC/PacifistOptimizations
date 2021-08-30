package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.nearby_entity_tracking;

import me.rancraftplayz.pacifist.optimizations.lithium.common.EntityTrackerSection;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(EntitySectionStorage.class)
public class SectionedEntityCacheMixin<T extends EntityAccess> {
    @Inject(method = "g", at = @At("RETURN"))
    private void rememberPos(long var0, CallbackInfoReturnable<T> cir) {
        ((EntityTrackerSection)cir.getReturnValue()).setPos(var0);
    }
}
