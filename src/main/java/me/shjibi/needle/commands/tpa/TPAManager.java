package me.shjibi.needle.commands.tpa;

import me.shjibi.needle.Main;
import me.shjibi.needle.commands.handlers.CommandTPA;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/* 管理tpa请求，指令等 */
public final class TPAManager {

    private TPAManager() {
        requests = new ArrayList<>();
    }

    public static final String[] COMMANDS = {
            "tpa", "tpahere", "tpaccept", "tpadeny"
    };

    private static TPAManager instance;

    private List<TeleportRequest> requests;

    public static TPAManager getInstance() {
        if (instance == null) instance = new TPAManager();
        return instance;
    }

    public boolean containsRequest(TeleportRequest request) {
        for(TeleportRequest each : requests) {
            if(each.equals(request)) return true;
        }
        return false;
    }

    public boolean containsRequest(Player from, Player to, TeleportType type) {
        return containsRequest(new TeleportRequest(from, to, 0L, type));
    }

    public void addRequest(TeleportRequest request) {
        requests.add(request);
    }

    public void removeRequest(TeleportRequest request) {
        requests.remove(request);
    }

    public TeleportRequest getRequest(Player from, Player to, TeleportType type) {
        for(TeleportRequest each : requests) {
            if(each.equals(new TeleportRequest(from, to, 0L, type))) return each;
        }
        return null;
    }

    public void runCleaningTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                requests.removeIf(TeleportRequest::shouldRemove);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }



}
