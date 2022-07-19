package me.shjibi.needle.utils.spigot;

import me.shjibi.needle.Main;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static me.shjibi.needle.utils.StringUtil.color;
import static me.shjibi.needle.utils.StringUtil.title;

public final class ItemUtil {

    private static final Map<Item, String> hiddenItems = new HashMap<>();

    private ItemUtil() {}

    /** 获取指定材质的头颅 */
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

    /** 获取物品NBT(字符串) */
    public static String getItemNBT(ItemStack item) {
        try {
            Object nmsItem = asNMSCopy(item);
            Object nbt = nmsItem.getClass().getMethod("s").invoke(nmsItem);
            return (String) nbt.getClass().getMethod("toString").invoke(nbt);
        } catch (ReflectiveOperationException | NullPointerException e) {
            return null;
        }
    }

    /** 获取格式化的物品名(传入ItemStack) */
    public static String getFormattedItemType(ItemStack item) {
        return title(item.getType().name().toLowerCase().replace('_', ' '));
    }

    /** 获取格式化的物品名(传入Material) */
    public static String getFormattedItemType(Material type) {
        return title(type.name().toLowerCase().replace('_', ' '));
    }

    /** 获取展示物品的TextComponent */
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

        TextComponent component = new TextComponent(color( itemColor + "[" + name + "&r]"));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new net.md_5.bungee.api.chat.hover.content.Item
                (id, amount, ItemTag.ofNbt(nbt))));
        if (amount > 1) component.addExtra(color(itemColor + " x " + amount));

        return component;
    }

    /** 是否是附魔书 */
    public static boolean isEnchantmentBook(ItemStack item) {
        if (item == null) return false;
        if (item.getItemMeta() == null) return false;
        return item.getItemMeta() instanceof EnchantmentStorageMeta;
    }

    /** 是否是高级附魔书 */
    public static boolean isOPEnchantmentBook(ItemStack item) {
        if (!isEnchantmentBook(item)) return false;
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        if (meta == null) return false;
        for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
            if (entry.getKey().getMaxLevel() < entry.getValue()) return true;
        }
        return false;
    }

    /** 获取一本OP附魔书 */
    public static ItemStack getOPEnchantmentBook(Enchantment enchantment, int extra) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        if (meta == null) return null;
        meta.addStoredEnchant(enchantment, enchantment.getMaxLevel() + Math.max(0, extra), true);
        book.setItemMeta(meta);
        return book;
    }

    /** 把指定物品添加到指定玩家背包中,如果背包满了则丢在地上 */
    public static void giveItem(Player p, ItemStack item) {
        if (p == null || item == null) return;
        boolean full = p.getInventory().firstEmpty() == -1;
        if (!full) {
            p.getInventory().addItem(item);
        } else {
            p.getWorld().dropItemNaturally(p.getLocation(), item);
        }
    }

    /** 掉落物品, 只有指定的玩家才能看到 */
    public static Item dropItem(Location loc, ItemStack itemStack, Player player) {
        return dropItem(loc, itemStack, player.getName());
    }

    /** 掉落物品, 只有指定玩家的名字才能看到 */
    public static Item dropItem(Location loc, ItemStack itemStack, String name) {
        if (loc.getWorld() == null) throw new RuntimeException("无法生成Item: 世界为null");
        Item item = loc.getWorld().dropItem(loc, itemStack);
        hiddenItems.put(item, name);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(name)) continue;
            hideItem(p, item);
        }
        return item;
    }

    /** 处理玩家加入 */
    public static void handlePlayerJoin(Player p) {
        for (Map.Entry<Item, String> entry : hiddenItems.entrySet()) {
            if (p.getName().equals(entry.getValue())) continue;
            hideItem(p, entry.getKey());
        }
    }

    /** 返回是否该取消捡起事件 */
    public static boolean handleItemPickup(Item item, Player p) {
        List<Item> items = hiddenItems.keySet().stream().toList();
        int index = items.stream().map(Entity::getEntityId).toList().indexOf(item.getEntityId());
        if (index == -1) return false;
        boolean result = !hiddenItems.get(items.get(index)).equals(p.getName());
        if (!result) hiddenItems.remove(items.get(index));
        return result;
    }

    private static void hideItem(Player player, Item item) {
        try {
            Class<?> craftPlayerClass = SpigotUtil.getCraftBukkitClass("entity.CraftPlayer");
            if (craftPlayerClass == null) throw new ReflectiveOperationException();
            Method method = craftPlayerClass.getDeclaredMethod("hideEntity0", Plugin.class, Entity.class);
            method.setAccessible(true);
            method.invoke(player, Main.getInstance(), item);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /** 获取NMS的ItemStack */
    public static Object asNMSCopy(ItemStack item) {
        try {
            Class<?> itemClass = SpigotUtil.getCraftBukkitClass("inventory.CraftItemStack");
            if (itemClass == null) throw new ReflectiveOperationException();
            return itemClass.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("无法获取NMSCopy");
        }
    }

}
