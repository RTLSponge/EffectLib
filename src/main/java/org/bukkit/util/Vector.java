package org.bukkit.util;

import com.flowpowered.math.vector.Vector3d;

public class Vector {

    private double y;
    private double z;
    private double x;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getY() {
        return y;
    }

    public Vector setY(double y) {
        this.y = y;
        return this;
    }

    public double getZ() {
        return z;
    }

    public Vector setZ(double z) {
        this.z = z;
        return this;
    }

    public double getX() {
        return x;
    }

    public Vector setX(double x) {
        this.x = x;
        return this;
    }

    public Vector3d sponge() {
        return new Vector3d(x, y, z);
    }
}
