package me.rancraftplayz.pacifist.optimizations.lithium.common;

import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.function.Predicate;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

public interface PointOfInterestSetFilterable {
    boolean get(Predicate<PoiType> type, PoiManager.Occupancy status, Collector<PoiRecord> consumer);
}
