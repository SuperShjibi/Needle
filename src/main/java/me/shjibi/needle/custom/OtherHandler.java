package me.shjibi.needle.custom;

import me.shjibi.needle.utils.JavaUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

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
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (!withinHideAndSeekArea(p.getLocation())) return;
        if (p.getGameMode() == GameMode.CREATIVE) return;
        e.setCancelled(true);
    }

    /* 防破坏 */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (!withinHideAndSeekArea(p.getLocation())) return;
        if (p.getGameMode() == GameMode.CREATIVE) return;
        e.setCancelled(true);
    }

    /* 防小黑破坏 */
    @EventHandler
    public void onEndermanTeleport(EntityTeleportEvent e) {
        if (e.getEntityType() != EntityType.ENDERMAN) return;
        if (!withinHideAndSeekArea(e.getTo())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void test(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        JavaUtil.debug(p.getName() + " picked up a " + e.getItem().getItemStack().getType() + "!");
    }

    private static boolean withinHideAndSeekArea(Location loc) {
        return withinArea(loc, 1149, 1249, 47, 96, 324 , 424);
    }

}
