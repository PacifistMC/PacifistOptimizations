package me.rancraftplayz.pacifist.optimizations.lithium.mixins.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
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

// TODO: Make a fix for PaperMC
@Mixin(GoalSelector.class)
public abstract class GoalSelectorMixin {
    private static final Goal.Flag[] CONTROLS = Goal.Flag.values();

    @Shadow
    @Final
    // This is profiler
    private Supplier<Profiler> e;

    @Mutable
    @Shadow
    @Final
    // This is availableGoals
    private Set<WrappedGoal> d;

    @Shadow
    @Final
    // This is disabledFlags
    private EnumSet<Goal.Flag> f;

    @Shadow
    @Final
    // This is lockedFlags
    private Map<Goal.Flag, WrappedGoal> c;

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
        Profiler profiler = this.e.get();
        profiler.begin("goalCleanup");

        // Stop any availableGoals which are disabled or shouldn't continue executing
        this.stopGoals();

        // Update the controls
        this.cleanupControls();

        profiler.mark("goalUpdate");

        // Try to start new availableGoals where possible
        this.startGoals();

        profiler.reset();
    }

    /**
     * Attempts to stop all availableGoals which are running and either shouldn't continue or no longer have available controls.
     */
    private void stopGoals() {
        for (WrappedGoal goal : this.d) {
            // Filter out availableGoals which are not running
            if (!goal.isRunning()) {
                continue;
            }

            // If the goal shouldn't continue or any of its controls have been disabled, then stop the goal
            if (!goal.canContinueToUse() || this.areControlsDisabled(goal)) {
                goal.stop();
            }
        }
    }

    /**
     * Performs a scan over all currently held controls and releases them if their associated goal is stopped.
     */
    private void cleanupControls() {
        for (Goal.Flag control : CONTROLS) {
            WrappedGoal goal = this.c.get(control);

            // If the control has been acquired by a goal, check if the goal should still be running
            // If the goal should not be running anymore, release the control held by it
            if (goal != null && !goal.isRunning()) {
                this.c.remove(control);
            }
        }
    }

    /**
     * Attempts to start all availableGoals which are not-already running, can be started, and have their controls available.
     */
    private void startGoals() {
        for (WrappedGoal goal : this.d) {
            // Filter out availableGoals which are already running or can't be started
            if (goal.isRunning()) {
                continue;
            }

            // Check if the goal's controls are available or can be replaced
            if (!this.areGoalControlsAvailable(goal)) {
                continue;
            }

            //canStart has side effects, so do not reorder it before the previous tests
            if (!goal.canUse()) {
                continue;
            }

            // Hand over controls to this goal and stop any availableGoals which depended on those controls
            for (Goal.Flag control : goal.getFlags()) {
                WrappedGoal otherGoal = this.getGoalOccupyingControl(control);

                if (otherGoal != null) {
                    otherGoal.stop();
                }

                this.setGoalOccupyingControl(control, goal);
            }

            goal.start();
        }
    }

    /**
     * Ticks all running AI availableGoals.
     */
    private void tickGoals() {
        this.e.get().begin("goalTick");

        // Tick all currently running availableGoals
        for (WrappedGoal goal : this.d) {
            if (goal.isRunning()) {
                goal.tick();
            }
        }

        this.e.get().reset();
    }

    /**
     * Returns true if any controls of the specified goal are disabled.
     */
    private boolean areControlsDisabled(WrappedGoal goal) {
        for (Goal.Flag control : goal.getFlags()) {
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
    private boolean areGoalControlsAvailable(WrappedGoal goal) {
        for (Goal.Flag control : goal.getFlags()) {
            if (this.isControlDisabled(control)) {
                return false;
            }

            WrappedGoal occupied = this.getGoalOccupyingControl(control);

            if (occupied != null && !occupied.canBeReplacedBy(goal)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if the specified control is disabled.
     */
    private boolean isControlDisabled(Goal.Flag control) {
        return this.f.contains(control);
    }

    /**
     * Returns the goal which is currently holding the specified control, or null if no goal is.
     */
    private WrappedGoal getGoalOccupyingControl(Goal.Flag control) {
        return this.c.get(control);
    }

    /**
     * Changes the goal which is currently holding onto a control.
     */
    private void setGoalOccupyingControl(Goal.Flag control, WrappedGoal goal) {
        this.c.put(control, goal);
    }

}
