package me.shjibi.needle.dragon;

import me.shjibi.needle.rare.EventRarity;
import me.shjibi.needle.utils.JavaUtil;
import me.shjibi.needle.utils.StringUtil;
import me.shjibi.needle.utils.spigot.ItemUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public record Loot(ItemStack loot, int chance, EventRarity rarity) {

    private static final PotionEffect[] GOD_EFFECTS = {
            new PotionEffect(PotionEffectType.SPEED, 20 * 60 * 60, 3),
            new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60 * 60, 5),
            new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 60 * 60, 1),
            new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 60 * 60, 1),
            new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 60, 3),
            new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20 * 60 * 60, 3),
            new PotionEffect(PotionEffectType.SATURATION, 20 * 60 * 60, 2),
    };

    private static final Loot[] STRONG_LOOTS = {
        new Loot(new ItemStack(Material.DRAGON_HEAD), 13, EventRarity.RARE),
        new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.DAMAGE_ALL, 1), 20, EventRarity.RARE),
        new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.DAMAGE_ARTHROPODS, 1), 20, EventRarity.RARE),
        new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.DAMAGE_UNDEAD, 1), 20, EventRarity.RARE),
        new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.ARROW_DAMAGE, 1), 20, EventRarity.RARE),
    };

    private static final Loot[] TANK_LOOTS = {
        new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 50, EventRarity.VERY_RARE),
        new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.PROTECTION_EXPLOSIONS, 1), 25, EventRarity.RARE),
        new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.PROTECTION_FALL, 1), 55, EventRarity.VERY_RARE),
        new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.PROTECTION_FIRE, 1), 25, EventRarity.RARE),
        new Loot(ItemUtil.getOPEnchantmentBook(Enchantment.PROTECTION_PROJECTILE, 1), 25, EventRarity.RARE)
    };

    private static final Loot[] MAGICAL_LOOTS = {
        new Loot(getGodPot(), 45, EventRarity.VERY_RARE),
        new Loot(getPotion(new PotionEffect(PotionEffectType.HARM, 10 * 60, 3)), 25, EventRarity.RARE),
        new Loot(getPotion(new PotionEffect(PotionEffectType.POISON, 10 * 25, 3)), 20, EventRarity.COMMON),
        new Loot(getPotion(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 30, 2)), 20, EventRarity.COMMON),
        new Loot(getPotion(new PotionEffect(PotionEffectType.WITHER, 10 * 35, 2)), 20, EventRarity.RARE)
    };

    private static final Loot[] WEAK_LOOTS = {
            new Loot(getPotion(new PotionEffect(PotionEffectType.WEAKNESS, 10 * 60, 3)), 15, EventRarity.COMMON),
            new Loot(getPotion(Material.TIPPED_ARROW, new PotionEffect(PotionEffectType.WEAKNESS, 10 * 60, 3), 64), 15, EventRarity.COMMON),
    };

    private static final Loot[] WEIRD_LOOTS = {

    };

    private static final Loot[] MASTER_LOOTS = {

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
            case MAGICAL -> randomMagicItem();
        };
    }

    public static ItemStack randomSkull() {
        Material material = JavaUtil.randomElement(Material.CREEPER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL);
        return new ItemStack(material, JavaUtil.randomInt(1, 10));
    }

    public static ItemStack randomMagicItem() {
        ItemStack[] potions = {
                new ItemStack(Material.DRAGON_BREATH, JavaUtil.randomInt(1, 64)),
                new ItemStack(Material.SPECTRAL_ARROW, JavaUtil.randomInt(1, 64)),
                new ItemStack(Material.BLAZE_ROD, JavaUtil.randomInt(1, 64)),
                new ItemStack(Material.ENDER_EYE, JavaUtil.randomInt(1, 64)),
                new ItemStack(Material.FERMENTED_SPIDER_EYE, JavaUtil.randomInt(1, 64)),
                new ItemStack(Material.EXPERIENCE_BOTTLE, JavaUtil.randomInt(1, 64)),
        };
        return JavaUtil.randomElement(potions);
    }

    public static ItemStack getGodPot() {
        ItemStack pot = new ItemStack(Material.POTION);
        PotionMeta potMeta = ((PotionMeta) pot.getItemMeta());
        if (potMeta == null || GOD_EFFECTS == null) return pot;
        for (PotionEffect effect : GOD_EFFECTS) {
            potMeta.addCustomEffect(effect, true);
        }
        potMeta.setColor(Color.fromRGB(255, 255, 255));
        pot.setItemMeta(potMeta);
        return pot;
    }

    public static ItemStack getPotion(PotionEffect effect) {
        return getPotion(Material.LINGERING_POTION, effect, 1);
    }

    public static ItemStack getPotion(Material mat, PotionEffect effect, int amount) {
        ItemStack pot = new ItemStack(mat, amount);
        PotionMeta potMeta = ((PotionMeta) pot.getItemMeta());

        if (potMeta == null) return pot;
        potMeta.setDisplayName(StringUtil.color("&c&l神药"));
        potMeta.addCustomEffect(effect, true);

        potMeta.setColor(effect.getType().getColor());
        pot.setItemMeta(potMeta);
        return pot;
    }

}
