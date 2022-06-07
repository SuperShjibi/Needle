package me.shjibi.needle.event;

import me.shjibi.needle.Main;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public final class EventManager {

    private EventManager() {}

    private static final String[] NAMES = {
        "Anvil", "Bed", "Chat", "JoinQuit", "Sit", "Suicide"
    };

    public static void registerListeners() {
        Main plugin = Main.getInstance();
        for (String name : NAMES) {
            try {
                Class<? extends Listener> clazz = (Class<? extends Listener>) Class.forName("me.shjibi.needle.event.listeners.Event" + name);
                plugin.getServer().getPluginManager().registerEvents(clazz.getConstructor().newInstance(), plugin);
            } catch (ReflectiveOperationException ignored) {
                Main.getInstance().getLogger().log(Level.SEVERE, "无法加载事件！");
            }
        }
    }



}
