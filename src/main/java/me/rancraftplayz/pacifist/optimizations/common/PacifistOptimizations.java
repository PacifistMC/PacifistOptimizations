package me.rancraftplayz.pacifist.optimizations.common;

import com.google.inject.Inject;
import me.rancraftplayz.pacifist.optimizations.common.config.PacifistConfig;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.ignite.api.Platform;
import space.vectrix.ignite.api.event.Subscribe;
import space.vectrix.ignite.api.event.platform.PlatformInitializeEvent;

public class PacifistOptimizations {
    private final Logger logger;
    private final Platform platform;
    private final PacifistConfig config = PacifistConfig.getOrCreateConfig();

    @Inject
    public PacifistOptimizations(Logger logger, Platform platform) {
        this.logger = logger;
        this.platform = platform;
    }

    @Subscribe
    public void onInitialize(final @NonNull PlatformInitializeEvent event) {
        this.logger.info("Using Pacifist Optimizations!");
        if (config != null) {
            this.logger.info("Lithium: " + (PacifistConfig.lithium ? "Enabled" : "Disabled"));
        }
    }
}
