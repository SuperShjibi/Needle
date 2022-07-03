package me.shjibi.needle.event.listeners.extra;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

import static me.shjibi.needle.utils.SpigotUtil.isEnchantmentBook;
import static me.shjibi.needle.utils.SpigotUtil.isOPEnchantmentBook;

public class EventAnvil implements Listener {

    /* 修复了铁砧付不了高等级附魔书的特性 */
    @EventHandler
    public void onEnchant(PrepareAnvilEvent e) {
        ItemStack result = e.getResult();
        ItemStack firstItem = e.getInventory().getItem(0);
        ItemStack secondItem = e.getInventory().getItem(1);  // 获取附魔书槽位的物品

        if (firstItem == null || secondItem == null || result == null) return;
        if (!(secondItem.getItemMeta() instanceof EnchantmentStorageMeta enchantMeta)) return;  // 确保是附魔书

        if ((isOPEnchantmentBook(firstItem) && isEnchantmentBook(secondItem)) || (isEnchantmentBook(firstItem) && isOPEnchantmentBook(secondItem))) {
            e.setResult(new ItemStack(Material.AIR));
            return;
        }

        if (!isOPEnchantmentBook(secondItem)) return;

        Map<Enchantment, Integer> oldEnchants = enchantMeta.getStoredEnchants();
        Map<Enchantment, Integer> newEnchants = new HashMap<>();

        ItemMeta resultMeta = result.getItemMeta();

        if (resultMeta == null) return;

        for (Map.Entry<Enchantment, Integer> entry : oldEnchants.entrySet()) {
            Enchantment enc = entry.getKey();
            if (!enc.canEnchantItem(result)) continue;
            int currentLevel = resultMeta.getEnchantLevel(enc);
            int level = entry.getValue();
            if (currentLevel >= level) continue;
            newEnchants.put(enc, level);
        }

        if (newEnchants.isEmpty()) return;

        for (Map.Entry<Enchantment, Integer> entry : newEnchants.entrySet()) {
                resultMeta.removeEnchant(entry.getKey());
                resultMeta.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        result.setItemMeta(resultMeta);
    }
    
}
