package me.shjibi.needle.commands.handlers;

import me.shjibi.needle.Main;
import me.shjibi.needle.commands.base.PlayerCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class CommandShowoff extends PlayerCommandHandler {

    public CommandShowoff() {
        super(Main.getInstance(), "showoff", 0, null);
    }

    @Override
    protected void execute(Player sender, Command command, String label, String[] args) {
        ItemStack item = sender.getInventory().getItemInMainHand();
    }

}
