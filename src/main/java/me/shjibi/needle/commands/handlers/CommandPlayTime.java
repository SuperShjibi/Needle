package me.shjibi.needle.commands.handlers;

import me.shjibi.needle.commands.base.PlayerCommandHandler;
import me.shjibi.needle.Main;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.shjibi.needle.utils.StringUtil.color;

public final class CommandPlayTime extends PlayerCommandHandler {

    public CommandPlayTime() {
        super(Main.getInstance(), "playtime", 0, null, color("&c该指令只能由玩家执行"));
    }

    /* 查看自己(或其它玩家)的游玩时间(小时) */
    @Override
    protected void execute(Player p, Command command, String s, String[] args) {
        if (args.length == 0) {
            float playTime = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20f / 60f / 60f;
            p.sendMessage(color(String.format("&a你游玩了&6%.3f&a小时", playTime)));
        } else {
            String targetName = args[0];
            Player target = Bukkit.getPlayerExact(targetName);

            if (target == null) {
                p.sendMessage(color("&c该玩家不在线！"));
                return;
            }

            float playTime = target.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20f / 60f / 60f;
            p.sendMessage(color(String.format("&6%s&a游玩了&6%.3f&a小时", targetName, playTime)));
        }
    }

}
