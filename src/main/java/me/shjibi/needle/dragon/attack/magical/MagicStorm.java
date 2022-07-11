package me.shjibi.needle.dragon.attack.magical;

import me.shjibi.needle.Main;
import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.spigot.DragonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MagicStorm implements Attacker {

    @Override
    public boolean attack(DragonBattle battle) {
        Player p = JavaUtil.randomElement(DragonUtil.getAllFighters(battle));
        if (p == null) return false;
        List<FallingBlock> blocks = new ArrayList<>();

        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 5, 1);

        for (int i = 0; i < 10; i++) {
            Location loc = p.getLocation().clone().add(0, i * 0.05, 0);
            FallingBlock block = spawnFallingBlock(Material.LIGHT_BLUE_TERRACOTTA, loc,
                    new Vector(0, -0.5, 0)
            );
            blocks.add(block);
        }

        for (int i = 0; i < 60; i++) {
            Location loc = p.getLocation().clone().add(0, i * 0.05, 0);
            FallingBlock block = spawnFallingBlock(Material.OBSIDIAN, loc,
                    loc.getDirection().normalize().rotateAroundY(i * 6)
            );
            blocks.add(block);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                blocks.forEach(Entity::remove);
            }
        }.runTaskLater(Main.getInstance(), (long) (2.5 * 20L));

        return true;
    }

    public FallingBlock spawnFallingBlock(Material material, Location loc, Vector vector) {
        FallingBlock block = Objects.requireNonNull(loc.getWorld()).spawnFallingBlock(loc, Bukkit.createBlockData(material));
        block.setHurtEntities(true);
        block.setDropItem(false);
        block.setInvulnerable(true);
        block.setVelocity(vector);
        return block;
    }

}
