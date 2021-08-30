package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.nearby_entity_tracking;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(ServerLevel.class)
public interface ServerWorldAccessor {
    @Accessor("G")
    PersistentEntitySectionManager<Entity> getEntityManager();
}
