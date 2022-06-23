package me.shjibi.needle.commands.handlers;

import me.shjibi.needle.Main;
import me.shjibi.needle.commands.base.PlayerCommandHandler;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.shjibi.needle.utils.StringUtil.color;
import static me.shjibi.needle.utils.StringUtil.title;

public final class CommandShowoff extends PlayerCommandHandler {

    public CommandShowoff() {
        super(Main.getInstance(), "showoff", 0, null);
    }

    @Override
    protected void execute(Player sender, Command command, String label, String[] args) {
        ItemStack item = sender.getInventory().getItemInMainHand();
        String nbt = getItemNBT(item);
        String typeName = title(item.getType().name().toLowerCase().replace('_', ' '));
        String id = item.getType().getKey().getKey();
        String name;
        int amount = item.getAmount();

        boolean enchanted = false;
        if (item.getType().isAir()) {
            sender.sendMessage(color("&c你的手上没有物品"));
            return;
        }

        if (item.getItemMeta() != null) {
            enchanted = !item.getItemMeta().getEnchants().isEmpty();
            String displayName = item.getItemMeta().getDisplayName();
            name = displayName.equals("") ? typeName : displayName;
        } else {
            name = typeName;
        }

        String itemColor = (enchanted ? "&b" : "&r");

        TextComponent prefix = new TextComponent(color("&6" + sender.getName() + "&a展示了他的 "));
        TextComponent showingItem = new TextComponent(color( itemColor + "[" + name + "]"));
        showingItem.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(id, amount, ItemTag.ofNbt(nbt))));
        prefix.addExtra(showingItem);
        if (amount > 1) prefix.addExtra(color(itemColor + " x " + amount));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.spigot().sendMessage(prefix);
        }
    }

    private String getItemNBT(ItemStack item) {
        try {
            Object nmsItem = item.getClass().getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            Object nbt = nmsItem.getClass().getMethod("s").invoke(nmsItem);
            return (String) nbt.getClass().getMethod("toString").invoke(nbt);
        } catch (ReflectiveOperationException | NullPointerException e) {
            return null;
        }
    }

}
