package me.shjibi.needle.event.listeners.utility;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import static me.shjibi.needle.utils.StringUtil.color;

public final class BedEventHandler implements Listener {

    /* 上床消息 */
    @EventHandler
    public void onEnterBed(PlayerBedEnterEvent e) {
        if (e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
        String name = e.getPlayer().getName();
        Bukkit.broadcastMessage(color("&1" + name + "&6入睡了"));
    }

}
