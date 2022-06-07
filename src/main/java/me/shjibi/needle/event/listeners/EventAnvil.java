package me.shjibi.needle.event.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;

public final class EventAnvil implements Listener {

    /* 防止过于昂贵(好像有些bug */
    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent e) {
        if (e.getInventory().getRepairCost() > 39) {
            e.getInventory().setRepairCost(39);
        }
    }

}
