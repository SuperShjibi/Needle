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

import static me.shjibi.needle.utils.StringUtil.color;
import static me.shjibi.needle.utils.StringUtil.stripColor;

public final class CommandTPA extends PlayerCommandHandler {


    public CommandTPA() {
        super(Main.getInstance(), "tpa", 1, color("&c用法: /tpa <玩家>"), color("该指令只能由玩家执行"));
    }

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
            boolean[] results = checkRequest(target, p);
            if (!results[0]) return;
            acceptRequest(results[1] ? target : p,  results[1] ? p : target, results[1] ? TeleportType.THERE : TeleportType.HERE);
        } else if (label.equalsIgnoreCase("tpadeny")) {
            boolean[] results = checkRequest(target, p);
            if (!results[0]) return;
            TeleportRequest request = TPAManager.getInstance().getRequest(target, p, results[1] ? TeleportType.THERE : TeleportType.HERE);
            TPAManager.getInstance().removeRequest(request);
            p.sendMessage(color("&a成功拒绝"));
            target.sendMessage(color("&c对方已拒绝"));
        }
    }

    private void createRequest(String label, Player p, Player target) {
        boolean typeBool = label.equalsIgnoreCase("tpa"); // true则为tpa，否则为tpahere
        TeleportType type = typeBool ? TeleportType.THERE : TeleportType.HERE;
        Player from = typeBool ? p : target;
        Player to = typeBool ? target : p;

        if (TPAManager.getInstance().containsRequest(to, from, type)) {
            acceptRequest(from, to, type);
        }

        TeleportRequest request = new TeleportRequest(from, to, System.currentTimeMillis(), type);
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

    private boolean[] checkRequest(Player target, Player p) {
        boolean typeBool;
        boolean exists = (typeBool = TPAManager.getInstance().containsRequest(target, p, TeleportType.THERE)) ||
                TPAManager.getInstance().containsRequest(p, target, TeleportType.HERE);

        if (!exists) p.sendMessage(color("&a对方没有向你发送传送&6/&9拉人&a请求!"));
        return new boolean[] {exists, typeBool};
    }

    @Override
    public void register() {
        for (String command : TPAManager.COMMANDS) {
            PluginCommand pluginCmd = Objects.requireNonNull(plugin.getCommand(command));
            pluginCmd.setExecutor(this);
            pluginCmd.setTabCompleter(this);
        }
    }

}
