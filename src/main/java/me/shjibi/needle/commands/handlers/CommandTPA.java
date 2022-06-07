package me.shjibi.needle.commands.handlers;

import me.shjibi.needle.Main;
import me.shjibi.needle.commands.base.PlayerCommandHandler;
import me.shjibi.needle.commands.tpa.TPAManager;
import me.shjibi.needle.commands.tpa.TeleportRequest;
import me.shjibi.needle.commands.tpa.TeleportType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.Objects;

import static me.shjibi.needle.commands.tpa.TeleportType.HERE;
import static me.shjibi.needle.commands.tpa.TeleportType.THERE;
import static me.shjibi.needle.utils.StringUtil.color;
import static me.shjibi.needle.utils.StringUtil.stripColor;

public final class CommandTPA extends PlayerCommandHandler {


    public CommandTPA() {
        super(Main.getInstance(), "tpa", 1, color("&c用法: /指令 <玩家>"), color("该指令只能由玩家执行"));
    }

    
    /* 处理tpa, tpahere, tpaccept, tpadeny */
    @Override
    protected void execute(Player p, Command command, String label, String[] args) {
        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            p.sendMessage(color("&c该玩家不存在"));
            return;
        }

        if (target.getName().equals(p.getName())) {
            p.sendMessage(color("&c你不能对自己使用这条指令"));
            return;
        }

        if (label.equalsIgnoreCase("tpa") || label.equalsIgnoreCase("tpahere")) {
            createRequest(label, p, target);
        } else if (label.equalsIgnoreCase("tpaccept")) {
            boolean[] shouldAcceptResult = checkRequest(target, p);
            if (!shouldAcceptResult[0]) return;
            Player from = shouldAcceptResult[1] ? target : p;
            Player to = shouldAcceptResult[1] ? p : target;
            TeleportType type = shouldAcceptResult[1] ? THERE : HERE;

            acceptRequest(from, to, type);

            boolean[] shouldDeleteResult = checkRequest(p, target, false);
            if (!shouldDeleteResult[0]) return;
            if (shouldDeleteResult[1] == shouldAcceptResult[1]) {
                TPAManager.getInstance().removeRequest(new TeleportRequest(to, from, 0L, type));
                p.sendMessage(color("&c由于你已经同意了对方的请求，所以你给对方的请求被删除了"));
            }
        } else if (label.equalsIgnoreCase("tpadeny")) {
            boolean[] results = checkRequest(target, p);
            if (!results[0]) return;
            TeleportRequest request = TPAManager.getInstance().getRequest(target, p, results[1] ? THERE : HERE);
            TPAManager.getInstance().removeRequest(request);
            p.sendMessage(color("&a成功拒绝"));
            target.sendMessage(color("&c对方已拒绝"));
        }
    }

    /* 创建TeleportRequest */
    private void createRequest(String label, Player p, Player target) {
        boolean typeBool = label.equalsIgnoreCase("tpa"); // true则为tpa，否则为tpahere
        TeleportType type = typeBool ? THERE : HERE;  // 获取枚举常量
        // 判断目的地和起始地
        Player from = typeBool ? p : target;  
        Player to = typeBool ? target : p;

        // 判断对方是否发送了对应的请求，如果是，则直接同意
        TeleportType anotherType = type == THERE ? HERE : THERE;
        if (TPAManager.getInstance().containsRequest(from, to, anotherType)) {
            acceptRequest(from, to, anotherType);
            return;
        }

        // 创建请求
        TeleportRequest request = new TeleportRequest(from, to, System.currentTimeMillis(), type);

        // 判断是否已有请求，如果有则进行提示
        if (!TPAManager.getInstance().containsRequest(request))
            TPAManager.getInstance().addRequest(request);
        else {
            p.sendMessage(color("&c你已经给该玩家发送过" + (typeBool ? "传送" : "拉人") + "请求了"));
            return;
        }

        String senderMessage = typeBool ? "&a你给&6" + target.getName() + "&a发送了传送请求！" : "&9你给&6" + target.getName() + "&9发送了拉人请求！";
        String receiverMessage = typeBool ?  "&6" + p.getName() + "&a给你发送了传送请求" :  "&6" + p.getName() + "&9给你发送了拉人请求";

        p.sendMessage(color(senderMessage));
        target.sendMessage(color(receiverMessage));

        
        // 发送同意/拒绝
        TextComponent accept = new TextComponent(color("&a[同意]"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(color("&a&o点击同意"))));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + p.getName()));

        int length = stripColor(receiverMessage).length();

        TextComponent deny = new TextComponent(color("&c[拒绝]"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(color("&c&o点击拒绝"))));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + p.getName()));

        TextComponent whiteSpace = new TextComponent(String.format("%" + length + "s", ""));
        whiteSpace.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
        whiteSpace.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("")));

        accept.addExtra(whiteSpace);
        accept.addExtra(deny);

        target.spigot().sendMessage(accept);
    }


    /* 同意请求 */
    private void acceptRequest(Player from, Player to, TeleportType type) {
        TeleportRequest request = TPAManager.getInstance().getRequest(from, to, type);
        boolean result = request != null && request.accept();
        if (!result)
            from.sendMessage(color((request != null ? "&a对方已下线" : "&c请求已过期")));
        else {
            from.sendMessage(color("&a已传送!"));
            to.sendMessage(color("&a已同意传送请求!"));
        }

        TPAManager.getInstance().removeRequest(request);
    }

    /* 检查请求，返回布尔数组，包含{是否存在该请求(bool), 请求类型(bool)} */
    private boolean[] checkRequest(Player target, Player p) {
        return checkRequest(target, p, true);
    }

    /* 检查请求，返回布尔数组，包含{是否存在该请求(bool), 请求类型(bool)} */
    private boolean[] checkRequest(Player target, Player p, boolean message) {
        boolean typeBool;
        boolean exists = (typeBool = TPAManager.getInstance().containsRequest(target, p, THERE)) ||
                TPAManager.getInstance().containsRequest(p, target, HERE);

        if (!exists && message) p.sendMessage(color("&a对方没有向你发送&6传送&a/&9拉人&a请求!"));
        return new boolean[] {exists, typeBool};
    }


    /* 重写了register，因为要注册多个指令 */
    @Override
    public void register() {
        for (String command : TPAManager.COMMANDS) {
            PluginCommand pluginCmd = Objects.requireNonNull(plugin.getCommand(command));
            pluginCmd.setExecutor(this);
            pluginCmd.setTabCompleter(this);
        }
    }

}
