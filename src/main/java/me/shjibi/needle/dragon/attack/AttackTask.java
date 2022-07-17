package me.shjibi.needle.dragon.attack;

import me.shjibi.needle.dragon.DragonFight;
import me.shjibi.needle.dragon.DragonType;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.scheduler.BukkitRunnable;

import static me.shjibi.needle.utils.JavaUtil.roll;
import static me.shjibi.needle.utils.spigot.DragonUtil.*;

public class AttackTask extends BukkitRunnable {

    @Override
    public void run() {
        if (DragonFight.getDragonBattle() == null || DragonFight.getCurrentDragon() == null) return;
        if (DragonFight.getCurrentDragon().isDead()) return;

        if (!roll()) return;
        DragonAttack randomAttack = randomDragonAttack(DragonFight.getCurrentType());
        DragonFight.setLastAttack(randomAttack);
        if (randomAttack != null) {
            boolean result = DragonFight.getLastAttack().attack(DragonFight.getDragonBattle());
            if (result) {
                sendTalkSafely(DragonFight.getDragonBattle().getBossBar().getPlayers(), randomDragonAttackMessage(randomAttack), randomAttack.getName());
            }
        }
    }

}
