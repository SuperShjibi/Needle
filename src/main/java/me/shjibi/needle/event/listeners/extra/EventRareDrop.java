package me.shjibi.needle.event.listeners.extra;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

import static me.shjibi.needle.utils.SpigotUtil.giveItem;
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
        int obsidianCount = p.getStatistic(Statistic.MINE_BLOCK, Material.OBSIDIAN);
        if (obsidianCount < 1200) return;
        else if (obsidianCount == 1200) {
            p.sendMessage(color("&6你已挖掘1200黑曜石！"));
            p.sendMessage(color("&6接下来你在&o十分有效率&o地挖掘黑曜石时，有几率触发一个&9&l稀有事件&6！"));
        }

        if (item.getItemMeta() == null) return;
        if (!item.getType().name().contains("_PICKAXE")) return;
        if (!item.containsEnchantment(Enchantment.DIG_SPEED)) return;
        int enchantmentLevel = item.getEnchantmentLevel(Enchantment.DIG_SPEED);
        if (enchantmentLevel < 5) return;

        int bonusChance = p.getStatistic(Statistic.MINE_BLOCK, Material.OBSIDIAN) / 1200;
        bonusChance = Math.min(12, bonusChance);
        boolean upgrade = random.nextInt(1000) > (1000 - bonusChance);

        if (!upgrade) return;

        if (enchantmentLevel == 5) {
            item.removeEnchantment(Enchantment.DIG_SPEED);
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.DIG_SPEED, 6, true);
            item.setItemMeta(meta);
            p.sendMessage(color("&9&l稀有事件: &7你的镐被升级成了效率VI!"));
        } else {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            if (meta == null) return;
            meta.addStoredEnchant(Enchantment.DIG_SPEED, 6, true);
            giveItem(p, book);
            p.sendMessage(color("&9&l稀有事件: &7你的镐已经是效率VI了，所以掉落了一本效率VI的附魔书!"));
        }
    }

}
