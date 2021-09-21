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

    /**
     * Spigot
     */
    @Property(
            type = PropertyType.SWITCH,
            name = "Disable Spigot AsyncCatcher",
            category = "Server-Settings",
            subcategory = "Spigot"
    )
    public static boolean isAsyncCatcherDisabled = true;

    /**
     * Dimensional Threading
     */
    @Property(
            type = PropertyType.SWITCH,
            name = "Dimensional Threading",
            category = "Optimizations",
            subcategory = "Dimensional Threading"
    )
    public static boolean dimthread = true;
    public static int dimthread_threadcount = 3;


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
