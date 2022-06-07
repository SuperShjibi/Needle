package me.shjibi.needle.commands.tpa;

import org.bukkit.entity.Player;

public record TeleportRequest(Player from, Player to, long start, TeleportType type) {

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
        return (System.currentTimeMillis() - start) > 60 * 1000;
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
