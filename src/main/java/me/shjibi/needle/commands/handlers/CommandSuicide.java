package me.shjibi.needle.commands.handlers;

import me.shjibi.needle.Main;
import me.shjibi.needle.commands.base.PlayerCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

import static me.shjibi.needle.utils.StringUtil.color;

public final class CommandSuicide extends PlayerCommandHandler {

    public static final Set<String> suiciders = new HashSet<>();

    public CommandSuicide() {
        super(Main.getInstance(), "suicide", 0, null, color("&c该指令只能由玩家执行"));
    }

    @Override
    protected void execute(Player p, Command cmd, String label, String[] args) {
        suiciders.add(p.getName());
        p.setHealth(0);
    }

}
