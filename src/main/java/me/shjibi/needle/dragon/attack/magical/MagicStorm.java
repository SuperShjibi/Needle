package me.shjibi.needle.dragon.attack.magical;

import me.shjibi.needle.dragon.attack.Attacker;
import me.shjibi.needle.dragon.attack.DragonAttack;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.spigot.DragonUtil;
import org.bukkit.*;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MagicStorm implements Attacker, Listener {

    private static final double RADIUS = 3.5;
    private static final int BLOCKS = 32;

    private static final List<FallingBlock> blocks = new ArrayList<>();

    @Override
    public boolean attack(DragonBattle battle) {
        Player p = JavaUtil.randomElement(DragonUtil.getAllFighters(battle));
        if (p == null) return false;

        blocks.clear();

        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 5, 1);
        p.damage(RADIUS);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 2, 5, false, false, false));

        for (int i = 0; i < BLOCKS; i++) {
            Location loc = p.getLocation().clone().add(
                    RADIUS * Math.cos(2 * Math.PI * i / BLOCKS),
                    i * 0.75,
                    RADIUS * Math.sin(2 * Math.PI * i / BLOCKS)
                );
            FallingBlock block = spawnFallingBlock(Material.LIGHT_BLUE_TERRACOTTA, loc,
                    new Vector(0, -0.1, 0)
            );
            blocks.add(block);
        }

        for (int i = 0; i < BLOCKS * 2; i++) {
            Location loc = p.getLocation().clone().add(0, i * 0.5, 0);
            FallingBlock block = spawnFallingBlock(Material.OBSIDIAN, loc,
                    new Vector(0, -0.1, 0)
            );
            blocks.add(block);
        }

        AreaEffectCloud cloud = p.getWorld().spawn(p.getLocation(), AreaEffectCloud.class);
        cloud.setSource(battle.getEnderDragon());
        cloud.addCustomEffect(new PotionEffect(
                PotionEffectType.HARM,
                5,
                2,
                false,
                false,
                false
        ), true);
        cloud.setRadius((float) RADIUS);
        cloud.setDuration(2 * 20);
        cloud.setColor(Color.PURPLE);

        DragonUtil.sendAttackMessage(battle, DragonAttack.MAGIC_STORM, p.getName());

        return true;
    }

    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent e) {
        if (!(e.getEntity() instanceof FallingBlock block)) return;
        if (!blocks.contains(block)) return;
        e.setCancelled(true);
        e.getEntity().remove();
        blocks.remove(block);
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
