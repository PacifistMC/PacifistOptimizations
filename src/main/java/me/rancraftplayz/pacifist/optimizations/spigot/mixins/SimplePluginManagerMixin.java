package me.rancraftplayz.pacifist.optimizations.spigot.mixins;

import org.bukkit.event.Event;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimplePluginManager.class)
public abstract class SimplePluginManagerMixin {
    @Shadow protected abstract void fireEvent(@NotNull Event event);

    /**
     * @author RanCraftPlayz
     * @reason Vaporizing bukkit at 3 AM
     */
    @Overwrite
    public void callEvent(@NotNull Event event) {
        this.fireEvent(event);
    }
}
