package me.shjibi.needle.utils;

import me.shjibi.needle.Main;
import me.shjibi.needle.event.listeners.extra.EventRarity;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

import static me.shjibi.needle.utils.StringUtil.*;

public class SpigotUtil {

    private SpigotUtil() {}

    /*
    把AdvancementProgress(成就进度)获取的Criterion(目标)字符串翻译成中文
    第一个String -> 原文, 第二个String -> 译文
    */
    private static final Map<String, String> advancementTranslation = new HashMap<>();
    private static List<String> allAdvancementNames = null;  // 存储以减少卡顿

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
            Main.getInstance().getLogger().log(Level.SEVERE, "无法加载翻译!");
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
        if (allAdvancementNames == null) {
            allAdvancementNames = new ArrayList<>();
            Iterator<Advancement> iter = Bukkit.getServer().advancementIterator();
            while (iter.hasNext()) {
                String name = iter.next().getKey().getKey();
                if (!name.startsWith("recipes/")) allAdvancementNames.add(name);
            }
        }
        return allAdvancementNames;
    }

    /* 获取所有进度(成就)的名称(除了配方进度),存到数组里 */
    public static String[] getAdvancementNamesArray() {
        List<String> names = getAdvancementNameList();
        String[] result = new String[names.size()];
        return names.toArray(result);
    }

    /* 把传入Collection里的所有criteria翻译 */
    public static List<String> translateAdvancements(Collection<String> raw) {  // 应该是'translateCriteria'
        List<String> result = new ArrayList<>();
        for (String str : raw) {
            String translated = advancementTranslation.get(str);
            result.add(translated == null ? str : translated); // 如果没有该翻译,就用原文(应该每一个都翻译了..吧)
        }
        return result;
    }


    /* 用Bukkit的方法获取Advancement */
    public static Advancement getAdvancementByName(String name) {
        return Bukkit.getAdvancement(NamespacedKey.minecraft(name));
    }

    /* 通过名字获取离线玩家 */
    public static OfflinePlayer getOfflinePlayer(String name) {
        for (OfflinePlayer p : Main.getInstance().getServer().getOfflinePlayers()) {
            if (name.equals(p.getName())) return p;
        }
        return null;
    }

    /* 获取服务器版本号 */
    public static String getVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf(".") + 1);
    }

    /* 获取NMS类 */
    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            System.out.println("无法找到类! (" + name + ")");
            return null;
        }
    }

    /* 获取craft bukkit类 */
    public static Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            System.out.println("无法找到类! (" + name + ")");
            return null;
        }
    }

    /* 把指定物品添加到指定玩家背包中,如果背包满了则丢在地上 */
    public static void giveItem(Player p, ItemStack item) {
        if (p == null || item == null) return;
        boolean full = p.getInventory().firstEmpty() == -1;
        if (!full) {
            p.getInventory().addItem(item);
        } else {
            p.getWorld().dropItemNaturally(p.getLocation(), item);
        }
    }

    /* 向指定玩家发送多条消息 */
    public static void sendMessages(Player p, String... messages) {
        for (String msg : messages) {
            p.sendMessage(fullyColorize(msg));
        }
    }

    /* 是否是附魔书 */
    public static boolean isEnchantmentBook(ItemStack item) {
        if (item == null) return false;
        if (item.getItemMeta() == null) return false;
        return item.getItemMeta() instanceof EnchantmentStorageMeta;
    }

    /* 是否是OP附魔书 (是否有一个附魔的等级超过最大附魔等级) */
    public static boolean isOPEnchantmentBook(ItemStack item) {
        if (!isEnchantmentBook(item)) return false;
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        if (meta == null) return false;
        for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
            if (entry.getKey().getMaxLevel() < entry.getValue()) return true;
        }
        return false;
    }

    /* 获取一本OP附魔书 */
    public static ItemStack getOPEnchantmentBook(Enchantment enchantment, int extra) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        if (meta == null) return null;
        meta.addStoredEnchant(enchantment, enchantment.getMaxLevel() + Math.max(0, extra), true);
        book.setItemMeta(meta);
        return book;
    }

    public static void broadcastRandomEvent(EventRarity rarity, String text, Player winner) {
        playNoticeSound(winner);
        String winnerName = winner.getName();
        text = color("&7" + text);
        String prefix = "&c&l全服通告! " + rarity.getText() + ": ";

        for (Player online : Bukkit.getOnlinePlayers()) {
            String name = online.getName().equals(winnerName) ? "你" : winnerName;
            online.sendMessage(color(prefix + text.replace("{name}", name)));
        }
    }

    public static void broadcastRandomEvent(EventRarity rarity, String text, Player winner, boolean notice) {
        if (notice) playNoticeSound(winner);
        String winnerName = winner.getName();
        text = color("&7" + text);
        String prefix = "&c&l全服通告! " + rarity.getText() + ": ";

        for (Player online : Bukkit.getOnlinePlayers()) {
            String name = online.getName().equals(winnerName) ? "你" : winnerName;
            online.sendMessage(color(prefix + text.replace("{name}", name)));
        }
    }

    public static void broadcastRandomEvent(EventRarity rarity, TextComponent component, Player winner) {
        playNoticeSound(winner);
        String winnerName = winner.getName();
        String text = component.getText();
        component.setText(color("&7" + text));

        for (Player online : Bukkit.getOnlinePlayers()) {
            String name = online.getName().equals(winnerName) ? "你" : winnerName;
            component.setText(text.replace("{name}", name));

            TextComponent prefix = new TextComponent(color("&c&l全服通告! " + rarity.getText() + ": "));
            prefix.addExtra(component);

            online.spigot().sendMessage(prefix);
        }
    }

    public static String getFormattedItemType(ItemStack item) {
        return title(item.getType().name().toLowerCase().replace('_', ' '));
    }

    public static String getFormattedItemType(Material type) {
        return title(type.name().toLowerCase().replace('_', ' '));
    }

    public static TextComponent getItemShowcaseComponent(ItemStack item) {
        String nbt = getItemNBT(item);
        String typeName = getFormattedItemType(item);
        String id = item.getType().getKey().getKey();
        String name;
        int amount = item.getAmount();

        boolean enchanted = false;
        boolean enchantBook = false;

        if (item.getItemMeta() != null) {
            enchanted = !item.getItemMeta().getEnchants().isEmpty();
            enchantBook = item.getItemMeta() instanceof EnchantmentStorageMeta;

            String displayName = item.getItemMeta().getDisplayName();
            name = displayName.equals("") ? typeName : displayName;
        } else {
            name = typeName;
        }

        String itemColor = (enchanted ? "&b" : (enchantBook ? "&e" : "&r"));

        TextComponent component = new TextComponent(color( itemColor + "[" + name + "]"));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(id, amount, ItemTag.ofNbt(nbt))));
        if (amount > 1) component.addExtra(color(itemColor + " x " + amount));

        return component;
    }

    public static String getItemNBT(ItemStack item) {
        try {
            Object nmsItem = item.getClass().getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            Object nbt = nmsItem.getClass().getMethod("s").invoke(nmsItem);
            return (String) nbt.getClass().getMethod("toString").invoke(nbt);
        } catch (ReflectiveOperationException | NullPointerException e) {
            return null;
        }
    }

    public static void playNoticeSound(Player p) {
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                p.playSound(p.getLocation(), i % 2 == 0 ? Sound.BLOCK_NOTE_BLOCK_BIT : Sound.BLOCK_NOTE_BLOCK_COW_BELL
                        , 10f, 1.33484f);
                try {
                    Thread.sleep(120);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    public static boolean isInMainIsland(Location loc) {
        return Math.abs(loc.getBlockX()) < 500 && Math.abs(loc.getBlockZ()) < 500;
    }

    public static ItemStack getSkull(String textures) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (textures == null || textures.isEmpty()) {
            return head;
        }

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        if (headMeta == null) return null;

        try {
            Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
            Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
            Class<?> propertyMapClass = Class.forName("com.mojang.authlib.properties.PropertyMap");
            Object gameProfile = gameProfileClass.getConstructor(UUID.class, String.class).newInstance(UUID.randomUUID(), null);

            Object properties = gameProfileClass.getMethod("getProperties").invoke(gameProfile);
            propertyMapClass.getMethod("put", Object.class, Object.class).invoke(properties, "textures",
                    propertyClass.getConstructor(String.class, String.class).newInstance("textures", textures)
            );

            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, gameProfile);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }

        head.setItemMeta(headMeta);
        return head;
    }

    public static void setUnchangeableName(ItemStack item) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "final_name"),
                PersistentDataType.SHORT, (short) 1);
    }

    public static boolean isNameUnchangeable(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        Short value = meta.getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "final_name"), PersistentDataType.SHORT);
        return value != null && value == (short) 1;
    }

}
