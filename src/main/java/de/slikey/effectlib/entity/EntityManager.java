package de.slikey.effectlib.entity;

import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.listener.ItemListener;
import de.slikey.effectlib.util.RandomUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class EntityManager {

    private final EffectLib effectLib;
    private final Map<Entity, Task> entities;
    private boolean disposed = false;
    private final Function<Entity, Cause> causeFactory = entity ->
            Cause.of(
                    NamedCause.source(
                            EntitySpawnCause.builder()
                                    .entity(entity)
                                    .type(SpawnTypes.PLUGIN)
                                    .build()
                    )
            );

    public EntityManager(EffectLib effectLib) {
        this.effectLib = effectLib;
        this.entities = new HashMap<Entity, Task>();
    }

    public void removeAll() {
        for (Map.Entry<Entity, Task> entry : entities.entrySet()) {
            entry.getKey().remove();
            entry.getValue().cancel();
        }
        entities.clear();
    }

    public void remove(Entity entity) {
        entities.get(entity).cancel();
        entities.remove(entity);
        entity.remove();
    }

    public void add(final Entity entity, int duration) {
        if (disposed) {
            throw new IllegalStateException("EffectManager is disposed and not able to accept any effects.");
        }
        Task task = Sponge.getScheduler().createTaskBuilder()
                .execute(() -> this.remove(entity))
                .intervalTicks(duration)
                .submit(effectLib);
        entities.put(entity, task);
    }

    public Item spawnItem(ItemStack is, Location<World> loc, int duration) {
        is.offer(Keys.DISPLAY_NAME, Text.of(ItemListener.ITEM_IDENTIFIER, TextColors.RED, RandomUtils.random.nextInt(10000)));
        EntityArchetype template = EntityArchetype.builder()
                .type(EntityTypes.ITEM)
                .set(Keys.REPRESENTED_ITEM, is.createSnapshot())
                .build();
        Item i = (Item) loc.getExtent().createEntity(template.getEntityData()).get();
        loc.getExtent().spawnEntity(i, causeFactory.apply(i));
        i.offer(Keys.INFINITE_PICKUP_DELAY, true);
        i.offer(Keys.INFINITE_DESPAWN_DELAY, true);
        //i.offer(ItemListener.ITEM_IDENTIFIER, 0);
        add(i, duration);
        return i;
    }

    public Entity spawnEntity(EntityType type, Location<World> loc, int duration) {
        Entity e = loc.getExtent().createEntity(type, loc.getPosition());
        loc.getExtent().spawnEntity(e, this.causeFactory.apply(e));
        //TODO customData
        //e.offer(ItemListener.ITEM_IDENTIFIER, 0);
        add(e, duration);
        return e;
    }

    public void dispose() {
        disposed = true;
        removeAll();
    }

}
