package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.poi.fast_retrieval;

import me.rancraftplayz.pacifist.optimizations.lithium.common.Collector;
import me.rancraftplayz.pacifist.optimizations.lithium.common.PointOfInterestSetFilterable;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiSection;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(PoiSection.class)
public class PointOfInterestSetMixin implements PointOfInterestSetFilterable {
    @Shadow
    @Final
    private Map<PoiType, Set<PoiRecord>> byType;

    @Override
    public boolean get(Predicate<PoiType> type, PoiManager.Occupancy status, Collector<PoiRecord> consumer) {
        for (Map.Entry<PoiType, Set<PoiRecord>> entry : this.byType.entrySet()) {
            if (!type.test(entry.getKey())) {
                continue;
            }

            for (PoiRecord poi : entry.getValue()) {
                if (!status.getTest().test(poi)) {
                    continue;
                }

                if (!consumer.collect(poi)) {
                    return false;
                }
            }
        }
        return true;
    }
}
