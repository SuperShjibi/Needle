package me.shjibi.needle.custom;

import me.shjibi.needle.Main;
import me.shjibi.needle.dragon.DragonFight;
import me.shjibi.needle.dragon.Loot;
import me.shjibi.needle.utils.spigot.ItemUtil;
import me.shjibi.needle.utils.spigot.SpigotUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import static me.shjibi.needle.utils.JavaUtil.contains;
import static me.shjibi.needle.utils.JavaUtil.debug;
import static me.shjibi.needle.utils.StringUtil.color;
import static me.shjibi.needle.utils.StringUtil.fullyColorize;
import static me.shjibi.needle.utils.spigot.DragonUtil.getDragonType;

public final class ChatHandler implements Listener {

    /* ban一些指令 */
    private static final String[] banned = {
            "rl", "reload", "plugins",
            "pl", "ver", "version", "about",
            "icanhasbukkit"
    };

    private static final String[] admins = {
            "SuperShjibi", "SuperShjiba", "dick_24", "It2Me"
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
            World end = Bukkit.getWorld("world_the_end");
            if (end == null) return;
            DragonBattle dragonBattle = end.getEnderDragonBattle();
            if (dragonBattle == null) return;
            debug("loc: " + dragonBattle.getEndPortalLocation());
        }
    }
}
