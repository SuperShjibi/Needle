package me.shjibi.needle.commands.base;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;


/* 玩家指令(只有玩家才可以使用的指令) */
public abstract class PlayerCommandHandler extends BaseCommandHandler {

    private final String notPlayerMessage;

    public PlayerCommandHandler(JavaPlugin plugin, String name, int minArgs, String usage, String notPlayerMessage) {
        super(plugin, name, minArgs, usage == null ? null : usage.split("\n"));
        this.notPlayerMessage = notPlayerMessage;
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(notPlayerMessage);
            return true;
        }

        if (args.length < minArgs) {
            sendUsage(sender);
            return true;
        }
        execute(p, command, label, args);
        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(notPlayerMessage);
            return new ArrayList<>();
        }
        return completeTab(p, command, label, args);
    }

    public List<String> completeTab(Player sender, Command command, String label, String[] args) {return null;}

    protected abstract void execute(Player sender, Command command, String label, String[] args);

}
