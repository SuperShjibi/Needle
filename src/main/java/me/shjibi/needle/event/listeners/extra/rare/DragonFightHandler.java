package me.shjibi.needle.event.listeners.extra.rare;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import static me.shjibi.needle.utils.JavaUtil.randomInt;
import static me.shjibi.needle.utils.JavaUtil.roll;
import static me.shjibi.needle.utils.SpigotUtil.*;

public final class DragonFightHandler implements Listener {

    private static final Loot[] dragonLoots = initDragonLoots();

    @EventHandler
    public void onKillDragon(EntityDeathEvent e) {
        if (e.getEntityType() != EntityType.ENDER_DRAGON) return;
        Player p = e.getEntity().getKiller();

        if (p == null) return;

        Loot prize = rollLoot();
        if (prize == null) return;

        ItemStack loot = prize.loot;
        giveItem(p, loot);

        TextComponent prefix = new TextComponent("{name}获得了末影龙掉落的战利品: ");
        prefix.addExtra(getItemShowcaseComponent(loot));

        broadcastRandomEvent(prize.rarity, prefix, p);
    }

    private static Loot rollLoot() {
        for (Loot loot : dragonLoots) {
            if (roll(loot.chance)) return loot;
        }
        return null;
    }

    private static Loot[] initDragonLoots() {
        ItemStack book = getOPEnchantmentBook(Enchantment.DAMAGE_ALL, 1);
        ItemStack dragonBreath = new ItemStack(Material.DRAGON_BREATH, randomInt(1, Material.DRAGON_BREATH.getMaxStackSize()));
        ItemStack dragonHead = new ItemStack(Material.DRAGON_HEAD);

        return new Loot[]{
            new Loot(book, 20, EventRarity.RARE),
            new Loot(dragonBreath, 8, EventRarity.COMMON),
            new Loot(dragonHead, 15, EventRarity.RARE)
        };
    }

    public record Loot(ItemStack loot, int chance, EventRarity rarity) {}

}
