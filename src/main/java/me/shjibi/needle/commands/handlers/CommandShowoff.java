package me.shjibi.needle.commands.handlers;

import me.shjibi.needle.Main;
import me.shjibi.needle.commands.base.PlayerCommandHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.shjibi.needle.utils.StringUtil.color;
import static me.shjibi.needle.utils.spigot.ItemUtil.getItemShowcaseComponent;

public final class CommandShowoff extends PlayerCommandHandler {

    public CommandShowoff() {
        super(Main.getInstance(), "showoff", 0, null);
    }

    @Override
    protected void execute(Player sender, Command command, String label, String[] args) {
        ItemStack item = sender.getInventory().getItemInMainHand();

        if (item.getType().isAir()) {
            sender.sendMessage(color("&c你的手上没有物品"));
            return;
        }

        TextComponent prefix = new TextComponent(color("&6" + sender.getName() + "&a展示了他的 "));
        TextComponent itemComponent = getItemShowcaseComponent(item);
        prefix.addExtra(itemComponent);
        Bukkit.spigot().broadcast(prefix);
    }

}
