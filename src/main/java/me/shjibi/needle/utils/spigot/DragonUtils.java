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

    public static boolean isNormalDragon(EnderDragon dragon) {
        return dragon.getDragonBattle() == null || dragon.getHealth() == 200;
    }

    public static DragonType getDragonType(EnderDragon dragon) {
        if (dragon.getBossBar() == null) return null;
        String text = dragon.getBossBar().getTitle();
        for (DragonType type : DragonType.values()) if (type.getName().equals(text)) return type;
        return null;
    }

    public static ChatColor toChatColor(BarColor color) {
        if (color == BarColor.PURPLE) return ChatColor.DARK_PURPLE;
        if (color == BarColor.PINK) return ChatColor.LIGHT_PURPLE;
        return ChatColor.valueOf(color.toString());
    }

    public static String getDragonTalkPrefix(DragonType type) {
        return toChatColor(type.getColor()) + "[" + type.getName() + "]: " + ChatColor.GRAY;
    }

    public static String randomDragonTalk(DragonType type, String category) {
        List<String> talks = dragonTalks.getStringList(type.toString().toLowerCase() + "." + category);
        talks.remove(lastTalk);
        lastTalk = JavaUtil.randomElement(talks);
        return getDragonTalkPrefix(type) + fullyColorize(lastTalk);
    }

    public static DragonAttack randomDragonAttack(DragonType type) {
        List<DragonAttack> attacks = new ArrayList<>(Arrays.stream(DragonAttack.values()).filter(attack -> attack.getType() == type).toList());
        if (attacks.size() != 1) {
            attacks.remove(lastAttack);
            lastAttack = JavaUtil.randomElement(attacks);
        }
        return lastAttack;
    }

    public static String randomDragonAttackMessage(DragonAttack attack) {
        List<String> talks = dragonTalks.getStringList("attacks." + attack.toString().toLowerCase());
        return getDragonTalkPrefix(attack.getType()) + fullyColorize(JavaUtil.randomElement(talks));
    }

    public static void sendAttackMessage(DragonBattle battle, DragonAttack attack, String target) {
        DragonType type = attack.getType();
        String color = toChatColor(type.getColor()).toString();
        String msg = color + type.getName() + ChatColor.GRAY + "对" + ChatColor.GOLD + target +
                ChatColor.GRAY + "使用了" + color + ChatColor.BOLD + attack.getName();
        battle.getBossBar().getPlayers().forEach(p -> p.sendMessage(msg));
    }

    public static void sendAttackMessage(DragonBattle battle, DragonAttack attack, String target, String effect) {
        DragonType type = attack.getType();
        String color = toChatColor(type.getColor()).toString();
        String msg = color + type.getName() + ChatColor.GRAY + "对" + ChatColor.GOLD + target + ChatColor.GRAY + "使用了" + color + ChatColor.BOLD + attack.getName() + ChatColor.GRAY + ", " + effect;
        battle.getBossBar().getPlayers().forEach(p -> p.sendMessage(msg));
    }

    public static List<Player> getAllFighters(DragonBattle battle) {
        List<Player> players = battle.getBossBar().getPlayers();
        List<Player> copied = new ArrayList<>(players);
        copied.removeIf(p -> p.getGameMode() != GameMode.SURVIVAL && p.getGameMode() != GameMode.ADVENTURE);
        return copied;
    }


}
