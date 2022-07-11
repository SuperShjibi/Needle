package me.shjibi.needle.utils.spigot;

import me.shjibi.needle.Main;
import me.shjibi.needle.dragon.DragonType;
import me.shjibi.needle.dragon.attack.DragonAttack;
import me.shjibi.needle.utils.JavaUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.DragonBattle;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import static me.shjibi.needle.utils.StringUtil.fullyColorize;

public final class DragonUtils {

    private static YamlConfiguration dragonTalks;
    private static String lastTalk = "";
    private static DragonAttack lastAttack;

    private DragonUtils() {}

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
        if (dragon.getBossBar() == null) return null;
        String text = dragon.getBossBar().getTitle();
        for (DragonType type : DragonType.values()) if (type.getName().equals(text)) return type;
        return null;
    }

    /** 将BarColor转成ChatColor */
    public static ChatColor toChatColor(BarColor color) {
        if (color == BarColor.PURPLE) return ChatColor.DARK_PURPLE;
        if (color == BarColor.PINK) return ChatColor.LIGHT_PURPLE;
        return ChatColor.valueOf(color.toString());
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
        return getDragonTalkPrefix(attack.getType()) + fullyColorize(JavaUtil.randomElement(talks));
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
        String msg = color + type.getName() + ChatColor.GRAY + "对" + ChatColor.GOLD + target + ChatColor.GRAY + "使用了" + color + ChatColor.BOLD + attack.getName() + ChatColor.GRAY + ", " + effect;
        battle.getBossBar().getPlayers().forEach(p -> p.sendMessage(msg));
    }

    /** 获取这场龙战中所有非创造、旁观模式的玩家 */
    public static List<Player> getAllFighters(DragonBattle battle) {
        List<Player> players = battle.getBossBar().getPlayers();
        List<Player> copied = new ArrayList<>(players);
        copied.removeIf(p -> p.getGameMode() != GameMode.SURVIVAL && p.getGameMode() != GameMode.ADVENTURE);
        return copied;
    }
}
