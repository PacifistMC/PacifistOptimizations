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
            if (!PacifistConfig.isItCursed()) {
                return false;
            }
        }
        if (mixinClassName.equals("me.rancraftplayz.pacifist.optimizations.lithium.mixins.entity.data_tracker.use_arrays.DataWatcherMixinPaperMC")) {
            if (PacifistConfig.isItCursed()) {
                return false;
            }
        }
        if (mixinClassName.equals("me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.goal.GoalSelectorMixin")) {
            if (!PacifistConfig.isItCursed()) {
                return false;
            }
        }
        if (mixinClassName.equals("me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.goal.GoalSelectorMixinPaperMC")) {
            if (PacifistConfig.isItCursed()) {
                return false;
            }
        }
        if (mixinClassName.equals("me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.poi.fast_retrieval.SerializingRegionBasedStorageMixin")) {
            if (!PacifistConfig.isItCursed()) {
                return false;
            }
        }
        if (mixinClassName.equals("me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.poi.fast_retrieval.SerializingRegionBasedStorageMixinPaperMC")) {
            if (PacifistConfig.isItCursed()) {
                return false;
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
