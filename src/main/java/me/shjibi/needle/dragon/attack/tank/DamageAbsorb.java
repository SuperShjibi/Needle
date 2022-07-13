package me.shjibi.needle.dragon.attack.tank;

import me.shjibi.needle.Main;
import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.dragon.attack.DragonAttack;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.spigot.DragonUtil;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.scheduler.BukkitRunnable;

public class DamageAbsorb implements Attacker {

    private static EnderDragon dragon;

    @Override
    public boolean attack(DragonBattle battle) {
        if (battle.getEnderDragon() == null) return false;
        dragon = battle.getEnderDragon();
        int duration = JavaUtil.randomInt(3, 6);
        dragon.setInvulnerable(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                onDisable();
            }
        }.runTaskLater(Main.getInstance(), duration * 20L);

        DragonUtil.sendAttackMessage(battle, DragonAttack.DAMAGE_ABSORB, "它自己", "在&e" + duration + "&7秒内它将不会受到伤害!");
        return true;
    }

    @Override
    public void onDisable() {
        if (dragon == null) return;
        dragon.setInvulnerable(false);
    }

}
