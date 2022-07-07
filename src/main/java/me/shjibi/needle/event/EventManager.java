package me.shjibi.needle.event;

import me.shjibi.needle.Main;
import me.shjibi.needle.event.listeners.extra.*;
import me.shjibi.needle.event.listeners.extra.rare.BlockMineHandler;
import me.shjibi.needle.event.listeners.extra.rare.DragonFightHandler;
import me.shjibi.needle.event.listeners.extra.rare.EndermanKillHandler;
import me.shjibi.needle.event.listeners.fun.*;
import me.shjibi.needle.event.listeners.utility.*;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public final class EventManager {

    private EventManager() {}

    /* 所有Listener的类 */
    private static final Class<?>[] LISTENERS = {
        SitHandler.class, SuicideHandler.class, BedEventHandler.class,
        ChatHandler.class, JoinQuitHandler.class, AnvilHandler.class,
        BlockMineHandler.class, DragonFightHandler.class, EndermanKillHandler.class,

    };


    /* 利用反射注册Listeners */
    public static void registerListeners() {
        Main plugin = Main.getInstance();
        try {
            for (Class<?> clazz : LISTENERS) {
                Object obj = clazz.getConstructor().newInstance();
                if (!(obj instanceof Listener listener)) continue;
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
            }
        } catch (ReflectiveOperationException e) {
            Main.getInstance().getLogger().log(Level.SEVERE, "无法加载事件!");
        }

    }



}
