package me.rancraftplayz.pacifist.optimizations.lithium.common;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

public class PointOfInterestTypeHelper {
    private static Predicate<BlockState> POI_BLOCKSTATE_PREDICATE;

    public static void init(Set<BlockState> types) {
        if (POI_BLOCKSTATE_PREDICATE != null) {
            throw new IllegalStateException("Already initialized");
        }

        POI_BLOCKSTATE_PREDICATE = types::contains;
    }

    public static boolean shouldScan(LevelChunkSection section) {
        return section.maybeHas(POI_BLOCKSTATE_PREDICATE);
    }
}
