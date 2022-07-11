package me.shjibi.needle.dragon.attack.magical;

import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.dragon.attack.DragonAttack;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.spigot.DragonUtil;
import org.bukkit.Color;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class StunAttack implements Attacker, Listener {

    private static List<Player> players;
    private static long time;

    @Override
    public boolean attack(DragonBattle battle) {
        players = DragonUtil.getAllFighters(battle);
        if (players.isEmpty()) return false;
        int duration = JavaUtil.randomInt(3, 6);
        time = System.currentTimeMillis() + duration * 1000L;
        players.forEach(p -> {
            AreaEffectCloud cloud = p.getWorld().spawn(p.getLocation(), AreaEffectCloud.class);
            cloud.setColor(Color.AQUA);
            cloud.setDuration(20 * duration - 10);
            cloud.setSource(battle.getEnderDragon());
        });
        DragonUtil.sendAttackMessage(battle, DragonAttack.STUN_ATTACK, "所有人", "在&b" + duration + "&7秒内大家都无法移动了!");
        return true;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (System.currentTimeMillis() > time) return;
        if (!players.contains(e.getPlayer())) return;
        e.setCancelled(true);
    }

}
