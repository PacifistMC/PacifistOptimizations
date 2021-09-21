package me.rancraftplayz.pacifist.optimizations.spigot.mixins;

import org.spigotmc.AsyncCatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Use a better way to modify this field
@Mixin(AsyncCatcher.class)
public abstract class AsyncCatcherMixin {
    @Shadow public static boolean enabled;

    @Inject(method = "catchOp", at = @At("HEAD"))
    private static void itsAMeSpigot(String reason, CallbackInfo ci) {
        enabled = false;
    }
}
