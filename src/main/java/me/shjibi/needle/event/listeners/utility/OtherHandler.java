package me.shjibi.needle.event.listeners.utility;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static me.shjibi.needle.utils.StringUtil.color;
import static me.shjibi.needle.utils.spigot.SpigotUtil.withinArea;

public final class OtherHandler implements Listener {

    /* 上床消息 */
    @EventHandler
    public void onEnterBed(PlayerBedEnterEvent e) {
        if (e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
        String name = e.getPlayer().getName();
        Bukkit.broadcastMessage(color("&1" + name + "&6入睡了"));
    }

    /* 防刷怪 */
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        CreatureSpawnEvent.SpawnReason reason = e.getSpawnReason();
        if (reason == CreatureSpawnEvent.SpawnReason.COMMAND ||
            reason == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG ||
            reason == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (withinHideAndSeekArea(e.getLocation())) e.setCancelled(true);
    }

    /* 防破坏 */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!withinHideAndSeekArea(e.getTo())) return;
        if (p.getGameMode() != GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) return;
        p.setGameMode(GameMode.ADVENTURE);
    }

    private static boolean withinHideAndSeekArea(Location loc) {
        return withinArea(loc, 1149, 1249, 47, 96, 324 , 424);
    }

}
