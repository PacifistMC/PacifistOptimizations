package me.rancraftplayz.pacifist.optimizations.common.config;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.io.File;

public class PacifistConfig extends Vigilant {
    @Property(
            type = PropertyType.SWITCH,
            name = "Lithium",
            category = "Optimizations",
            subcategory = "Lithium"
    )
    public static boolean lithium = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Disable Unsafe Mixins for PaperMC",
            description = "Only enable this if your server isn't running paper",
            category = "PaperMC"
    )
    public static boolean disableUnsafeMixinsPaper = true;

    public PacifistConfig() {
        super(new File("./configs/pacifist-optimizations.toml"));
        this.initialize();
    }

    private static PacifistConfig config;

    public static PacifistConfig getOrCreateConfig() {
        if (config == null) {
            PacifistConfig configg = new PacifistConfig();
            configg.preload();
            return config = configg;
        }
        return config;
    }
}
