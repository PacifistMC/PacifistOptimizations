package me.rancraftplayz.pacifist.optimizations.common.config;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

public class PacifistConfig extends Vigilant {
    /**
     * Lithium
     */
    @Property(
            type = PropertyType.SWITCH,
            name = "Lithium",
            category = "Optimizations",
            subcategory = "Lithium"
    )
    public static boolean lithium = true;

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

    public static boolean isItCursed() {
        try {
            Class.forName("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMap");
            return true;
        } catch (ClassNotFoundException yoooo) {
            return false;
        }
    }
}
