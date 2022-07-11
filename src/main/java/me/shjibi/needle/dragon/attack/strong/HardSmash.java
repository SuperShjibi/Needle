package me.shjibi.needle.dragon.attack.strong;

import me.shjibi.needle.dragon.attack.DragonAttack;
import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.spigot.DragonUtil;
import org.bukkit.Sound;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

import static me.shjibi.needle.utils.spigot.DragonUtil.getAllFighters;

public class HardSmash implements Attacker {

    @Override
    public boolean attack(DragonBattle battle) {
        List<Player> players = getAllFighters(battle);
        if (players.isEmpty()) return false;
        for (Player player : players) {
            Vector vec = new Vector(0, JavaUtil.randomInt(3, 6), 0);
            player.teleport(player.getLocation().clone().add(0, 3.5, 0));
            player.setVelocity(vec);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 5, 1);
        }
        DragonUtil.sendAttackMessage(battle, DragonAttack.HARD_SMASH, "所有人");
        return true;
    }

}
