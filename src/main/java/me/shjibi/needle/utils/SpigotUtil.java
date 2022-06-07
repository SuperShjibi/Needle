package me.shjibi.needle.utils;

import me.shjibi.needle.Main;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class SpigotUtil {

    private SpigotUtil() {}

    /*
    把AdvancementProgress(成就进度)获取的Criterion(目标)字符串翻译成中文
    第一个String -> 原文, 第二个String -> 译文
    */
    private static final Map<String, String> advancementTranslation = new HashMap<>();

    /*
    把translation.txt中的翻译加载到内存
    */
    public static void loadTranslation() {
        try {
            byte[] bytes = Objects.requireNonNull(Main.class.getResourceAsStream("/translation.yml")).readAllBytes();
            String content = new String(bytes, StandardCharsets.UTF_8);

            for (String line : content.split("\r\n")) {
                String[] pair = line.split(": ");
                if (pair.length < 2) continue;
                advancementTranslation.put(pair[0], pair[1]);
            }


        } catch (IOException | NullPointerException e) {
            e.printStackTrace();  // 一般不会出错
            Main.getInstance().getLogger().log(Level.SEVERE, "无法加载翻译！");
        }
    }

    /* 获取世界的中文名  */
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

    /* 获取所有进度(成就)的名称(除了配方进度) */
    public static List<String> getAdvancementNameList() {
        List<String> names = new ArrayList<>();
        Iterator<Advancement> iter = Bukkit.getServer().advancementIterator();
        while (iter.hasNext()) {
            String name = iter.next().getKey().getKey();
            if (!name.startsWith("recipes/")) names.add(name);
        }
        return names;
    }

    /* 获取所有进度(成就)的名称(除了配方进度)，存到数组里 */
    public static String[] getAdvancementNamesArray() {
        List<String> names = getAdvancementNameList();
        String[] result = new String[names.size()];
        return names.toArray(result);
    }

    /* 把传入Collection里的所有criteria翻译 */
    public static List<String> translateAdvancements(Collection<String> raw) {  // should be 'translateCriteria'
        List<String> result = new ArrayList<>();
        for (String str : raw) {
            String translated = advancementTranslation.get(str);
            result.add(translated == null ? str : translated); // 如果没有该翻译，就用原文(应该每一个都翻译了..吧)
        }
        return result;
    }


    /* 用Bukkit的方法获取Advancement */
    public static Advancement getAdvancementByName(String name) {
        return Bukkit.getAdvancement(NamespacedKey.minecraft(name));
    }

}
