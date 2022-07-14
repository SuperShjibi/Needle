package me.shjibi.needle.dragon;

import me.shjibi.needle.Main;
import me.shjibi.needle.dragon.attack.DragonAttack;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.shjibi.needle.utils.JavaUtil.*;
import static me.shjibi.needle.utils.spigot.DragonUtil.*;
import static me.shjibi.needle.utils.spigot.SpigotUtil.setMaxHealth;

public class DragonFight implements Listener {

    private static final long ATTACK_COOLDOWN = 20;
    private static final Map<String, Double> damageMap = new HashMap<>();

    private static DragonBattle dragonBattle;

    private static DragonAttack lastAttack;
    private static EnderDragon currentDragon;
    private static DragonType currentType;

    private static final BukkitRunnable attackTask = new BukkitRunnable() {
        @Override
        public void run() {
            if (dragonBattle == null || currentDragon == null) return;
            if (!roll()) return;
            lastAttack = randomDragonAttack(currentType);
            if (lastAttack != null) {
                boolean result = lastAttack.attack(currentDragon.getDragonBattle());
                if (result) {
                    sendTalkSafely(dragonBattle.getBossBar().getPlayers(), randomDragonAttackMessage(lastAttack), lastAttack.getName());
                }
            }
        }
    };


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

        if (!(entity instanceof EnderDragon)) return;
        currentDragon = (EnderDragon) entity;
        if (currentDragon.getDragonBattle() == null) return;

        dragonBattle = currentDragon.getDragonBattle();

        currentType = DragonType.WEAK;
        for (DragonType type : DragonType.values()) {
            if (roll(type.getChance())) {
                currentType = type;
                break;
            }
        }

        BossBar bar = dragonBattle.getBossBar();

        bar.setColor(currentType.getColor());
        bar.setTitle(currentType.getName());
        bar.setStyle(BarStyle.SEGMENTED_6);

        currentDragon.setCustomName(currentType.getName());

        String spawnMessage = randomDragonTalk(currentType, "spawn");
        sendTalkSafely(bar.getPlayers(), spawnMessage, "龙生成对话");

        setMaxHealth(currentDragon, currentType.getMaxHealth());

        attackTask.runTaskTimer(Main.getInstance(), ATTACK_COOLDOWN * 20, ATTACK_COOLDOWN * 20);
    }

    @EventHandler
    public void onDragonChangePhase(EnderDragonChangePhaseEvent e) {
        if (e.getEntity().getTicksLived() == 0) return;
        if (dragonBattle == null) return;

        EnderDragon.Phase phase = e.getNewPhase();

        if (currentType == null) return;

        if (phase == EnderDragon.Phase.DYING) return;

        BossBar bar = currentDragon.getBossBar();
        if (bar == null) return;

        List<Player> players = bar.getPlayers();

        phase = handleNewPhase(phase);
        e.setNewPhase(phase);

        if (contains(DRAGON_TALK_PHASES, phase)) {
            sendTalkSafely(players, randomDragonTalk(currentType, "phase"), "龙改变状态");
        }

    }

    @EventHandler
    public void onDamageDragon(EntityDamageByEntityEvent e) {
        if (e.getEntityType() != EntityType.ENDER_DRAGON) return;
        if (!(e.getEntity() instanceof EnderDragon)) return;

        if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
            e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            e.setCancelled(true);
        }

        Player p = null;
        if (e.getDamager() instanceof Player) {
            p = (Player) e.getDamager();
        } else if (e.getDamager() instanceof Projectile proj) {
            if (proj.getShooter() instanceof Player) p = (Player) proj.getShooter();
        }
        if (p == null) return;
        if (damageMap.containsKey(p.getName())) {
            double damage = damageMap.get(p.getName());
            damageMap.put(p.getName(), e.getFinalDamage() + damage);
        } else {
            damageMap.put(p.getName(), e.getFinalDamage());
        }
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent e) {
        if (e.getEntityType() != EntityType.ENDER_DRAGON) return;
        if (!(e.getEntity() instanceof EnderDragon dragon)) return;

        DragonType type = getDragonType(dragon);
        BossBar bossbar = dragon.getBossBar();

        giveLoot(dragonBattle, damageMap);
        sendDamageMap(dragonBattle, damageMap);

        if (bossbar != null) sendTalkSafely(bossbar.getPlayers(), randomDragonTalk(type, "death"), type.getName() + "死亡");
        onDisable();
    }

    private static EnderDragon.Phase handleNewPhase(EnderDragon.Phase phase) {
        List<Player> players = dragonBattle.getBossBar().getPlayers();

        switch (currentType) {
            case STRONG:
                if (phase == EnderDragon.Phase.FLY_TO_PORTAL && roll()) {
                    String category = "stay_circling";
                    sendTalkSafely(players, randomDragonTalk(currentType, category), category);
                    return randomElement(EnderDragon.Phase.STRAFING, EnderDragon.Phase.CIRCLING);
                }
                return phase;
            case TANK:
                if (phase == EnderDragon.Phase.CIRCLING && roll(5)) {
                    String category = "to_portal";
                    sendTalkSafely(players, randomDragonTalk(currentType, category), category);
                    return EnderDragon.Phase.LAND_ON_PORTAL;
                }
                if (phase == EnderDragon.Phase.LEAVE_PORTAL && roll(3)) {
                    String category = "stay_portal";
                    sendTalkSafely(players, randomDragonTalk(currentType, category), category);
                    return EnderDragon.Phase.LAND_ON_PORTAL;
                }
                return phase;
            default:
                return phase;
        }
    }

    public static void fetchDragonInfo() {
        World end = Bukkit.getWorld("world_the_end");
        if (end == null) return;
        dragonBattle = end.getEnderDragonBattle();
        if (dragonBattle == null || dragonBattle.getEnderDragon() == null) return;
        currentDragon = dragonBattle.getEnderDragon();
        currentType = getDragonType(currentDragon);
        attackTask.runTaskTimer(Main.getInstance(), 0, ATTACK_COOLDOWN * 20);
    }

    public static void onDisable() {
        dragonBattle = null;
        currentDragon = null;
        if (lastAttack != null) lastAttack.getAttack().onDisable();
    }
}
