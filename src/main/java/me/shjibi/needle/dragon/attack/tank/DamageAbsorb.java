package me.shjibi.needle.dragon.attack.tank;

import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.dragon.attack.DragonAttack;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.spigot.DragonUtil;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageAbsorb implements Attacker, Listener {

    private static long time;
    private static int entityID;

    @Override
    public boolean attack(DragonBattle battle) {
        if (battle.getEnderDragon() == null) return false;
        int duration = JavaUtil.randomInt(3, 6);
        time = System.currentTimeMillis() + duration * 1000L;
        entityID = battle.getEnderDragon().getEntityId();
        DragonUtil.sendAttackMessage(battle, DragonAttack.DAMAGE_ABSORB, "它自己", "在&e" + duration + "&7秒内它将不会受到伤害!");
        return true;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntityType() != EntityType.ENDER_DRAGON) return;
        if (e.getEntity().getEntityId() != entityID) return;
        if (System.currentTimeMillis() > time) return;
        e.setCancelled(true);
    }



}
