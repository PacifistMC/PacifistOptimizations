package me.rancraftplayz.pacifist.optimizations.lithium.mixins.math.fast_blockpos;

import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

@Mixin(value = BlockPosition.class, remap = false)
public abstract class BlockPosMixin extends BaseBlockPosition {
    public BlockPosMixin(int x, int y, int z) {
        super(x, y, z);
    }

    /**
     * @author JellySquid
     * @reason Simplify and inline
     */
    @Overwrite(remap = false)
    public BlockPosition up() {
        return (BlockPosition) (Object) new BlockPos(this.getX(), this.getY() + 1, this.getZ());
    }

    /**
     * @author JellySquid
     * @reason Simplify and inline
     */
    @Overwrite(remap = false)
    public BlockPosition up(int distance) {
        return (BlockPosition) (Object) new BlockPos(this.getX(), this.getY() + distance, this.getZ());
    }

    /**
     * @author JellySquid
     * @reason Simplify and inline
     */
    @Overwrite(remap = false)
    public BlockPosition down() {
        return (BlockPosition) (Object) new BlockPos(this.getX(), this.getY() - 1, this.getZ());
    }

    /**
     * @author JellySquid
     * @reason Simplify and inline
     */
    @Overwrite(remap = false)
    public BlockPosition down(int distance) {
        return (BlockPosition) (Object) new BlockPos(this.getX(), this.getY() - distance, this.getZ());
    }

    /**
     * @author JellySquid
     * @reason Simplify and inline
     */
    @Overwrite(remap = false)
    public BlockPosition north() {
        return (BlockPosition) (Object) new BlockPos(this.getX(), this.getY(), this.getZ() - 1);
    }

    /**
     * @author JellySquid
     * @reason Simplify and inline
     */
    @Overwrite(remap = false)
    public BlockPosition north(int distance) {
        return (BlockPosition) (Object) new BlockPos(this.getX(), this.getY(), this.getZ() - distance);
    }

    /**
     * @author JellySquid
     * @reason Simplify and inline
     */
    @Overwrite(remap = false)
    public BlockPosition south() {
        return (BlockPosition) (Object) new BlockPos(this.getX(), this.getY(), this.getZ() + 1);
    }

    /**
     * @author JellySquid
     * @reason Simplify and inline
     */
    @Overwrite(remap = false)
    public BlockPosition south(int distance) {
        return (BlockPosition) (Object) new BlockPos(this.getX(), this.getY(), this.getZ() + distance);
    }

    /**
     * @author JellySquid
     * @reason Simplify and inline
     */
    @Overwrite(remap = false)
    public BlockPosition west() {
        return (BlockPosition) (Object) new BlockPos(this.getX() - 1, this.getY(), this.getZ());
    }

    /**
     * @author JellySquid
     * @reason Simplify and inline
     */
    @Overwrite(remap = false)
    public BlockPosition west(int distance) {
        return (BlockPosition) (Object) new BlockPos(this.getX() - distance, this.getY(), this.getZ());
    }

    /**
     * @author JellySquid
     * @reason Simplify and inline
     */
    @Overwrite(remap = false)
    public BlockPosition east() {
        return (BlockPosition) (Object) new BlockPos(this.getX() + 1, this.getY(), this.getZ());
    }

    /**
     * @author JellySquid
     * @reason Simplify and inline
     */
    @Overwrite(remap = false)
    public BlockPosition east(int distance) {
        return (BlockPosition) (Object) new BlockPos(this.getX() + distance, this.getY(), this.getZ());
    }
}
