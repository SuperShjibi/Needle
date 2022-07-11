package me.shjibi.needle.dragon.attack;

import me.shjibi.needle.dragon.DragonType;
import me.shjibi.needle.dragon.attack.magical.*;
import me.shjibi.needle.dragon.attack.strong.*;
import me.shjibi.needle.dragon.attack.tank.*;
import org.bukkit.boss.DragonBattle;

public enum DragonAttack {

    DAMAGE_ABSORB("伤害吸收", DragonType.TANK, new DamageAbsorb()),
    TNT_LAUNCHER("坦克发射", DragonType.TANK, new TNTLauncher()),
    SLOW_DOWN("重量增加", DragonType.TANK, new SlowDown()),

    DRAGON_PUNCH("巨龙拳击", DragonType.STRONG, new DragonPunch()),
    HARD_SMASH("强力敲击", DragonType.STRONG, new HardSmash()),
    ARROW_SPAM("力量射击", DragonType.STRONG, new ArrowSpam()),

    STUN_ATTACK("晕厥术", DragonType.MAGICAL, new StunAttack()),
    MAGIC_STORM("魔法风暴", DragonType.MAGICAL, new MagicStorm()),
    BLACK_MAGIC("黑魔法", DragonType.MAGICAL, new BlackMagic());

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
