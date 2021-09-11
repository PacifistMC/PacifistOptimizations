package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.poi.fast_retrieval;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import me.rancraftplayz.pacifist.optimizations.lithium.common.*;
import net.minecraft.core.SectionPos;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.BitSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(SectionStorage.class)
public abstract class SerializingRegionBasedStorageMixinPaperMC<R> implements RegionBasedStorageSectionAccess<R> {
    @Shadow(aliases = "e")
    @Final
    @Mutable
    private Long2ObjectMap<Optional<R>> loadedElements;

    @Shadow
    protected abstract Optional<R> get(long pos);

    @Shadow
    protected abstract void readColumn(ChunkPos pos);

    @Shadow
    @Final
    protected LevelHeightAccessor levelHeightAccessor;

    private Long2ObjectOpenHashMap<BitSet> columns;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(File var0, Function<Runnable, Codec<R>> var1, Function<Runnable, R> var2, DataFixer var3, DataFixTypes var4, boolean var5, LevelHeightAccessor var6, CallbackInfo ci) {
        this.columns = new Long2ObjectOpenHashMap<>();
        this.loadedElements = new ListeningLong2ObjectOpenHashMapPaperMC<>(this::onEntryAdded, this::onEntryRemoved);
    }

    private void onEntryRemoved(long key, Optional<R> value) {
        // NO-OP... vanilla never removes anything, leaking entries.
        // We might want to fix this.
    }

    private void onEntryAdded(long key, Optional<R> value) {
        int y = Pos.SectionYIndex.fromSectionCoord(this.levelHeightAccessor, SectionPos.y(key));

        // We only care about items belonging to a valid sub-chunk
        if (y < 0 || y >= Pos.SectionYIndex.getNumYSections(this.levelHeightAccessor)) {
            return;
        }

        int x = SectionPos.x(key);
        int z = SectionPos.z(key);

        long pos = ChunkPos.asLong(x, z);

        BitSet flags = this.columns.get(pos);

        if (flags == null) {
            this.columns.put(pos, flags = new BitSet(Pos.SectionYIndex.getNumYSections(this.levelHeightAccessor)));
        }

        flags.set(y, value.isPresent());
    }

    @Override
    public Stream<R> getWithinChunkColumn(int chunkX, int chunkZ) {
        BitSet flags = this.getCachedColumnInfo(chunkX, chunkZ);

        // No items are present in this column
        if (flags.isEmpty()) {
            return Stream.empty();
        }

        return flags.stream()
                .map(chunkYIndex -> Pos.SectionYCoord.fromSectionIndex(this.levelHeightAccessor, chunkYIndex))
                .mapToObj((chunkY) -> this.loadedElements.get(SectionPos.asLong(chunkX, chunkY, chunkZ)).orElse(null))
                .filter(Objects::nonNull);
    }

    @Override
    public boolean collectWithinChunkColumn(int chunkX, int chunkZ, Collector<R> consumer) {
        BitSet flags = this.getCachedColumnInfo(chunkX, chunkZ);

        // No items are present in this column
        if (flags.isEmpty()) {
            return true;
        }
        for (int chunkY = flags.nextSetBit(0); chunkY >= 0; chunkY = flags.nextSetBit(chunkY + 1)) {
            R obj = this.loadedElements.get(SectionPos.asLong(chunkX, Pos.SectionYCoord.fromSectionIndex(this.levelHeightAccessor, chunkY), chunkZ)).orElse(null);

            if (obj != null && !consumer.collect(obj)) {
                return false;
            }
        }

        return true;
    }

    private BitSet getCachedColumnInfo(int chunkX, int chunkZ) {
        long pos = ChunkPos.asLong(chunkX, chunkZ);

        BitSet flags = this.getColumnInfo(pos, false);

        if (flags != null) {
            return flags;
        }

        this.readColumn(new ChunkPos(pos));

        return this.getColumnInfo(pos, true);
    }

    private BitSet getColumnInfo(long pos, boolean required) {
        BitSet set = this.columns.get(pos);

        if (set == null && required) {
            throw new NullPointerException("No data is present for column: " + new ChunkPos(pos));
        }

        return set;
    }
}

