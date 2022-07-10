package me.shjibi.needle.dragon.attack.strong;

import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.dragon.attack.DragonAttack;

import me.shjibi.needle.utils.spigot.DragonUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.List;
import static me.shjibi.needle.utils.JavaUtil.*;
import static me.shjibi.needle.utils.spigot.DragonUtils.getAllFighters;

public class ArrowSpam implements Attacker {

    @Override
    public boolean attack(DragonBattle battle) {
        List<Player> players = getAllFighters(battle);
        players.removeIf(p -> p.getGameMode() != GameMode.SURVIVAL && p.getGameMode() != GameMode.ADVENTURE);

        if (players.isEmpty()) return false;

        for (Player p : players) {
            shootProjectile(Fireball.class, p.getLocation().add(0, 0.2, 0), battle);
            for (int i = 0; i < randomInt(5, 10); i++) {
                Location loc = p.getLocation().clone().add(
                        (randomBool() ? -1 : 1) * randomDouble(0, 1),
                        randomInt(1, 2),
                        (randomBool() ? -1 : 1) * randomDouble(0, 1)
                );
                shootProjectile(Arrow.class, loc, battle);
            }
        }

        DragonUtils.sendAttackMessage(battle, DragonAttack.ARROW_SPAM, "所有人");
        return true;
    }

    private static void shootProjectile(Class<? extends Projectile> clazz, Location loc, DragonBattle battle) {
        if (loc.getWorld() == null) return;
        Projectile proj = loc.getWorld().spawn(loc, clazz);
        proj.setVelocity(new Vector(0, -1, 0));
        proj.setShooter(battle.getEnderDragon());
        if (clazz == Arrow.class) {
            Arrow arrow = (Arrow) proj;
            arrow.setDamage(5);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
        }
    }
}
