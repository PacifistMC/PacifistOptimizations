package me.rancraftplayz.pacifist.optimizations.spigot;

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
        PacifistConfig.getOrCreateConfig();
        if (mixinClassName.endsWith("SimplePluginManagerMixin")) {
            if (!PacifistConfig.isItCursed()) {
                return false;
            }
        }
        if (mixinClassName.endsWith("SimplePluginManagerPaperMC")) {
            if (PacifistConfig.isItCursed()) {
                return false;
            }
        }
        return PacifistConfig.isAsyncCatcherDisabled;
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
