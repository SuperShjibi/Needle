package me.shjibi.needle.commands.handlers;

import me.shjibi.needle.commands.base.PlayerCommandHandler;
import me.shjibi.needle.Main;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.List;

import static me.shjibi.needle.utils.JavaUtil.allContains;
import static me.shjibi.needle.utils.spigot.SpigotUtil.*;
import static me.shjibi.needle.utils.StringUtil.color;

public final class CommandProgress extends PlayerCommandHandler {

    public CommandProgress() {
        super(Main.getInstance(), "progress", 1, color("&c参数错误!\n&a/$label &a<成就名> &9<是否显示未完成的(可选,默认显示已完成的)>  &7查看你的成就进度(已完成的部分/未完成的部分)"));
    }

    @Override
    public List<String> completeTab(Player player, Command command, String s, String[] args) {
        if (args.length == 1) {
            return allContains(args[0], getAdvancementNamesArray());
        } else if (args.length == 2) {
            return allContains(args[1], "true", "false");
        }
        return null;
    }

    /* 查看成就的进度(已完成标准 或 未完成标准) */
    @Override
    protected void execute(Player p, Command command, String s, String[] args) {
        Advancement advancement = getAdvancementByName(args[0]);

        if (advancement == null) {
            p.sendMessage(color("&c该成就不存在"));
            return;
        }

        boolean showRemaining = false; // 则显示未完成的
        if (args.length > 1) {
            showRemaining = Boolean.parseBoolean(args[1]);
        }

        AdvancementProgress progress = p.getAdvancementProgress(advancement);

        String listString = translateAdvancements((showRemaining ? progress.getRemainingCriteria() : progress.getAwardedCriteria())).toString();
        listString = listString.equals("[]") ? "无" : listString;

        p.sendMessage(color("&a你&9" + (showRemaining ? "未" : "已") + "&a完成的进度: &6" + listString.replace(",", color("&a,&6"))));

    }
}
