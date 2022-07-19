package me.shjibi.needle.dragon.attack.weird;

import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.utils.JavaUtil;
import org.bukkit.Location;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;

public class WeirdLightning implements Attacker {

    @Override
    public boolean attack(DragonBattle battle) {
        Player player = JavaUtil.randomElement(battle.getBossBar().getPlayers());
        if (player == null) return false;
        player.getWorld().spawn(player.getLocation(), LightningStrike.class);
        for (int i = 0; i < JavaUtil.randomInt(3, 33); i++) {
            float extra = (i % 2 == 0 ? -1 : 1) * (i / 5f);
            player.getWorld().spawn(new Location(
                    player.getWorld(),
                    player.getLocation().getX() + extra,
                    player.getLocation().getY(),
                    player.getLocation().getZ() + extra
                ), LightningStrike.class);
        }
        return true;
    }

}
