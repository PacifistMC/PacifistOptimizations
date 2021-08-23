package me.rancraftplayz.pacifist.optimizations.lithium;

import me.rancraftplayz.pacifist.optimizations.common.PacifistOptimizations;
import me.rancraftplayz.pacifist.optimizations.common.config.PacifistConfig;
import me.rancraftplayz.pacifist.optimizations.common.config.PacifistInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.configurate.CommentedConfigurationNode;
import space.vectrix.ignite.api.config.Configuration;
import space.vectrix.ignite.api.config.Configurations;

import java.util.List;
import java.util.Set;

public class MixinConfig implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        PacifistConfig config = PacifistConfig.getOrCreateConfig();
        if (PacifistConfig.disableUnsafeMixinsPaper
                && mixinClassName.equals("me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.goal.PathfinderGoalSelectorMixin")) {
            return false;
        }
        if (mixinClassName.equals("me.rancraftplayz.pacifist.optimizations.lithium.mixins.entity.data_tracker.use_arrays.DataWatcherMixin")) {
            try {
                Class.forName("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMap");
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        if (mixinClassName.equals("me.rancraftplayz.pacifist.optimizations.lithium.mixins.entity.data_tracker.use_arrays.DataWatcherMixinPaperMC")) {
            try {
                Class.forName("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMap");
                return false;
            } catch (ClassNotFoundException ignored) {
            }
        }
        return PacifistConfig.lithium;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
