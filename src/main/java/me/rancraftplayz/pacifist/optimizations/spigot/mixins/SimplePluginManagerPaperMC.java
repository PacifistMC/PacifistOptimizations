package me.rancraftplayz.pacifist.optimizations.spigot.mixins;

import com.destroystokyo.paper.event.server.ServerExceptionEvent;
import com.destroystokyo.paper.exception.ServerEventException;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.logging.Level;

@Mixin(SimplePluginManager.class)
public abstract class SimplePluginManagerPaperMC {
    @Shadow @Final private Server server;

    /**
     * @author RanCraftPlayz
     * @reason Vaporizing bukkit at 3 AM
     */
    @Overwrite
    public void callEvent(@NotNull Event event) {
        HandlerList handlers = event.getHandlers();
        RegisteredListener[] listeners = handlers.getRegisteredListeners();
        RegisteredListener[] var4 = listeners;
        int var5 = listeners.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            RegisteredListener registration = var4[var6];
            if (registration.getPlugin().isEnabled()) {
                try {
                    registration.callEvent(event);
                } catch (AuthorNagException var10) {
                    Plugin plugin = registration.getPlugin();
                    if (plugin.isNaggable()) {
                        plugin.setNaggable(false);
                        this.server.getLogger().log(Level.SEVERE, String.format("Nag author(s): '%s' of '%s' about the following: %s", plugin.getDescription().getAuthors(), plugin.getDescription().getFullName(), var10.getMessage()));
                    }
                } catch (Throwable var11) {
                    String var10000 = event.getEventName();
                    String msg = "Could not pass event " + var10000 + " to " + registration.getPlugin().getDescription().getFullName();
                    this.server.getLogger().log(Level.SEVERE, msg, var11);
                    if (!(event instanceof ServerExceptionEvent)) {
                        this.callEvent(new ServerExceptionEvent(new ServerEventException(msg, var11, registration.getPlugin(), registration.getListener(), event)));
                    }
                }
            }
        }
    }
}
