package me.shjibi.needle.event;

import me.shjibi.needle.Main;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public final class EventManager {

    private EventManager() {}

    /* 所有Listener的名字 */
    private static final String[] NAMES = {
        "Anvil", "Bed", "Chat", "JoinQuit", "Sit", "Suicide"
    };


    /* 利用反射注册Listeners */
    public static void registerListeners() {
        Main plugin = Main.getInstance();
        for (String name : NAMES) {
            try {
                Class<?> clazz = Class.forName("me.shjibi.needle.event.listeners.Event" + name);
                Object obj = clazz.getConstructor().newInstance();
                if (!(obj instanceof Listener listener)) return;
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
            } catch (ReflectiveOperationException ignored) {
                Main.getInstance().getLogger().log(Level.SEVERE, "无法加载事件！");
            }
        }
    }



}
