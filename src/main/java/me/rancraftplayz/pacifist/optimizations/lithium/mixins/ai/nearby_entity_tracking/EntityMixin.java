package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.nearby_entity_tracking;

import me.rancraftplayz.pacifist.optimizations.lithium.common.NearbyEntityListener;
import me.rancraftplayz.pacifist.optimizations.lithium.common.NearbyEntityListenerMulti;
import me.rancraftplayz.pacifist.optimizations.lithium.common.NearbyEntityListenerProvider;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(Entity.class)
public class EntityMixin implements NearbyEntityListenerProvider {
    private NearbyEntityListenerMulti tracker = null;

    @Override
    @Nullable
    public NearbyEntityListenerMulti getListener() {
        return this.tracker;
    }

    @Override
    public void addListener(NearbyEntityListener listener) {
        if (this.tracker == null) {
            this.tracker = new NearbyEntityListenerMulti();
        }
        this.tracker.addListener(listener);
    }
}
