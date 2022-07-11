package me.shjibi.needle.dragon.attack.magical;

import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.dragon.attack.DragonAttack;
import me.shjibi.needle.utils.spigot.DragonUtil;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlackMagic implements Attacker {

    private static final List<PotionEffect> effects = new ArrayList<>(Arrays.asList(
            new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1, false, false, false),
            new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 2, false, false, false),
            new PotionEffect(PotionEffectType.POISON, 20 * 5, 1, false, false, false)
    ));

    @Override
    public boolean attack(DragonBattle battle) {
        List<Player> players = DragonUtil.getAllFighters(battle);
        if (players.isEmpty()) return false;
        players.forEach(p -> p.addPotionEffects(effects));
        DragonUtil.sendAttackMessage(battle, DragonAttack.BLACK_MAGIC, "所有人", "现在每个人的身上都有了负面效果");
        return true;
    }

}
