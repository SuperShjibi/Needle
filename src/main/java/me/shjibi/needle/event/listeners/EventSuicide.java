package me.shjibi.needle.event.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static me.shjibi.needle.commands.handlers.CommandSuicide.suiciders;

public final class EventSuicide implements Listener {

    @EventHandler
    public void onSuicide(PlayerDeathEvent e) {
        String name = e.getEntity().getName();
        if (!suiciders.contains(name)) return;
        e.setDeathMessage(name + "自杀了");
        suiciders.remove(name);
    }

}
