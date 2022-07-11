package me.shjibi.needle.dragon.attack.tank;

import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.dragon.attack.DragonAttack;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.spigot.DragonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

public class TNTLauncher implements Attacker {

    @Override
    public boolean attack(DragonBattle battle) {
        Player p = JavaUtil.randomElement(DragonUtil.getAllFighters(battle));
        if (p == null) return false;
        Location location = p.getLocation();
        p.getWorld().playSound(location, Sound.ENTITY_TNT_PRIMED, 5, 1);
        TNTPrimed tnt = p.getWorld().spawn(
                location.clone().add(0, 1.2, 0),
                TNTPrimed.class);
        int fuseTicks = JavaUtil.randomInt(16, 32);
        tnt.setFuseTicks(fuseTicks);
        tnt.setSource(battle.getEnderDragon());
        tnt.setYield(4 * fuseTicks / 20f);
        DragonUtil.sendAttackMessage(battle, DragonAttack.TNT_LAUNCHER, p.getName(), "TNT将在&e" + fuseTicks / 20f + "&7后爆炸");
        return true;
    }

}
