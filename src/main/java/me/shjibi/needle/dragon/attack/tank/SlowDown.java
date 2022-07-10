package me.shjibi.needle.dragon.attack.tank;

import me.shjibi.needle.Main;
import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.dragon.attack.DragonAttack;
import me.shjibi.needle.utils.spigot.DragonUtils;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static me.shjibi.needle.utils.StringUtil.color;

public class SlowDown implements Attacker {

    @Override
    public boolean attack(DragonBattle battle) {
        List<Player> players = DragonUtils.getAllFighters(battle);
        if (players.isEmpty()) return false;
        players.forEach(p -> p.setWalkSpeed(-0.2f));
        new BukkitRunnable() {
            @Override
            public void run() {
                players.forEach(p -> p.setWalkSpeed(0.2f));
            }
        }.runTaskLater(Main.getInstance(), 20 * 5);
        DragonUtils.sendAttackMessage(battle, DragonAttack.SLOW_DOWN, "所有人", color("所有人都将保持慢速&e5&7秒"));
        return true;
    }

}
