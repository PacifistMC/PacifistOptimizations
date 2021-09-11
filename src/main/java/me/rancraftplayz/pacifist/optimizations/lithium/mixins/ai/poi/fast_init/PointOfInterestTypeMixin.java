package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.poi.fast_init;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.rancraftplayz.pacifist.optimizations.lithium.common.PointOfInterestTypeHelper;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(PoiType.class)
public class PointOfInterestTypeMixin {
    @Shadow
    @Final
    @Mutable
    private static Map<BlockState, PoiType> TYPE_BY_STATE;

    static {
        TYPE_BY_STATE = new Reference2ReferenceOpenHashMap<>(TYPE_BY_STATE);

        PointOfInterestTypeHelper.init(TYPE_BY_STATE.keySet());
    }
}
