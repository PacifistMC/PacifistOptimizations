package me.rancraftplayz.pacifist.optimizations.lithium.common;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Objects;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

//Y values use coordinates, not indices (y=0 -> chunkY=0)
//upper bounds are EXCLUSIVE
public record WorldSectionBox(Level world, int chunkX1, int chunkY1, int chunkZ1, int chunkX2,
                              int chunkY2, int chunkZ2) {

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        WorldSectionBox that = (WorldSectionBox) obj;
        return Objects.equals(this.world, that.world) &&
                this.chunkX1 == that.chunkX1 &&
                this.chunkY1 == that.chunkY1 &&
                this.chunkZ1 == that.chunkZ1 &&
                this.chunkX2 == that.chunkX2 &&
                this.chunkY2 == that.chunkY2 &&
                this.chunkZ2 == that.chunkZ2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, chunkX1, chunkY1, chunkZ1, chunkX2, chunkY2, chunkZ2);
    }

    @Override
    public String toString() {
        return "WorldSectionBox[" +
                "world=" + world + ", " +
                "chunkX1=" + chunkX1 + ", " +
                "chunkY1=" + chunkY1 + ", " +
                "chunkZ1=" + chunkZ1 + ", " +
                "chunkX2=" + chunkX2 + ", " +
                "chunkY2=" + chunkY2 + ", " +
                "chunkZ2=" + chunkZ2 + ']';
    }

    public static WorldSectionBox entityAccessBox(Level world, AABB box) {
        int minX = SectionPos.posToSectionCoord(box.minX - 2.0D);
        int minY = SectionPos.posToSectionCoord(box.minY - 2.0D);
        int minZ = SectionPos.posToSectionCoord(box.minZ - 2.0D);
        int maxX = SectionPos.posToSectionCoord(box.maxX + 2.0D) + 1;
        int maxY = SectionPos.posToSectionCoord(box.maxY + 2.0D) + 1;
        int maxZ = SectionPos.posToSectionCoord(box.maxZ + 2.0D) + 1;
        return new WorldSectionBox(world, minX, minY, minZ, maxX, maxY, maxZ);
    }

    public int numSections() {
        return (this.chunkX2 - this.chunkX1) * (this.chunkY2 - this.chunkY1) * (this.chunkZ2 - this.chunkZ1);
    }
}
