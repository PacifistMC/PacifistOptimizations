package me.rancraftplayz.pacifist.optimizations.lithium.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiSection;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.function.Predicate;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

public class PointOfInterestCollectors {
    public static Collector<PoiRecord> collectAllWithinRadius(BlockPos pos, double radius, Collector<PoiRecord> out) {
        double radiusSq = radius * radius;

        return (point) -> {
            if (point.getPos().distSqr(pos) <= radiusSq) {
                return out.collect(point);
            }

            return true;
        };
    }

    public static Collector<PoiSection> collectAllMatching(Predicate<PoiType> predicate, PoiManager.Occupancy status, Collector<PoiRecord> out) {
        return (set) -> ((PointOfInterestSetFilterable) set).get(predicate, status, out);
    }
}
