package me.shjibi.needle.commands.handlers;

import me.shjibi.needle.Main;
import me.shjibi.needle.commands.base.PlayerCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public final class CommandSuicide extends PlayerCommandHandler {

    // 用HashSet保存执行了自杀的人,以实现自定义死亡信息
    public static final Set<String> suiciders = new HashSet<>();

    public CommandSuicide() {
        super(Main.getInstance(), "suicide", 0, null);
    }


    /* 将指令发送者的生命值设为0 */
    @Override
    protected void execute(Player p, Command cmd, String label, String[] args) {
        suiciders.add(p.getName());
        p.setHealth(0);
    }

}
