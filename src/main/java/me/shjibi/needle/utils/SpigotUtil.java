package me.shjibi.needle.utils;

import me.shjibi.needle.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class SpigotUtil {

    private SpigotUtil() {}

    private static final Map<String, String> advancementTranslation = new HashMap<>();


    public static void loadTranslation() {
        try {
            byte[] bytesC2A = Objects.requireNonNull(Main.class.getResourceAsStream("/criteriaMapping.txt")).readAllBytes();
            String contentC2A = new String(bytesC2A, StandardCharsets.UTF_8);
            Map<String, String> C2A = new HashMap<>();
            for (String line : contentC2A.split("\n")) {
                String[] pair = line.split(": ");
                if (pair.length < 2) continue;
                C2A.put(pair[1], pair[0]);
            }

            byte[] bytes = Objects.requireNonNull(Main.class.getResourceAsStream("/translation.txt")).readAllBytes();
            String content = new String(bytes, StandardCharsets.UTF_8);

            for (String line : content.split("\r\n")) {
                String[] pair = line.split(": ");
                if (pair.length < 2) continue;
                String criterion = C2A.get(pair[0]);
                advancementTranslation.put(criterion == null ? pair[0] : criterion, pair[1]);
            }


        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            Main.getInstance().getLogger().log(Level.SEVERE, "无法加载翻译！");
        }
    }

    // 获取世界的中文名
    public static String getWorldName(World world) {
        if (world == null)
            return "未知";
        if (world.getName().equals("world"))
            return "主世界";
        if (world.getName().equals("world_nether"))
            return "地狱";
        if (world.getName().equals("world_the_end"))
            return "末地";
        return "未知";
    }

    public static List<String> getAdvancementNameList() {
        List<String> names = new ArrayList<>();
        Iterator<Advancement> iter = Bukkit.getServer().advancementIterator();
        while (iter.hasNext()) {
            String name = iter.next().getKey().getKey();
            if (!name.startsWith("recipes/")) names.add(name);
        }
        return names;
    }

    public static String[] getAdvancementNames() {
        List<String> names = getAdvancementNameList();
        String[] result = new String[names.size()];
        return names.toArray(result);
    }

    public static List<String> translateAdvancements(Collection<String> raw) {
        List<String> result = new ArrayList<>();
        for (String str : raw) {
            String translated = advancementTranslation.get(str);
            result.add(translated == null ? str : translated);
        }
        return result;
    }

    public static Advancement getAdvancementByName(String name) {
        Iterator<Advancement> iter = Bukkit.getServer().advancementIterator();
        while (iter.hasNext()) {
            Advancement advancement = iter.next();
            if (advancement.getKey().getKey().equals(name)) return advancement;
        }
        return null;
    }

}
