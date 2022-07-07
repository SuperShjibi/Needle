package me.shjibi.needle.event.listeners.utility;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;

import static me.shjibi.needle.utils.StringUtil.color;
import static me.shjibi.needle.utils.StringUtil.fullyColorize;

public class JoinQuitHandler implements Listener {

    public static final String loginTimeout = "你太长时间未登录了";

    /* 提前发送原版加入消息,并给特殊玩家特殊的入服提示 */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        String joinMessage = e.getJoinMessage();
        String name = e.getPlayer().getName();
        String custom = switch (name) {
            case "Cameraaa" -> "&e彩笔慎做人来辣, 大家快揍他";
            case "SuperShjiba" -> "{#46C8C8}管理员Shjiba进入了! 快把纪关了吧~";
            case "Hello125" -> "{#5A0F96}黑曜石肝帝Hello125来花他的欧气啦!";
            default -> null;
        };

        if (custom != null) Bukkit.broadcastMessage(fullyColorize(custom));
        if (joinMessage != null) Bukkit.broadcastMessage(joinMessage);

        e.setJoinMessage(null);
    }

    /* 登陆超时提示 */
    @EventHandler
    public void onLoginTimeout(PlayerKickEvent e) {
        if (loginTimeout.equals(e.getReason())) Bukkit.broadcastMessage(color("&e" + e.getPlayer().getName() + "因为30秒没有登录被踢出了服务器"));
    }

}
