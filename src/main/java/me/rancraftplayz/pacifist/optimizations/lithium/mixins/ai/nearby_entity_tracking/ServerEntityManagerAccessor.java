package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.nearby_entity_tracking;

import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(PersistentEntitySectionManager.class)
public interface ServerEntityManagerAccessor<T extends EntityAccess> {
    @Accessor("f")
    EntitySectionStorage<T> getf();
}
