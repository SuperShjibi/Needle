package me.shjibi.needle.rare;

import me.shjibi.needle.utils.spigot.SpigotUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

import static me.shjibi.needle.utils.JavaUtil.roll;
import static me.shjibi.needle.utils.StringUtil.color;
import static me.shjibi.needle.utils.spigot.ItemUtil.getOPEnchantmentBook;
import static me.shjibi.needle.utils.spigot.SpigotUtil.*;

public final class EndermanKillHandler implements Listener {

    private static final String SPECIAL_NAME = color("&c&l特殊末影人");

    @EventHandler
    public void onKillEnderman(EntityDeathEvent e) {
        if (e.getEntityType() != EntityType.ENDERMAN) return;

        Player p = e.getEntity().getKiller();
        if (p == null) return;

        Entity entity = e.getEntity();
        World world = entity.getWorld();

        if (isSpecialEnderman(entity)) {
            e.getDrops().clear();
            e.getDrops().add(getOPEnchantmentBook(Enchantment.PROTECTION_ENVIRONMENTAL, 1));
            SpigotUtil.broadcastRandomEvent(EventRarity.RARE, "{name}成功击杀了特殊末影人,并获得了保护V附魔书!", p);
        } else {
            if (world.getEnvironment() != World.Environment.THE_END) return;
            if (isInMainIsland(entity.getLocation())) return;

            if (!roll( 214)) return;

            Entity spawned = world.spawnEntity(entity.getLocation(), EntityType.ENDERMAN);
            if (!(spawned instanceof Enderman enderman)) return;

            p.sendTitle(color("&c特殊末影人!!"), color("&o&7击杀以获得保护V附魔书"), 10, 70, 10);
            playNoticeSound(p);

            setSpecialAttribute(enderman);
            enderman.setTarget(p);
            enderman.setCustomName(SPECIAL_NAME);
            enderman.setCustomNameVisible(true);
            enderman.setCarriedBlock(Bukkit.createBlockData(Material.END_PORTAL_FRAME));
        }
    }

    @EventHandler
    public void onEndermanTeleport(EntityTeleportEvent e) {
        if (!isSpecialEnderman(e.getEntity())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onEndermanDrown(EntityDamageEvent e) {
        if (!isSpecialEnderman(e.getEntity())) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.DROWNING) e.setCancelled(true);
    }

    @EventHandler
    public void onEntityPlace(EntityChangeBlockEvent e) {
        if (!isSpecialEnderman(e.getEntity())) return;
        e.setCancelled(true);
    }

    private static boolean isSpecialEnderman(Entity enderman) {
        return enderman.getType() == EntityType.ENDERMAN &&
                enderman.getCustomName() != null &&
                enderman.getCustomName().equals(SPECIAL_NAME);
    }
    
    private static void setSpecialAttribute(Enderman enderman) {
        setMaxHealth(enderman, 80);
        setAttackDamage(enderman, 15);
    }

}
