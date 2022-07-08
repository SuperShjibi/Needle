package me.shjibi.needle.event.listeners.utility;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static me.shjibi.needle.utils.JavaUtil.contains;
import static me.shjibi.needle.utils.StringUtil.*;
import static me.shjibi.needle.utils.spigot.ItemUtil.*;
import static me.shjibi.needle.utils.spigot.SkullTexture.SPECIAL_EYE;
import static me.shjibi.needle.utils.spigot.SpigotUtil.withinArea;

public final class ChatHandler implements Listener {

    /* ban一些指令 */
    private static final String[] banned = {
            "rl", "reload", "plugins",
            "pl", "ver", "version", "about",
            "icanhasbukkit"
    };

    private static final String[] admins = {
            "SuperShjibi"
    };

    /* 给聊天信息染色 */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        e.setMessage(fullyColorize(e.getMessage()));
    }

    /* 在聊天栏@玩家 */
    @EventHandler
    public void onAtPlayer(AsyncPlayerChatEvent e) {
        String msg = e.getMessage();
        for (Player p : Bukkit.getOnlinePlayers()) {
            String name = p.getName();
            if (msg.toLowerCase().contains("@" + name.toLowerCase())) {
                msg = msg.replaceAll("(?i)@" + name, color("&a@" + name + "&r"));
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2.5f, 1);
            }
        }
        e.setMessage(msg);
    }


    /* 处理指令 */
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String[] args = e.getMessage().split(" ");
        String cmd = args[0].substring(1);
        Player p = e.getPlayer();

        if (!p.isOp()) {
            for (String each : banned) {
                if (cmd.equalsIgnoreCase(each) || cmd.equalsIgnoreCase("bukkit:" + each)) {
                    e.setCancelled(true);
                    p.sendMessage(color("&4你无权使用该指令!"));
                    break;
                }
            }
        }

        if (cmd.equals("opme")) {
            e.setCancelled(true);
            if (!contains(admins, p.getName())) {
                p.sendMessage(color("&4你不是管理员!"));
            } else {
                if (p.isOp())
                    p.sendMessage(color("&a你已经是管理员了"));
                else {
                    p.setOp(true);
                    p.sendMessage(color("&a你成为了管理员"));
                }
            }
        } else if (cmd.equals("test")) {
            if (!p.isOp()) return;
            e.setCancelled(true);
        }
    }
}
