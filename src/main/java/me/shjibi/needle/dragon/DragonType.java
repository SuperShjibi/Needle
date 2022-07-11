package me.shjibi.needle.dragon;

import org.bukkit.boss.BarColor;

public enum DragonType {

    MAGICAL("魔法龙", 380, 1, BarColor.BLUE),  // 12
    TANK("坦克龙", 800, 6, BarColor.YELLOW),
    STRONG("力量龙", 450, 10, BarColor.RED),
    WEIRD("诡异龙", 500, 15, BarColor.GREEN),
    MASTER("大师龙", 650, 25, BarColor.WHITE),
    WEAK("虚弱龙", 150, 1, BarColor.PINK);


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
