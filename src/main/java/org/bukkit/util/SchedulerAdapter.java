package org.bukkit.util;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectLib;
import org.spongepowered.api.scheduler.Task;

public class SchedulerAdapter {

    private static SchedulerAdapter instance = new SchedulerAdapter();

    public static SchedulerAdapter getInstance() {
        return instance;
    }

    public Task runTask(EffectLib owningPlugin, Runnable runnable) {
        return null;
    }

    public Task runTask(EffectLib owningPlugin, Effect effect) {
        return null;
    }

    public Task runTaskLater(EffectLib owningPlugin, Effect effect, int delay) {
        return null;
    }

    public Task runTaskTimer(EffectLib owningPlugin, Effect effect, int delay, int period) {
        return null;
    }

    public void runTaskAsynchronously(Object owningPlugin, Runnable asyncRunnableTask) {

    }
}
