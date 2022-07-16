package me.shjibi.needle.utils.spigot;

import me.shjibi.needle.Main;
import me.shjibi.needle.dragon.DragonType;
import me.shjibi.needle.dragon.Loot;
import me.shjibi.needle.dragon.attack.DragonAttack;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.StringUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.DragonBattle;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static me.shjibi.needle.utils.JavaUtil.*;
import static me.shjibi.needle.utils.StringUtil.color;
import static me.shjibi.needle.utils.StringUtil.fullyColorize;
import static me.shjibi.needle.utils.spigot.SpigotUtil.addRandomOffset;

public final class DragonUtil {

    private static YamlConfiguration dragonTalks;
    private static String lastTalk = "";
    private static DragonAttack lastAttack;

    public static final EnderDragon.Phase[] DRAGON_TALK_PHASES = {
            EnderDragon.Phase.CIRCLING,
            EnderDragon.Phase.FLY_TO_PORTAL,
            EnderDragon.Phase.CHARGE_PLAYER,
    };

    private DragonUtil() {}

    /** 加载龙的对话 */
    public static void loadDragonTalks()  {
        dragonTalks = new YamlConfiguration();
        try {
            InputStream input = Main.class.getResourceAsStream("/dragon_talks.yml");
            if (input == null) throw new IOException();
            byte[] bytes = input.readAllBytes();
            input.close();

            dragonTalks.loadFromString(new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            Main.getInstance().getLogger().log(Level.SEVERE, "无法加载龙对话!");
        }

    }

    /** 通过BossBar的标题获取龙的类型 */
    public static DragonType getDragonType(EnderDragon dragon) {
        if (dragon.getBossBar() == null) throw new RuntimeException("无法判断龙类型!");
        String text = dragon.getBossBar().getTitle();
        for (DragonType type : DragonType.values()) if (type.getName().equals(text)) return type;
        throw new RuntimeException("无法判断龙类型");
    }

    /** 将BarColor转成ChatColor */
    public static ChatColor toChatColor(BarColor color) {
        return switch (color) {
            case PURPLE -> ChatColor.DARK_PURPLE;
            case PINK -> ChatColor.LIGHT_PURPLE;
            case BLUE -> ChatColor.AQUA;
            default -> ChatColor.valueOf(color.toString());
        };
    }

    /** 获取龙对话的前缀 */
    public static String getDragonTalkPrefix(DragonType type) {
        return toChatColor(type.getColor()) + "[" + type.getName() + "]: " + ChatColor.GRAY;
    }

    /** 从特定的键中抽取一条对话 */
    public static String randomDragonTalk(DragonType type, String category) {
        List<String> talks = dragonTalks.getStringList(type.toString().toLowerCase() + "." + category);
        talks.remove(lastTalk);
        lastTalk = JavaUtil.randomElement(talks);
        if (lastTalk == null) return null;
        return getDragonTalkPrefix(type) + fullyColorize(lastTalk);
    }

    /** 抽取适合type的一个DragonAttack */
    public static DragonAttack randomDragonAttack(DragonType type) {
        List<DragonAttack> attacks = new ArrayList<>(Arrays.stream(DragonAttack.values()).filter(attack -> attack.getType() == type).toList());
        if (attacks.size() != 1) {
            attacks.remove(lastAttack);
            lastAttack = JavaUtil.randomElement(attacks);
        } else {
            lastAttack = attacks.get(0);
        }
        return lastAttack;
    }

    /** 抽取一个适合attack的攻击对话 */
    public static String randomDragonAttackMessage(DragonAttack attack) {
        List<String> talks = dragonTalks.getStringList("attacks." + attack.toString().toLowerCase());
        String message = JavaUtil.randomElement(talks);
        if (message == null) return null;
        return getDragonTalkPrefix(attack.getType()) + fullyColorize(message);
    }

    /** 发送攻击提示(xx龙使用了xx) */
    public static void sendAttackMessage(DragonBattle battle, DragonAttack attack, String target) {
        DragonType type = attack.getType();
        String color = toChatColor(type.getColor()).toString();
        String msg = color + type.getName() + ChatColor.GRAY + "对" + ChatColor.GOLD + target +
                ChatColor.GRAY + "使用了" + color + ChatColor.BOLD + attack.getName();
        battle.getBossBar().getPlayers().forEach(p -> p.sendMessage(msg));
    }

    /** 发送攻击提示(xx龙使用了xx, xx) */
    public static void sendAttackMessage(DragonBattle battle, DragonAttack attack, String target, String effect) {
        DragonType type = attack.getType();
        String color = toChatColor(type.getColor()).toString();
        String msg = color + type.getName() + ChatColor.GRAY + "对" +
                ChatColor.GOLD + target + ChatColor.GRAY + "使用了" +
                color + ChatColor.BOLD + attack.getName() +
                ChatColor.GRAY + ", " + StringUtil.color(effect);

        battle.getBossBar().getPlayers().forEach(p -> p.sendMessage(msg));
    }

    /** 获取这场龙战中所有非创造、旁观模式的玩家 */
    public static List<Player> getAllFighters(DragonBattle battle) {
        List<Player> players = battle.getBossBar().getPlayers();
        List<Player> copied = new ArrayList<>(players);
        copied.removeIf(p -> p.getGameMode() == GameMode.SPECTATOR);
        return copied;
    }

    /** 安全地发送信息(如果dragonTalk为null就不发送) */
    public static void sendTalkSafely(List<Player> players, String dragonTalk, String type) {
        if (dragonTalk != null) players.forEach(p -> p.sendMessage(dragonTalk));
        else logSevere("没有为" + type + "找到合适的龙对话!");
    }

    /** 给予随机龙战利品 */
    public static void giveLoot(DragonBattle battle, Map<String, Double> damageMap) {
        if (battle.getEnderDragon() == null) return;
        Location loc = battle.getEndPortalLocation();
        if (loc == null) return;

        DragonType type = getDragonType(battle.getEnderDragon());

        List<Map.Entry<String, Double>> entries = damageMap.entrySet().stream().sorted(
                (c1, c2) -> c2.getValue().compareTo(c1.getValue())
        ).limit(3).toList();

        Loot[] rareLoots = Loot.getRareDragonLoots(type);
        int maxHealth = type.getMaxHealth();

        for (Map.Entry<String, Double> entry : entries) {
            String player = entry.getKey();
            Location normalLoc = getLootDropLocation(loc);
            debug("loc: " + normalLoc.getX() + ", " + normalLoc.getY() + ", " + normalLoc.getZ());
            Item commonItem = ItemUtil.dropItem(normalLoc, Loot.getCommonDragonLoot(type), player);
            commonItem.setGlowing(true);
            commonItem.setGravity(false);

            double extra = (entry.getValue() + maxHealth) / maxHealth;

            for (Loot rareLoot : rareLoots) {
                int chance = (int) (rareLoot.getChance() / extra);
                if (JavaUtil.roll(chance)) {
                    Item rareItem = ItemUtil.dropItem(getLootDropLocation(loc), rareLoot.getLoot(), player);
                    rareItem.setGlowing(true);
                    rareItem.setGravity(false);

                    TextComponent text = new TextComponent("{name}获得了龙战利品: ");
                    text.addExtra(ItemUtil.getItemShowcaseComponent(rareLoot.getLoot()));
                    SpigotUtil.broadcastRandomEvent(rareLoot.getRarity(), text, Bukkit.getPlayer(player), false);
                }
            }
        }
    }

    public static Location getLootDropLocation(Location portalLoc) {
        System.out.println(portalLoc);
        Location positive = addRandomOffset(portalLoc, 5, 8, 2, 5, 5, 8);
        Location negative = addRandomOffset(portalLoc, -8, -5, 2, 5, -8, -5);
        return JavaUtil.randomBool() ? positive : negative;
    }

    /** 发送伤害排行榜 */
    public static void sendDamageMap(DragonBattle battle, Map<String, Double> damageMap) {
        if (battle.getEnderDragon() == null) return;
        DragonType type = getDragonType(battle.getEnderDragon());

        List<Map.Entry<String, Double>> entries = damageMap.entrySet().stream().sorted(
                (c1, c2) -> c2.getValue().compareTo(c1.getValue())
        ).toList();

        String color = toChatColor(type.getColor()).toString();
        List<Player> players = battle.getBossBar().getPlayers();
        players.forEach(x -> x.sendMessage(color("&7与" + color + type.getName() + "&7的战斗结束了, 下面是&c伤害&7排行榜:")));

        if (entries.size() != 0) {
            for (int i = 0; i < entries.size(); i++) {
                Map.Entry<String, Double> entry = entries.get(i);
                String message = color("&7 - " + color + (i + 1) + ". &6" + entry.getKey() + color + ": &c" + round(entry.getValue(), 1) + "&7 - ");
                players.forEach(p -> p.sendMessage(message));
            }
        } else {
            players.forEach(p -> p.sendMessage(color("&7没有人对龙造成了&c伤害&7! &l*&4&l这很可能是一个bug, 请告诉服务器管理员&l&r*")));
        }
    }
}
