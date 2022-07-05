package me.shjibi.needle.event.listeners.extra.rare;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTeleportEvent;

import static me.shjibi.needle.utils.JavaUtil.roll;
import static me.shjibi.needle.utils.SpigotUtil.*;
import static me.shjibi.needle.utils.StringUtil.color;

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
            broadcastRandomEvent(EventRarity.RARE, "{name}成功击杀了特殊末影人,并获得了保护V附魔书!", p);
        } else {
            if (world.getEnvironment() != World.Environment.THE_END) return;
            if (world.getBiome(entity.getLocation()) == Biome.THE_END) return;

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
    
    private static boolean isSpecialEnderman(Entity enderman) {
        return enderman.getType() == EntityType.ENDERMAN &&
                enderman.getCustomName() != null &&
                enderman.getCustomName().equals(SPECIAL_NAME);
    }
    
    private static void setSpecialAttribute(Enderman enderman) {
        AttributeInstance maxHealth = enderman.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance attackDamage = enderman.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (maxHealth != null && attackDamage != null) {
            maxHealth.setBaseValue(80);
            attackDamage.setBaseValue(15);
        }
        enderman.setHealth(80);
    }

}
