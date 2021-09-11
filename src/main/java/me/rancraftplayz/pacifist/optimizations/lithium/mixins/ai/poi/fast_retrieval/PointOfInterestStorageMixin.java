package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.poi.fast_retrieval;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import me.rancraftplayz.pacifist.optimizations.lithium.common.Collector;
import me.rancraftplayz.pacifist.optimizations.lithium.common.PointOfInterestCollectors;
import me.rancraftplayz.pacifist.optimizations.lithium.common.Pos;
import me.rancraftplayz.pacifist.optimizations.lithium.common.RegionBasedStorageSectionAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiSection;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(PoiManager.class)
public abstract class PointOfInterestStorageMixin extends SectionStorage<PoiSection> {
    public PointOfInterestStorageMixin(File var0, Function<Runnable, Codec<PoiSection>> var1, Function<Runnable, PoiSection> var2, DataFixer var3, DataFixTypes var4, boolean var5, LevelHeightAccessor var6) {
        super(var0, var1, var2, var3, var4, var5, var6);
    }

    /**
     * @reason Retrieve all points of interest in one operation
     * @author JellySquid
     */
    @SuppressWarnings("unchecked")
    @VisibleForDebug
    @Overwrite
    public Stream<PoiRecord> getInChunk(Predicate<PoiType> predicate, ChunkPos pos, PoiManager.Occupancy status) {
        return ((RegionBasedStorageSectionAccess<PoiSection>) this)
                .getWithinChunkColumn(pos.x, pos.z)
                .flatMap((set) -> set.getRecords(predicate, status));
    }

    /**
     * @reason Retrieve all points of interest in one operation
     * @author JellySquid
     */
    @Overwrite
    public Optional<BlockPos> getRandom(Predicate<PoiType> typePredicate, Predicate<BlockPos> posPredicate, PoiManager.Occupancy status, BlockPos pos, int radius, Random rand) {
        List<PoiRecord> list = this.getAllWithinCircle(typePredicate, pos, radius, status);

        Collections.shuffle(list, rand);

        for (PoiRecord point : list) {
            if (posPredicate.test(point.getPos())) {
                return Optional.of(point.getPos());
            }
        }

        return Optional.empty();
    }

    /**
     * @reason Avoid stream-heavy code, use a faster iterator and callback-based approach
     * @author JellySquid
     */
    @Overwrite
    public Optional<BlockPos> findClosest(Predicate<PoiType> predicate, BlockPos pos, int radius, PoiManager.Occupancy status) {
        List<PoiRecord> points = this.getAllWithinCircle(predicate, pos, radius, status);

        BlockPos nearest = null;
        double nearestDistance = Double.POSITIVE_INFINITY;

        for (PoiRecord point : points) {
            double distance = point.getPos().distSqr(pos);

            if (distance < nearestDistance) {
                nearest = point.getPos();
                nearestDistance = distance;
            }
        }

        return Optional.ofNullable(nearest);
    }

    /**
     * @reason Avoid stream-heavy code, use a faster iterator and callback-based approach
     * @author JellySquid
     */
    @Overwrite
    public long getCountInRange(Predicate<PoiType> predicate, BlockPos pos, int radius, PoiManager.Occupancy status) {
        return this.getAllWithinCircle(predicate, pos, radius, status).size();
    }

    private List<PoiRecord> getAllWithinCircle(Predicate<PoiType> predicate, BlockPos pos, int radius, PoiManager.Occupancy status) {
        List<PoiRecord> points = new ArrayList<>();

        this.collectWithinCircle(predicate, pos, radius, status, points::add);

        return points;
    }

    private void collectWithinCircle(Predicate<PoiType> predicate, BlockPos pos, int radius, PoiManager.Occupancy status, Collector<PoiRecord> collector) {
        Collector<PoiRecord> filter = PointOfInterestCollectors.collectAllWithinRadius(pos, radius, collector);
        Collector<PoiSection> consumer = PointOfInterestCollectors.collectAllMatching(predicate, status, filter);

        int minChunkX = Pos.ChunkCoord.fromBlockCoord((pos.getX() - radius - 1));
        int minChunkZ = Pos.ChunkCoord.fromBlockCoord((pos.getZ() - radius - 1));

        int maxChunkX = Pos.ChunkCoord.fromBlockCoord((pos.getX() + radius + 1));
        int maxChunkZ = Pos.ChunkCoord.fromBlockCoord((pos.getZ() + radius + 1));

        // noinspection unchecked
        RegionBasedStorageSectionAccess<PoiSection> storage = ((RegionBasedStorageSectionAccess<PoiSection>) this);

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                if (!storage.collectWithinChunkColumn(x, z, consumer)) {
                    return;
                }
            }
        }
    }
}
