package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.brain;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.rancraftplayz.pacifist.optimizations.lithium.common.MaskedList;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(Brain.class)
public class BrainMixin<E extends LivingEntity> {
    @Shadow(aliases = "f")
    @Final
    private Map<Integer, Map<Activity, Set<Behavior<? super E>>>> availableBehaviorsByPriority;

    private MaskedList<Behavior<? super E>> flatTasks;

    @Inject(method = "a(Lnet/minecraft/world/entity/schedule/Activity;Lcom/google/common/collect/ImmutableList;Ljava/util/Set;Ljava/util/Set;)V", at = @At("RETURN"))
    private void updateTaskList(Activity var0, ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> var1, Set<Pair<MemoryModuleType<?>, MemoryStatus>> var2, Set<MemoryModuleType<?>> var3, CallbackInfo ci) {
        this.flatTasks = null;
    }

    @Inject(method = "g", at = @At("RETURN"))
    private void clearTaskList(CallbackInfo ci) {
        this.flatTasks = null;
    }

    private void initTaskList() {
        ObjectArrayList<Behavior<? super E>> list = new ObjectArrayList<>();

        for (Map<Activity, Set<Behavior<? super E>>> map : this.availableBehaviorsByPriority.values()) {
            for (Set<Behavior<? super E>> set : map.values()) {
                for (Behavior<? super E> task : set) {
                    //noinspection UseBulkOperation
                    list.add(task);
                }
            }
        }
        this.flatTasks = new MaskedList<>(list);
    }

    @Inject(method = "b(Lnet/minecraft/server/level/WorldServer;Lnet/minecraft/world/entity/EntityLiving;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/Behavior;g(Lnet/minecraft/server/level/WorldServer;Lnet/minecraft/world/entity/EntityLiving;J)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void removeStoppedTask(WorldServer var0, E var1, CallbackInfo ci, long l, Iterator<?> it, Behavior<? super E> task) {
        if (this.flatTasks == null) {
            this.initTaskList();
        }
        this.flatTasks.setVisible(task, false);
    }

    @Inject(method = "e(Lnet/minecraft/server/level/WorldServer;Lnet/minecraft/world/entity/EntityLiving;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/Behavior;f(Lnet/minecraft/server/level/WorldServer;Lnet/minecraft/world/entity/EntityLiving;J)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void removeTaskIfStopped(WorldServer var0, E var1, CallbackInfo ci, long l, Iterator<?> it, Behavior<? super E> task) {
        if (task.getStatus() != Behavior.Status.RUNNING) {
            if (this.flatTasks == null) {
                this.initTaskList();
            }
            this.flatTasks.setVisible(task, false);
        }
    }

    @ModifyVariable(method = "d(Lnet/minecraft/server/level/WorldServer;Lnet/minecraft/world/entity/EntityLiving;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/Behavior;e(Lnet/minecraft/server/level/WorldServer;Lnet/minecraft/world/entity/EntityLiving;J)Z", shift = At.Shift.AFTER))
    private Behavior<? super E> addStartedTasks(Behavior<? super E> task) {
        if (task.getStatus() == Behavior.Status.RUNNING) {
            if (this.flatTasks == null) {
                this.initTaskList();
            }
            this.flatTasks.setVisible(task, true);
        }
        return task;
    }

    /**
     * @author 2No2Name
     * @reason keep the list updated instead of recreating it all the time
     */
    @VisibleForDebug
    @Overwrite
    @Deprecated
    public List<Behavior<? super E>> d() {
        if (this.flatTasks == null) {
            this.initTaskList();
        }
        return this.flatTasks;
    }
}
