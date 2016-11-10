package de.slikey.effectlib.util;

import com.flowpowered.math.vector.Vector3d;
import org.bukkit.util.DirectionUtils;
import org.bukkit.util.Vector;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;

import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 * Represents a Location that can move, possibly bound to an Entity.
 */
public class DynamicLocation {
    private Transform<World> location;
    private Transform<World> originalLocation;
    private final WeakReference<Entity> entity;
    private float yawOffset;
    private float pitchOffset;
    private Vector offset;
    private Vector relativeOffset;
    private Vector entityOffset;
    private boolean updateLocation = true;
    private boolean updateDirection = true;
    private Transform<World> transform;

    public DynamicLocation(Transform<World> location) {
        this.location = location;
        this.originalLocation = location;
        this.entity = null;
    }

    public DynamicLocation(Entity entity) {
        if (entity != null) {
            this.entity = new WeakReference<Entity>(entity);
            this.location = getEntityLocation(entity);
        } else {
            this.entity = null;
            this.location = null;
        }
        this.originalLocation = location;
    }

    public DynamicLocation(Transform<World> location, Entity entity) {
        if (location != null) {
            this.location = location;
        } else if (entity != null) {
            this.location = getEntityLocation(entity);
        } else {
            this.location = null;
        }
        if (entity != null) {
            this.entity = new WeakReference<Entity>(entity);
            this.entityOffset = this.location.getPosition().sub(getEntityLocation(entity).getPosition());
        } else {
            this.entity = null;
        }
        this.originalLocation = location;
    }

    public void addOffset(Vector3d offset) {
        if (this.offset == null) {
            this.offset = offset;
        } else {
            this.offset = this.offset.add(offset);
        }
        this.updateOffsets();
    }

    public void addRelativeOffset(Vector offset) {
        if (this.relativeOffset == null) {
            this.relativeOffset = offset;
        } else {
            this.relativeOffset = this.relativeOffset.add(offset);
        }
        this.updateOffsets();
    }

    public Entity getEntity() {
        return entity == null ? null : entity.get();
    }

    public Transform<World> getLocation() {
        return location;
    }

    protected Transform<World> getEntityLocation(Entity entity) {
        Optional<EyeLocationProperty> eye = entity.getProperty(EyeLocationProperty.class);
        Transform<World> t = entity.getTransform();
        if(eye.isPresent()){
            t.setPosition(eye.get().getValue());
        }
        return t;
    }

    public void setDirection(Vector3d direction) {
        location = DirectionUtils.fromDirection(direction, location);
        updateDirectionOffsets();
    }

    protected void updateDirectionOffsets() {
        if (yawOffset != 0) {
            Vector3d r = location.getRotation();
            location = location.setRotation(r.add(0, this.yawOffset, 0));
        }
        if (pitchOffset != 0) {
            Vector3d r = location.getRotation();
            location = location.setRotation(r.add(pitchOffset, 0, 0));
        }
    }

    public void updateFrom(Transform<World> newLocation) {
        if (originalLocation != null) {
            originalLocation = newLocation;
        }
        updateOffsets();
    }

    public void updateOffsets() {
        if (originalLocation == null || location == null) return;
            location = originalLocation;
        if (offset != null) {
            location = location.setPosition(location.getPosition().add(offset));
        }
        if (relativeOffset != null) {
            location = location.add(VectorUtils.rotateVector(relativeOffset, location));
        }
        if (entityOffset != null) {
            location.add(entityOffset);
        }
    }

    public void setUpdateLocation(boolean update) {
        updateLocation = update;
    }

    public void update() {
        if (location == null || (!updateLocation && !updateDirection)) {
            return;
        }

        Entity entityReference = entity == null ? null : entity.get();
        if (entityReference != null) {
            Transform<World> currentLocation = getEntityLocation(entityReference);
            if (updateDirection)
            {

                setDirection(currentLocation.getDirection());
            }
            if (updateLocation)
            {
                updateFrom(currentLocation);
            }
        }
    }

    public void setUpdateDirection(boolean updateDirection) {
        this.updateDirection = updateDirection;
    }

    public void setDirectionOffset(float yawOffset, float pitchOffset) {
        this.pitchOffset = pitchOffset;
        this.yawOffset = yawOffset;
        updateDirectionOffsets();
    }

    public Transform<World> getTransform() {
        return transform;
    }
}
