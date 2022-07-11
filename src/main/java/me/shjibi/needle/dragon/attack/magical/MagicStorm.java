package me.shjibi.needle.dragon.attack.magical;

import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.spigot.DragonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

public class MagicStorm implements Attacker {

    @Override
    public boolean attack(DragonBattle battle) {
        Player p = JavaUtil.randomElement(DragonUtil.getAllFighters(battle));
        if (p == null) return false;
        for (int i = 0; i < 60; i++) {
            FallingBlock block = p.getWorld().spawnFallingBlock(p.getLocation(), Bukkit.createBlockData(Material.OBSIDIAN));
            block.setHurtEntities(true);
            block.setDropItem(false);
            block.setVelocity(p.getLocation().getDirection().normalize().rotateAroundY(i));
        }
        return true;
    }

}
