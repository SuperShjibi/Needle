package me.shjibi.needle.dragon;

import me.shjibi.needle.dragon.attack.DragonAttack;
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
import org.bukkit.event.entity.*;

import java.util.List;
import java.util.Objects;

import static me.shjibi.needle.utils.JavaUtil.*;
import static me.shjibi.needle.utils.spigot.DragonUtil.*;
import static me.shjibi.needle.utils.spigot.SpigotUtil.setMaxHealth;

public class DragonFight implements Listener {

    @EventHandler
    public void onDragonSpawn(CreatureSpawnEvent e) {
        if (e.getEntityType() != EntityType.ENDER_DRAGON) return;
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.COMMAND) {
            e.setCancelled(true);
            return;
        }

        if (e.getLocation().getWorld() == null || e.getLocation().getWorld().getEnvironment() != World.Environment.THE_END)
            return;

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
        sendTalkSafely(bar.getPlayers(), spawnMessage, "龙生成对话");

        setMaxHealth(dragon, dragonType.getMaxHealth());
    }

    @EventHandler
    public void onDragonChangePhase(EnderDragonChangePhaseEvent e) {
        EnderDragon dragon = e.getEntity();
        DragonBattle battle = dragon.getDragonBattle();

        debug("ticksLived: " + dragon.getTicksLived() + ", phase: " + e.getNewPhase());
        if (dragon.getTicksLived() == 0) return;
        if (battle == null) return;

        EnderDragon.Phase phase = e.getNewPhase();

        DragonType type = getDragonType(dragon);
        if (type == null) return;
        if (phase == EnderDragon.Phase.DYING) return;
        BossBar bar = dragon.getBossBar();
        if (bar == null) return;
        List<Player> players = bar.getPlayers();

        phase = Objects.requireNonNullElse(handleNewPhase(battle, phase, type), phase);
        e.setNewPhase(phase);

        boolean attacked = false;

        if (phase != EnderDragon.Phase.SEARCH_FOR_BREATH_ATTACK_TARGET &&
            phase != EnderDragon.Phase.ROAR_BEFORE_ATTACK &&
            phase != EnderDragon.Phase.STRAFING
            && roll(20, 12)) {
            DragonAttack attack = randomDragonAttack(type);
            if (attack != null) {
                boolean result = attack.attack(dragon.getDragonBattle());
                if (result) {
                    attacked = true;
                    sendTalkSafely(players, randomDragonAttackMessage(attack), attack.getName());
                }
            }
        }

        if (attacked) return;

        if (phase == EnderDragon.Phase.CIRCLING ||
            phase == EnderDragon.Phase.FLY_TO_PORTAL ||
            phase == EnderDragon.Phase.CHARGE_PLAYER &&
            roll(3)) {
            sendTalkSafely(players, randomDragonTalk(type, "phase"), "龙改变状态");
        }

    }

    @EventHandler
    public void onDamageDragon(EntityDamageByEntityEvent e) {
        if (e.getEntityType() != EntityType.ENDER_DRAGON) return;
        if (!(e.getEntity() instanceof EnderDragon dragon)) return;

        DragonType type = getDragonType(dragon);
        if (type == null) return;

        if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
            e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent e) {
        if (e.getEntityType() != EntityType.ENDER_DRAGON) return;
        if (!(e.getEntity() instanceof EnderDragon dragon)) return;

        DragonType type = getDragonType(dragon);
        BossBar bossbar = dragon.getBossBar();

        if (type == null || bossbar == null) return;

        sendTalkSafely(bossbar.getPlayers(), randomDragonTalk(type, "death"), type.getName() + "死亡");
    }

    private static EnderDragon.Phase handleNewPhase(DragonBattle battle, EnderDragon.Phase phase, DragonType type) {
        List<Player> players = battle.getBossBar().getPlayers();

        switch (type) {
            case STRONG:
                if (phase == EnderDragon.Phase.FLY_TO_PORTAL && roll()) {
                    String category = "stay_circling";
                    sendTalkSafely(players, randomDragonTalk(type, category), category);
                    return randomElement(EnderDragon.Phase.STRAFING, EnderDragon.Phase.CIRCLING);
                }
                return phase;
            case TANK:
                if (phase == EnderDragon.Phase.CIRCLING && roll(5)) {
                    String category = "to_portal";
                    sendTalkSafely(players, randomDragonTalk(type, category), category);
                    return EnderDragon.Phase.LAND_ON_PORTAL;
                }
                if (phase == EnderDragon.Phase.LEAVE_PORTAL && roll(3)) {
                    String category = "stay_portal";
                    sendTalkSafely(players, randomDragonTalk(type, category), category);
                    return EnderDragon.Phase.LAND_ON_PORTAL;
                }
                return phase;
            default:
                return phase;
        }
    }

}
