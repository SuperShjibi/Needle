package me.shjibi.needle.custom;

import me.shjibi.needle.Main;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.spigotmc.event.entity.EntityDismountEvent;

public final class SitHandler implements Listener {

    /* 右键台阶坐下 */
    @EventHandler
    public void onSit(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.isSneaking()) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (p.isInsideVehicle()) return;

        Block block = e.getClickedBlock();
        if (block == null) return;
        if (!(block.getBlockData() instanceof Stairs data)) return;

        if (data.getShape() != Stairs.Shape.STRAIGHT) return;
        if (data.getHalf() == Bisected.Half.TOP) return;
        if (!block.getRelative(0, 1, 0).isPassable()) return;

        e.setCancelled(true);

        Block under = block.getRelative(0, -1, 0);
        double yOffset = under.getType().isAir() || under.isLiquid() ? 0 : -0.1;
        Location loc = block.getLocation().add(0.5, yOffset, 0.5);
        p.getWorld().spawn(loc, Arrow.class, a -> {
            a.setMetadata("chair", new FixedMetadataValue(Main.getInstance(), "needle"));
            a.addPassenger(p);
        });
    }

    /* 按shift站起来时删除箭 */
    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        if (e.getDismounted().getType() != EntityType.ARROW) return;
        if (!e.getDismounted().hasMetadata("chair") || e.getDismounted().getMetadata("chair").stream().noneMatch(x ->
                x.getOwningPlugin() != null && x.getOwningPlugin().getName().equals(Main.getInstance().getName()) && x.asString().equals("needle"))) return;
        e.getDismounted().remove();
        Player p = (Player) e.getEntity();
        p.teleport(p.getLocation().add(0, 1, 0));
    }

    /* 玩家坐着退出时删除箭 */
    @EventHandler
    public void onIrresponsibleQuit(PlayerQuitEvent e) {
        if (!e.getPlayer().isInsideVehicle()) return;
        Entity vehicle = e.getPlayer().getVehicle();
        if (vehicle == null || vehicle.getType() != EntityType.ARROW) return;
        if (vehicle.hasMetadata("chair")) return;
        vehicle.remove();
    }

}
