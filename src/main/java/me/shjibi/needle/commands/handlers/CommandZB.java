package me.shjibi.needle.commands.handlers;

import me.shjibi.needle.commands.base.PlayerCommandHandler;
import me.shjibi.needle.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.shjibi.needle.utils.SpigotUtil.getWorldName;
import static me.shjibi.needle.utils.StringUtil.color;

public final class CommandZB extends PlayerCommandHandler {


    public CommandZB() {
        super(Main.getInstance(), "zb", 0, null, color("&c该指令只能由玩家执行"));
    }

    @Override
    protected void execute(Player p, Command command, String s, String[] args) {
        Location loc = p.getLocation();
        String world = getWorldName(loc.getWorld());

        String message = color("&b世界: &e" + world + ", 坐标: &6[&b" + loc.getBlockX() + "&e, &b" + loc.getBlockY() + "&e, &b" + loc.getBlockZ() + "&6]");

        if (args.length >= 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                p.sendMessage(color("&a玩家") + args[0] + "&c不存在!");
                return;
            }
            target.sendMessage(color("&6" + p.getName() + "&a向&9你&a公布了自己的&e坐标: "));
            target.sendMessage(message);
        } else {
            Bukkit.broadcastMessage(color("&6" + p.getName() + "&a向全服公布了自己的&e坐标: "));
            Bukkit.broadcastMessage(message);
        }
    }

}
