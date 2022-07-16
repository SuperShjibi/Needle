package me.shjibi.needle.utils.spigot;

import me.shjibi.needle.Main;
import me.shjibi.needle.rare.EventRarity;
import me.shjibi.needle.utils.JavaUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

import static me.shjibi.needle.utils.StringUtil.color;
import static me.shjibi.needle.utils.StringUtil.fullyColorize;

public class SpigotUtil {

    private SpigotUtil() {}

    /**
    AdvancementProgress(成就进度)的Criterion(目标)的翻译
    */
    private static final Map<String, String> advancementTranslation = new HashMap<>();
    /** 所有的进度名 */
    private static List<String> allAdvancementNames = null;

    /**
    把translation.yml中的翻译加载到内存
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

    /** 获取世界的中文名  */
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

    /** 获取所有进度(成就)的名称(除了配方进度) */
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

    /** 获取所有进度(成就)的名称(除了配方进度),存到数组里 */
    public static String[] getAdvancementNamesArray() {
        List<String> names = getAdvancementNameList();
        String[] result = new String[names.size()];
        return names.toArray(result);
    }

    /** 把传入Collection里的所有criteria翻译 */
    public static List<String> translateAdvancements(Collection<String> raw) {
        List<String> result = new ArrayList<>();
        for (String str : raw) {
            String translated = advancementTranslation.get(str);
            result.add(translated == null ? str : translated);
        }
        return result;
    }


    /** 通过名字获取成就 */
    public static Advancement getAdvancementByName(String name) {
        return Bukkit.getAdvancement(NamespacedKey.minecraft(name));
    }

    /** 通过名字获取离线玩家 */
    public static OfflinePlayer getOfflinePlayer(String name) {
        for (OfflinePlayer p : Main.getInstance().getServer().getOfflinePlayers()) {
            if (name.equals(p.getName())) return p;
        }
        return null;
    }

    /** 获取服务器版本号 */
    public static String getVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf(".") + 1);
    }

    /** 获取NMS类 */
    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            System.out.println("无法找到类! (" + name + ")");
            return null;
        }
    }

    /** 获取CraftBukkit类 */
    public static Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            System.out.println("无法找到类! (" + name + ")");
            return null;
        }
    }

    /** 向指定玩家发送多条消息 */
    public static void sendMessages(Player p, String... messages) {
        for (String msg : messages) {
            p.sendMessage(fullyColorize(msg));
        }
    }

    /** 向全服通告随机事件 */
    public static void broadcastRandomEvent(EventRarity rarity, String text, Player winner) {
        broadcastRandomEvent(rarity, text, winner, true);
    }

    /** 全服通告(用TextComponent) */
    public static void broadcastRandomEvent(EventRarity rarity, TextComponent component, Player winner) {
        broadcastRandomEvent(rarity, component, winner, true);
    }

    /** 给指定玩家发送包 */
    public static boolean sendPacket(Player p, Object packet) {
        try {
            Class<?> packetClass = Class.forName("net.minecraft.network.protocol.Packet");
            if (!packetClass.isAssignableFrom(packet.getClass())) return false;
            Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
            Object connection = entityPlayer.getClass().getField("b").get(entityPlayer);
            connection.getClass().getMethod("a", packetClass).invoke(connection, packet);
            return true;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 向全服通告随机事件 */
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

    /** 全服通告(用TextComponent) */
    public static void broadcastRandomEvent(EventRarity rarity, TextComponent component, Player winner, boolean notice) {
        if (notice) playNoticeSound(winner);
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

    /** 播放提示音 */
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

    /** 是否在末地主岛范围内(末地) */
    public static boolean isInMainIsland(Location loc) {
        return Math.abs(loc.getBlockX()) < 500 && Math.abs(loc.getBlockZ()) < 500;
    }

    /** 判断loc是否在提供的范围内 */
    public static boolean withinArea(Location loc, double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        return (minX < x && maxX > x) &&
                (minY < y && maxY > y) &&
                (minZ < z && maxZ > z);
    }

    /** 设置一个LivingEntity的最大生命值 */
    public static void setMaxHealth(LivingEntity entity, double maxHealth) {
        AttributeInstance attrib = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attrib != null) attrib.setBaseValue(maxHealth);
        entity.setHealth(maxHealth);
    }

    /** 设置一个LivingEntity的攻击伤害 */
    public static void setAttackDamage(LivingEntity entity, double attackDamage) {
        AttributeInstance attrib = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attrib != null) attrib.setBaseValue(attackDamage);
    }

    /** 给指定Location添加随机偏移量 */
    public static Location addRandomOffset(Location loc, int min, int max) {
        return addRandomOffset(loc, min, max, min, max, min, max);
    }

    /** 给指定Location添加随机偏移量 */
    public static Location addRandomOffset(Location loc, int xMin, int xMax, int yMin, int yMax, int zMin, int zMax) {
        int xOffset = xMin + JavaUtil.randomInt(0, xMax - xMin);
        int yOffset = yMin + JavaUtil.randomInt(0, yMax - yMin);
        int zOffset = zMin + JavaUtil.randomInt(0, zMax - zMin);
        return new Location(loc.getWorld(), loc.getX() + xOffset, loc.getY() + yOffset, loc.getZ() + zOffset,
                loc.getYaw(), loc.getPitch());
    }
}
