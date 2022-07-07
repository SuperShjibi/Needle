package me.shjibi.needle.commands.handlers.tpa;

import org.bukkit.entity.Player;

import static me.shjibi.needle.utils.StringUtil.color;


/* 代表了一个传送请求,包含了起始地(Player),目的地(Player),请求时间(long),以及类型(TeleportType) */
public record TeleportRequest(Player from, Player to, long start, TeleportType type) {

    public static final int REMOVE_DELAY = 60;

    public Player getFrom() {
        if (from.isOnline()) return from;
        return null;
    }

    public Player getTo() {
        if (to.isOnline()) return to;
        return null;
    }

    public TeleportType getType() {
        return type;
    }

    public boolean accept() {
        if (!from.isOnline() || !to.isOnline()) return false;
        if (shouldRemove()) return false;
        from.teleport(to.getLocation());
        return true;
    }

    public boolean shouldRemove() {
        return (System.currentTimeMillis() - start) > REMOVE_DELAY * 1000;
    }

    public void sendRemoveMessage() {
        boolean typeBool = (type == TeleportType.THERE);
        Player reqSender = typeBool ? from : to;
        Player receiver = typeBool ? to : from;
        reqSender.sendMessage(color("&7你发送给&e" + receiver.getName() + "&7的请求已过期!"));
        receiver.sendMessage(color("&e" + reqSender.getName() + "&7发送你的请求已过期!"));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TeleportRequest request)) return false;
        if (this.getFrom() == null || this.getTo() == null || request.getTo() == null || request.getFrom() == null) return false;
        return this.getFrom().getName().equals(request.getFrom().getName()) &&
                this.getTo().getName().equals(request.getTo().getName()) &&
                this.getType() == request.getType();
    }

}
