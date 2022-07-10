package me.shjibi.needle.dragon;

import me.shjibi.needle.dragon.attack.DragonAttack;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;
import java.util.Objects;

import static me.shjibi.needle.utils.JavaUtil.randomElement;
import static me.shjibi.needle.utils.JavaUtil.roll;
import static me.shjibi.needle.utils.spigot.DragonUtils.*;
import static me.shjibi.needle.utils.spigot.SpigotUtil.setMaxHealth;

public class DragonFight implements Listener {

    @EventHandler
    public void onDragonSpawn(CreatureSpawnEvent e) {
        if (e.getEntityType() != EntityType.ENDER_DRAGON) return;
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.COMMAND) return;
        if (e.getLocation().getWorld() == null || e.getLocation().getWorld().getEnvironment() != World.Environment.THE_END) return;

        LivingEntity entity = e.getEntity();
        if (!(entity instanceof EnderDragon dragon)) return;
        if (dragon.getDragonBattle() == null) return;

        DragonBattle battle = dragon.getDragonBattle();

        DragonType dragonType = DragonType.WEAK;
        for (DragonType type : DragonType.values()) {
            if (roll(type.getChance())) {
                dragonType = type;
                break;
            }
        }

        BossBar bar = battle.getBossBar();

        bar.setColor(dragonType.getColor());
        bar.setTitle(dragonType.getName());
        bar.setStyle(BarStyle.SEGMENTED_6);

        dragon.setCustomName(dragonType.getName());

        String spawnMessage = randomDragonTalk(dragonType, "spawn");
        bar.getPlayers().forEach(p -> p.sendMessage(spawnMessage));

        setMaxHealth(dragon, dragonType.getMaxHealth());
    }

    @EventHandler
    public void onDragonChangePhase(EnderDragonChangePhaseEvent e) {
        EnderDragon dragon = e.getEntity();
        Bukkit.broadcastMessage("ticksLived: " + dragon.getTicksLived() + ", phase: " + e.getNewPhase());
        if (dragon.getTicksLived() == 0) return;
        if (isNormalDragon(dragon)) return;
        EnderDragon.Phase phase = e.getNewPhase();
        DragonType type = getDragonType(dragon);
        if (type == null) return;
        if (phase == EnderDragon.Phase.DYING) return;

        List<Player> players = Objects.requireNonNull(dragon.getBossBar()).getPlayers();

        boolean attacked = false;

        if (phase == EnderDragon.Phase.FLY_TO_PORTAL && roll()) {
            phase = randomElement(EnderDragon.Phase.STRAFING, EnderDragon.Phase.CIRCLING);
            if (phase != null) e.setNewPhase(phase);
        }

        if (phase != EnderDragon.Phase.SEARCH_FOR_BREATH_ATTACK_TARGET &&
            phase != EnderDragon.Phase.ROAR_BEFORE_ATTACK &&
            phase != EnderDragon.Phase.STRAFING
            && roll(20, 12)) {
            DragonAttack attack = randomDragonAttack(type);
            if (attack != null) {
                boolean result = attack.attack(dragon.getDragonBattle());
                if (result) {
                    attacked = true;
                    players.forEach(p -> p.sendMessage(randomDragonAttackMessage(attack)));
                }
            }
        }

        if (attacked) return;

        if (phase == EnderDragon.Phase.CIRCLING ||
            phase == EnderDragon.Phase.FLY_TO_PORTAL ||
            phase == EnderDragon.Phase.CHARGE_PLAYER &&
            roll(3)) {
            String dragonTalk = randomDragonTalk(type, "phase");
            players.forEach(p -> p.sendMessage(dragonTalk));
        }

    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent e) {
        if (e.getEntityType() != EntityType.ENDER_DRAGON) return;
        if (!(e.getEntity() instanceof EnderDragon dragon)) return;
        if (isNormalDragon(dragon)) return;

        DragonType type = getDragonType(dragon);
        BossBar bossbar = dragon.getBossBar();

        if (type == null) return;

        String dragonTalk = randomDragonTalk(type, "death");
        Objects.requireNonNull(bossbar).getPlayers().forEach(p -> p.sendMessage(dragonTalk));
    }

}
