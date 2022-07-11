package me.shjibi.needle.dragon.attack.tank;

import me.shjibi.needle.Main;
import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.dragon.attack.DragonAttack;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.spigot.DragonUtil;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static me.shjibi.needle.utils.StringUtil.color;

public class SlowDown implements Attacker {

    @Override
    public boolean attack(DragonBattle battle) {
        List<Player> players = DragonUtil.getAllFighters(battle);
        if (players.isEmpty()) return false;
        players.forEach(p -> p.setWalkSpeed(-0.2f));
        int duration = JavaUtil.randomInt(3, 8);
        new BukkitRunnable() {
            @Override
            public void run() {
                players.forEach(p -> p.setWalkSpeed(0.2f));
            }
        }.runTaskLater(Main.getInstance(), 20L * duration);
        DragonUtil.sendAttackMessage(battle, DragonAttack.SLOW_DOWN, "所有人", "所有人都将保持慢速&e" + duration + "&7秒");
        return true;
    }

}
