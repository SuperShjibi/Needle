package me.shjibi.needle;

import me.shjibi.needle.commands.CommandManager;
import me.shjibi.needle.dragon.DragonFight;
import me.shjibi.needle.event.EventManager;
import me.shjibi.needle.utils.spigot.DragonUtil;
import me.shjibi.needle.utils.spigot.SpigotUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    /**
    唯一的实例
    */
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        SpigotUtil.loadTranslation();
        DragonUtil.loadDragonTalks();
        DragonFight.fetchDragonInfo();

        CommandManager.registerHandlers();
        EventManager.registerListeners();

        getLogger().info("启动了Needle插件~");
    }

    @Override
    public void onDisable() {
        DragonFight.onDisable();
        instance = null;
        getLogger().info("禁用了Needle插件~");
    }

    /** 获取插件实例 */
    public static Main getInstance() {
        return instance;
    }

}
