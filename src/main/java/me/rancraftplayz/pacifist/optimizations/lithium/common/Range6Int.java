package me.rancraftplayz.pacifist.optimizations.lithium.common;

import java.util.Objects;

/**
 * Original code by JellySquid, licensed under GNU Lesser General Public License v3.0
 * you can find the original code on https://github.com/CaffeineMC/lithium-fabric/ (Yarn mappings)
 */

public record Range6Int(int negativeX, int negativeY, int negativeZ, int positiveX, int positiveY, int positiveZ) {

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Range6Int that = (Range6Int) obj;
        return this.negativeX == that.negativeX &&
                this.negativeY == that.negativeY &&
                this.negativeZ == that.negativeZ &&
                this.positiveX == that.positiveX &&
                this.positiveY == that.positiveY &&
                this.positiveZ == that.positiveZ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(negativeX, negativeY, negativeZ, positiveX, positiveY, positiveZ);
    }

    @Override
    public String toString() {
        return "Range6Int[" +
                "negativeX=" + negativeX + ", " +
                "negativeY=" + negativeY + ", " +
                "negativeZ=" + negativeZ + ", " +
                "positiveX=" + positiveX + ", " +
                "positiveY=" + positiveY + ", " +
                "positiveZ=" + positiveZ + ']';
    }

}
