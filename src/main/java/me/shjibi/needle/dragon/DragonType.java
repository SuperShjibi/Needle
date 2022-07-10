package me.shjibi.needle.dragon;

import org.bukkit.boss.BarColor;

public enum DragonType {

    STRONG("力量龙", 450, 1, BarColor.RED), // 10
    TANK("坦克龙", 800, 6, BarColor.YELLOW),
    MAGICAL("魔法龙", 380, 12, BarColor.BLUE),
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
