package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.goal;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.PathfinderGoalWrapped;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.util.perf.Profiler;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

// TODO: Make a fix for papermc
@Mixin(PathfinderGoalSelector.class)
public abstract class PathfinderGoalSelectorMixin {
    private static final PathfinderGoal.Type[] CONTROLS = PathfinderGoal.Type.values();

    @Shadow
    @Final
    // This is profiler
    private Supplier<GameProfilerFiller> e;

    @Mutable
    @Shadow
    @Final
    // This is availableGoals
    private Set<PathfinderGoalWrapped> d;

    @Shadow
    @Final
    // This is disabledFlags
    private EnumSet<PathfinderGoal.Type> f;

    @Shadow
    @Final
    // This is lockedFlags
    private Map<PathfinderGoal.Type, PathfinderGoalWrapped> c;

    /**
     * Replace the goal set with an optimized collection type which performs better for iteration.
     */
    @Inject(method = "<init>(Ljava/util/function/Supplier;)V", at = @At("RETURN"))
    private void reinit(Supplier<Profiler> supplier, CallbackInfo ci) {
        this.d = new ObjectLinkedOpenHashSet<>(this.d);
    }

    /**
     * Avoid the usage of streams entirely to squeeze out additional performance.
     *
     * @reason Remove lambdas and complex stream logic
     * @author JellySquid
     */
    @Overwrite
    public void doTick() {
        this.updateGoalStates();
        this.tickGoals();
    }

    /**
     * Checks the state of all availableGoals for the given entity, starting and stopping them as necessary (because a goal
     * has been disabled, the controls are no longer available or have been reassigned, etc.)
     */
    private void updateGoalStates() {
        GameProfilerFiller e = this.e.get();
        e.enter("goalCleanup");

        // Stop any availableGoals which are disabled or shouldn't continue executing
        this.stopGoals();

        // Update the controls
        this.cleanupControls();

        e.exitEnter("goalUpdate");

        // Try to start new availableGoals where possible
        this.startGoals();

        e.exit();
    }

    /**
     * Attempts to stop all availableGoals which are running and either shouldn't continue or no longer have available controls.
     */
    private void stopGoals() {
        for (PathfinderGoalWrapped goal : this.d) {
            // Filter out availableGoals which are not running
            if (!goal.g()) {
                continue;
            }

            // If the goal shouldn't continue or any of its controls have been disabled, then stop the goal
            if (!goal.b() || this.areControlsDisabled(goal)) {
                goal.d();
            }
        }
    }

    /**
     * Performs a scan over all currently held controls and releases them if their associated goal is stopped.
     */
    private void cleanupControls() {
        for (PathfinderGoal.Type control : CONTROLS) {
            PathfinderGoalWrapped goal = this.c.get(control);

            // If the control has been acquired by a goal, check if the goal should still be running
            // If the goal should not be running anymore, release the control held by it
            if (goal != null && !goal.g()) {
                this.c.remove(control);
            }
        }
    }

    /**
     * Attempts to start all availableGoals which are not-already running, can be started, and have their controls available.
     */
    private void startGoals() {
        for (PathfinderGoalWrapped goal : this.d) {
            // Filter out availableGoals which are already running or can't be started
            if (goal.g()) {
                continue;
            }

            // Check if the goal's controls are available or can be replaced
            if (!this.areGoalControlsAvailable(goal)) {
                continue;
            }

            // canStart has side effects, so do not reorder it before the previous tests
            if (!goal.a()) {
                continue;
            }

            // Hand over controls to this goal and stop any availableGoals which depended on those controls
            for (PathfinderGoal.Type control : goal.i()) {
                PathfinderGoalWrapped otherGoal = this.getGoalOccupyingControl(control);

                if (otherGoal != null) {
                    otherGoal.d();
                }

                this.setGoalOccupyingControl(control, goal);
            }

            goal.c();
        }
    }

    /**
     * Ticks all running AI availableGoals.
     */
    private void tickGoals() {
        this.e.get().enter("goalTick");

        // Tick all currently running availableGoals
        for (PathfinderGoalWrapped goal : this.d) {
            if (goal.g()) {
                goal.e();
            }
        }

        this.e.get().exit();
    }

    /**
     * Returns true if any controls of the specified goal are disabled.
     */
    private boolean areControlsDisabled(PathfinderGoalWrapped goal) {
        for (PathfinderGoal.Type control : goal.i()) {
            if (this.isControlDisabled(control)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if all controls for the specified goal are either available (not acquired by another goal) or replaceable
     * (acquired by another goal, but eligible for replacement) and not disabled for the entity.
     */
    private boolean areGoalControlsAvailable(PathfinderGoalWrapped goal) {
        for (PathfinderGoal.Type control : goal.i()) {
            if (this.isControlDisabled(control)) {
                return false;
            }

            PathfinderGoalWrapped occupied = this.getGoalOccupyingControl(control);

            if (occupied != null && !occupied.a(goal)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if the specified control is disabled.
     */
    private boolean isControlDisabled(PathfinderGoal.Type control) {
        return this.f.contains(control);
    }

    /**
     * Returns the goal which is currently holding the specified control, or null if no goal is.
     */
    private PathfinderGoalWrapped getGoalOccupyingControl(PathfinderGoal.Type control) {
        return this.c.get(control);
    }

    /**
     * Changes the goal which is currently holding onto a control.
     */
    private void setGoalOccupyingControl(PathfinderGoal.Type control, PathfinderGoalWrapped goal) {
        this.c.put(control, goal);
    }

}
