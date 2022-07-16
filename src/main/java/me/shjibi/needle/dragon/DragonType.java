package me.shjibi.needle.dragon;

import org.bukkit.boss.BarColor;

public enum DragonType {

    TANK("坦克龙", 800, 3, BarColor.YELLOW),
    STRONG("力量龙", 450, 5, BarColor.RED),
    MAGICAL("魔法龙", 380, 6, BarColor.BLUE),
    WEAK("虚弱龙", 150, 1, BarColor.PINK),

    /* 还没做 */
    WEIRD("诡异龙", 500, 15, BarColor.GREEN),
    MASTER("大师龙", 650, 25, BarColor.WHITE);

    private final String name;
    private final int maxHealth;
    private final int chance;
    private final BarColor color;

    DragonType(String name, int maxHealth, int chance, BarColor color) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.chance = chance;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getChance() {
        return chance;
    }

    public BarColor getColor() {
        return color;
    }

}
