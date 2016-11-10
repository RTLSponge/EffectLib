package org.bukkit.util;

import com.flowpowered.math.TrigMath;
import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;

public class DirectionUtils {

    public static Vector3d getDirection(Transform<World> transform){
        Vector3d rotation = transform.getRotation();
        return Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
    }

    public static Transform<World> fromDirection(Vector3d direction, Transform<World> transform){

        // x-z plane represented by theta = atan2
        double yaw;
        final double z = direction.getZ();
        if (z != 0) {
            yaw = TrigMath.RAD_TO_DEG * TrigMath.atan2(direction.getX(), z);
        } else {
            // If Z is zero, it's either -90 (for +X) or 90 (for -X)
            yaw = direction.getX() > 0 ? -90 : 90;
        }

        // y direction represented by angle between direction in x-z plane and actual direction
        // Normalised, so we know the length of the overall direction is one. Sign of y determines the pitch being up or down.
        double pitch = TrigMath.acos(direction.toVector2(true).length()) * -Math.signum(direction.getY());

        return transform.setRotation(new Vector3d(TrigMath.RAD_TO_DEG * pitch, yaw, 0));
    }

}
