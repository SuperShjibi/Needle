package me.shjibi.needle.event.listeners.extra;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

import static me.shjibi.needle.utils.SpigotUtil.*;
import static me.shjibi.needle.utils.StringUtil.color;

public class EventRareDrop implements Listener {

    private static final Random random = new Random();

    /*
    * 在挖掘的黑曜石数量>=1200后，使用效率5的镐子挖掘黑曜石时，有概率升级镐子至效率6
    * */
    @EventHandler
    public void onMineObsidian(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.OBSIDIAN) return;

        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (!item.getType().name().contains("_PICKAXE")) return;

        int obsidianCount = p.getStatistic(Statistic.MINE_BLOCK, Material.OBSIDIAN) + 1;  // 因为这次挖掘还没录入统计信息，所以得手动加1
        if (obsidianCount < 1200) return;

        else if (obsidianCount == 1200) sendMessages(p,
                "&6你已经挖掘了1200个黑曜石！",
                           "&e接下来你有几率在: ",
                           "&6&a&o十分有效率地&6挖掘&a&o黑曜石&6时，触发&9&l稀有事件&6！");

        if (item.getItemMeta() == null) return;
        if (!item.containsEnchantment(Enchantment.DIG_SPEED)) return;
        int enchantmentLevel = item.getEnchantmentLevel(Enchantment.DIG_SPEED);
        if (enchantmentLevel < 5) return;

        int bonusChance = p.getStatistic(Statistic.MINE_BLOCK, Material.OBSIDIAN) / 1200;
        bonusChance = Math.min(12, bonusChance);
        boolean lucky = random.nextInt(512) > (511 - bonusChance);

        if (!lucky) return;

        String luckyPlayer = p.getName();

        if (enchantmentLevel == 5) {
            item.removeEnchantment(Enchantment.DIG_SPEED);
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.DIG_SPEED, 6, true);
            item.setItemMeta(meta);
            for (Player online : Bukkit.getOnlinePlayers()) {
                String name = online.getName().equals(luckyPlayer) ? "你" : luckyPlayer;
                p.sendMessage(color("&c&l全服通告！&9&l稀有事件: &7" + name + "的镐子被升级成了效率VI！"));
            }
        } else {
            ItemStack book = getOPEnchantmentBook(Enchantment.DIG_SPEED, 1);
            giveItem(p, book);
            for (Player online : Bukkit.getOnlinePlayers()) {
                String name = online.getName().equals(luckyPlayer) ? "你" : luckyPlayer;
                online.sendMessage(color("&c&l全服通告！&9&l稀有事件: &7" + name + "在用效率VI的镐子挖掘黑曜石时获得了一本效率VI的附魔书！"));
            }
        }
    }

    @EventHandler
    public void onKillDragon(EntityDeathEvent e) {
        if (e.getEntityType() != EntityType.ENDER_DRAGON) return;
        Player p = e.getEntity().getKiller();

        if (p == null) return;

        boolean lucky = random.nextInt(20) == 1;
        if (!lucky) return;

        ItemStack book = getOPEnchantmentBook(Enchantment.DAMAGE_ALL, 1);
        giveItem(p, book);

        String luckyPlayer = p.getName();

        for (Player online : Bukkit.getOnlinePlayers()) {
            String name = online.getName().equals(luckyPlayer) ? "你" : luckyPlayer;
            online.sendMessage(color("&c&l全服通告！&9&l稀有事件: &7" + name + "获得了末影龙掉落的锋利VI附魔书!"));
        }
    }

    @EventHandler
    public void onMineGoldOre(BlockBreakEvent e) {
        if (!e.getBlock().getType().name().contains("GOLD_ORE")) return;
        Player p = e.getPlayer();

        boolean lucky = random.nextInt(2048) == 1;
        if (!lucky) return;

        int amount = random.nextBoolean() ? 1 : 2;
        ItemStack notchApple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, amount);
        giveItem(p, notchApple);

        String luckyPlayer = p.getName();

        for (Player online : Bukkit.getOnlinePlayers()) {
            String name = online.getName().equals(luckyPlayer) ? "你" : luckyPlayer;
            online.sendMessage(color("&c&l全服通告！&6&l罕见事件: &7" + name + "挖掘了带有魔法的金矿，获得了" + amount + "个附魔金苹果!"));
        }
    }

}
