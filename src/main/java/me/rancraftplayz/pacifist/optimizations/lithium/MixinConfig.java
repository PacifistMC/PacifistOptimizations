package me.rancraftplayz.pacifist.optimizations.lithium;

import me.rancraftplayz.pacifist.optimizations.common.config.PacifistConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

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
        if (mixinClassName.equals("me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.goal.GoalSelectorMixin")) {
            try {
                Class.forName("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet");
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        if (mixinClassName.equals("me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.goal.GoalSelectorMixinPaperMC")) {
            try {
                Class.forName("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet");
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
