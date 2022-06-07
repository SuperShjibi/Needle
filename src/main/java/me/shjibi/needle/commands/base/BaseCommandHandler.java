package me.shjibi.needle.commands.base;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


/* 基本注册指令处理 */
public abstract class BaseCommandHandler implements CommandExecutor, TabCompleter {

    protected final String[] usage;
    protected final int minArgs;
    protected final String name;
    protected final JavaPlugin plugin;


    public BaseCommandHandler(JavaPlugin plugin, String name, int minArgs, String[] usage) {
        this.plugin = Objects.requireNonNull(plugin);
        this.name = Objects.requireNonNull(name);
        this.minArgs = minArgs;
        this.usage = usage;
    }

    /* 注册指令处理 */
    public void register() {
        PluginCommand command = Objects.requireNonNull(plugin.getCommand(name));
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    /* 注册 */
    protected final void sendUsage(CommandSender sender) {
        if (usage == null) return;
        for (String line : usage) {
            sender.sendMessage(line);
        }
    }

    protected Player parsePlayer(String name) {
        return Bukkit.getPlayerExact(name);
    }

    protected Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }


}
