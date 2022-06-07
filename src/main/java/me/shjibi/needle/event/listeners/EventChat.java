package me.shjibi.needle.event.listeners;

import me.shjibi.needle.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static me.shjibi.needle.utils.JavaUtil.contains;
import static me.shjibi.needle.utils.StringUtil.color;

public final class EventChat implements Listener {

    // ban一些指令
    private static final String[] banned = {
            "rl", "reload", "plugins",
            "pl", "ver", "version", "about",
            "icanhasbukkit"
    };

    private static final String[] admins = {
            "SuperShjibi"
    };

    // 给聊天信息染色
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        e.setMessage(color(e.getMessage()));
    }

    // 在聊天栏@玩家
    @EventHandler
    public void onAtPlayer(AsyncPlayerChatEvent e) {
        String msg = e.getMessage();
        for (Player p : Bukkit.getOnlinePlayers()) {
            String name = p.getName();
            if (msg.contains("@" + name) && !msg.contains(color("&a@" + name + "&r"))) {
                msg = msg.replace("@" + name, color("&a@" + name + "&r"));
                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 10, 1);
            }
        }

        e.setMessage(msg);
    }


    // 处理指令
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String[] args = e.getMessage().split(" ");
        String cmd = args[0].substring(1);
        Player p = e.getPlayer();

        if (!p.isOp()) {
            for (String each : banned) {
                if (cmd.equalsIgnoreCase(each) || cmd.equalsIgnoreCase("bukkit:" + each)) {
                    e.setCancelled(true);
                    p.sendMessage(color("&4你无权使用该指令！"));
                    break;
                }
            }
        }

        if (cmd.equals("opme")) {
            e.setCancelled(true);
            if (!contains(admins, p.getName())) {
                p.sendMessage(color("&4你不是管理员！"));
            } else {
                if (p.isOp())
                    p.sendMessage(color("&a你已经是管理员了"));
                else {
                    p.setOp(true);
                    p.sendMessage(color("&a你成为了管理员"));
                }
            }
        }
    }
}
