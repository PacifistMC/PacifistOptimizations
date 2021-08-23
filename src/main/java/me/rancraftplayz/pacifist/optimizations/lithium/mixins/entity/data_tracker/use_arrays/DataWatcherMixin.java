package me.rancraftplayz.pacifist.optimizations.lithium.mixins.entity.data_tracker.use_arrays;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(DataWatcher.class)
public abstract class DataWatcherMixin {
    @Shadow @Final private Int2ObjectMap<DataWatcher.Item<?>> f;
    @Shadow private boolean h;
    private static final int DEFAULT_ENTRY_COUNT = 10, GROW_FACTOR = 8;

    /**
     * Mirrors the vanilla backing entries map. Each DataTracker.Entry can be accessed in this array through its ID.
     **/
    private DataWatcher.Item<?>[] entriesArray = new DataWatcher.Item<?>[DEFAULT_ENTRY_COUNT];

    /**
     * We redirect the call to add a tracked data to the internal map so we can add it to our new storage structure. This
     * should only ever occur during entity initialization. Type-erasure is a bit of a pain here since we must redirect
     * a calls to the generic Map interface.
     */
    private Object onAddTrackedDataInsertMap(int keyRaw, DataWatcher.Item<?> valueRaw) {
        DataWatcher.Item<?> v = valueRaw;

        DataWatcher.Item<?>[] storage = this.entriesArray;

        // Check if we need to grow the backing array to accommodate the new key range
        if (storage.length <= keyRaw) {
            // Grow the array to accommodate 8 entries after this one, but limit it to never be larger
            // than 256 entries as per the vanilla limit
            int newSize = Math.min(keyRaw +  GROW_FACTOR, 256);

            this.entriesArray = storage = java.util.Arrays.copyOf(storage, newSize);
        }

        // Update the storage
        storage[keyRaw] = v;

        // Ensure that the vanilla backing storage is still updated appropriately
        return this.f.put(keyRaw, v);
    }

    /**
     * @reason Avoid integer boxing/unboxing and use our array-based storage
     * @author JellySquid
     */
    @Overwrite
    private <T> DataWatcher.Item<T> b(DataWatcherObject<T> data) {
        try {
            DataWatcher.Item<?>[] array = this.entriesArray;

            int id = data.a();

            // The vanilla implementation will simply return null if the tracker doesn't contain the specified entry. However,
            // accessing an array with an invalid pointer will throw a OOB exception, where-as a HashMap would simply
            // return null. We check this case (which should be free, even if so insignificant, as the subsequent bounds
            // check will hopefully be eliminated)
            if (id < 0 || id >= array.length) {
                return null;
            }

            // This cast can fail if trying to access a entry which doesn't belong to this tracker, as the ID could
            // instead point to an entry of a different type. However, that is also vanilla behaviour.
            // noinspection unchecked
            return (DataWatcher.Item<T>) array[id];
        } catch (Throwable cause) {
            // Move to another method so this function can be in-lined better
            throw onGetException(cause, data);
        }
    }

    private static <T> ReportedException onGetException(Throwable cause, DataWatcherObject<T> data) {
        CrashReport report = CrashReport.forThrowable(cause, "Getting synced entity data");

        CrashReportCategory section = report.addCategory("Synced entity data");
        section.setDetail("Data ID", data);

        return new ReportedException(report);
    }

    /**
     * @reason too lazy to use Redirect (idk someone fix this code to use redirect or uh leave it like this)
     * @author RanCraftPlayz
     */
    @Overwrite
    private <T> void registerObject(DataWatcherObject<T> datawatcherobject, T t0) {
        DataWatcher.Item<T> datawatcher_item = new DataWatcher.Item(datawatcherobject, t0);
        this.onAddTrackedDataInsertMap(datawatcherobject.a(), datawatcher_item);
        this.h = false;
    }
}
