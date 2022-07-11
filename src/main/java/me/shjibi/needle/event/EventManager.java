package me.shjibi.needle.event;

import me.shjibi.needle.Main;
import me.shjibi.needle.custom.*;
import me.shjibi.needle.dragon.DragonFight;
import me.shjibi.needle.dragon.attack.magical.StunAttack;
import me.shjibi.needle.dragon.attack.tank.DamageAbsorb;
import me.shjibi.needle.rare.BlockMineHandler;
import me.shjibi.needle.rare.EndermanKillHandler;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public final class EventManager {

    private EventManager() {}

    /** 所有Listener的类 */
    public static final Class<?>[] LISTENERS = {
        SitHandler.class, SuicideHandler.class, OtherHandler.class,
        ChatHandler.class, JoinQuitHandler.class, AnvilHandler.class,
        BlockMineHandler.class, EndermanKillHandler.class, DragonFight.class,

        StunAttack.class
    };


    /** 利用反射注册Listeners */
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
