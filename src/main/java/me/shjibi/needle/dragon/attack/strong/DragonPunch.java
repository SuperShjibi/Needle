package me.shjibi.needle.dragon.attack.strong;

import me.shjibi.needle.dragon.attack.DragonAttack;
import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.spigot.DragonUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Player;

import java.util.List;

import static me.shjibi.needle.utils.JavaUtil.randomInt;
import static me.shjibi.needle.utils.StringUtil.color;
import static me.shjibi.needle.utils.spigot.DragonUtils.getAllFighters;

public class DragonPunch implements Attacker {

    @Override
    public boolean attack(DragonBattle battle) {
        List<Player> players = getAllFighters(battle);
        Player p = JavaUtil.randomElement(players);
        if (p == null) return false;
        double damage = randomInt(15, 20);
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 5, 1);
        p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 50);
        p.damage(damage, battle.getEnderDragon());

        DragonUtils.sendAttackMessage(battle, DragonAttack.DRAGON_PUNCH, p.getName(), color("造成了&c" + damage + "&7点伤害!"));
        return true;
    }

}
