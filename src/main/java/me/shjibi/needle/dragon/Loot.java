package me.shjibi.needle.dragon;

import me.shjibi.needle.rare.EventRarity;
import org.bukkit.inventory.ItemStack;

public record Loot(ItemStack loot, int chance, EventRarity rarity, int requirement) {

    public ItemStack getLoot() {
        return loot;
    }

    public int getChance() {
        return chance;
    }

    public EventRarity getRarity() {
        return rarity;
    }

    public int getRequirement() {
        return requirement;
    }

}
