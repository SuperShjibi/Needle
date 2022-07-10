package me.shjibi.needle.dragon.attack;

import me.shjibi.needle.dragon.DragonType;
import me.shjibi.needle.dragon.attack.strong.ArrowSpam;
import me.shjibi.needle.dragon.attack.strong.HardSmash;
import me.shjibi.needle.dragon.attack.strong.DragonPunch;
import org.bukkit.boss.DragonBattle;

public enum DragonAttack {

    DRAGON_PUNCH("巨龙拳击", DragonType.STRONG, new DragonPunch()),
    HARD_SMASH("强力敲击", DragonType.STRONG, new HardSmash()),
    ARROW_SPAM("力量射击", DragonType.STRONG, new ArrowSpam());

    private final String name;
    private final DragonType type;
    private final Attacker attack;

    DragonAttack(String name, DragonType type, Attacker attack) {
        this.name = name;
        this.type = type;
        this.attack = attack;
    }

    public String getName() {
        return name;
    }

    public DragonType getType() {
        return type;
    }

    public boolean attack(DragonBattle battle) {
        return attack.attack(battle);
    }
}
