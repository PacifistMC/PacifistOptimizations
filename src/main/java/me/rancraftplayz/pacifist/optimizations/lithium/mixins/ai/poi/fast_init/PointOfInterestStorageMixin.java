package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.poi.fast_init;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import me.rancraftplayz.pacifist.optimizations.lithium.common.PointOfInterestTypeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiSection;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Mixin(PoiManager.class)
public abstract class PointOfInterestStorageMixin extends SectionStorage<PoiSection> {
    public PointOfInterestStorageMixin(File var0, Function<Runnable, Codec<PoiSection>> var1, Function<Runnable, PoiSection> var2, DataFixer var3, DataFixTypes var4, boolean var5, LevelHeightAccessor var6) {
        super(var0, var1, var2, var3, var4, var5, var6);
    }

    @Shadow
    protected abstract void updateFromSection(LevelChunkSection var0, SectionPos var1, BiConsumer<BlockPos, PoiType> var2);

    /**
     * @reason Avoid Stream API
     * @author Jellysquid
     */
    @Overwrite
    public void checkConsistencyWithBlocks(ChunkPos chunkPos_1, LevelChunkSection section) {
        SectionPos sectionPos = SectionPos.of(chunkPos_1, section.bottomBlockY() >> 4);

        PoiSection set;
        try {
            set = this.get(sectionPos.asLong()).orElse(null);
        } catch (NullPointerException yoo) {
            set = null;
        }

        if (set != null) {
            set.refresh((consumer) -> {
                if (PointOfInterestTypeHelper.shouldScan(section)) {
                    this.updateFromSection(section, sectionPos, consumer);
                }
            });
        } else {
            if (PointOfInterestTypeHelper.shouldScan(section)) {
                set = this.getOrCreate(sectionPos.asLong());

                this.updateFromSection(section, sectionPos, set::add);
            }
        }
    }
}
