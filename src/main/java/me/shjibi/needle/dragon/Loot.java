package me.shjibi.needle.dragon;

import me.shjibi.needle.rare.EventRarity;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.spigot.ItemUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public record Loot(ItemStack loot, int chance, EventRarity rarity) {

    private static final Loot[] STRONG_LOOTS = {
            new Loot(new ItemStack(Material.DRAGON_HEAD), 13, EventRarity.RARE),
            new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.DAMAGE_ALL, 1), 20, EventRarity.RARE),
            new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.DAMAGE_ARTHROPODS, 1), 20, EventRarity.RARE),
            new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.DAMAGE_UNDEAD, 1), 20, EventRarity.RARE),
            new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.ARROW_DAMAGE, 1), 20, EventRarity.RARE),
    };

    private static final Loot[] TANK_LOOTS = {

    };

    private static final Loot[] WEAK_LOOTS = {

    };

    private static final Loot[] WEIRD_LOOTS = {

    };

    private static final Loot[] MASTER_LOOTS = {

    };

    private static final Loot[] MAGICAL_LOOTS = {

    };

    public ItemStack getLoot() {
        return loot;
    }

    public int getChance() {
        return chance;
    }

    public EventRarity getRarity() {
        return rarity;
    }

    /** 获取稀有龙战利品(不一定每次都出的) */
    public static Loot[] getRareDragonLoots(DragonType type) {
        return switch (type) {
            case STRONG -> STRONG_LOOTS;
            case TANK -> TANK_LOOTS;
            case WEAK -> WEAK_LOOTS;
            case WEIRD -> WEIRD_LOOTS;
            case MASTER -> MASTER_LOOTS;
            case MAGICAL -> MAGICAL_LOOTS;
        };
    }

    public static ItemStack getCommonDragonLoot(DragonType type) {
        return switch (type) {
            case STRONG -> new ItemStack(Material.END_STONE, JavaUtil.randomInt(1, 64));
            case TANK -> new ItemStack(Material.OBSIDIAN, JavaUtil.randomInt(1, 64));
            case WEAK -> new ItemStack(Material.RED_SAND, JavaUtil.randomInt(1, 64));
            case WEIRD -> new ItemStack(Material.ENDER_PEARL, JavaUtil.randomInt(1, 16));
            case MASTER -> randomSkull();
            case MAGICAL -> randomPotion();
        };
    }

    private static ItemStack randomSkull() {
        Material material = JavaUtil.randomElement(Material.CREEPER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL);
        return new ItemStack(material, JavaUtil.randomInt(1, 10));
    }

    private static ItemStack randomPotion() {
        ItemStack[] potions = {
                new ItemStack(Material.DRAGON_BREATH, JavaUtil.randomInt(1, 64)),
                new ItemStack(Material.SPECTRAL_ARROW, JavaUtil.randomInt(1, 64)),
                new ItemStack(Material.BLAZE_ROD, JavaUtil.randomInt(1, 64)),
                new ItemStack(Material.ENDER_EYE, JavaUtil.randomInt(1, 64)),
                new ItemStack(Material.FERMENTED_SPIDER_EYE, JavaUtil.randomInt(1, 64)),
        };
        return JavaUtil.randomElement(potions);
    }

}
