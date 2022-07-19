package me.shjibi.needle;

import me.shjibi.needle.commands.CommandManager;
import me.shjibi.needle.dragon.DragonFight;
import me.shjibi.needle.event.EventManager;
import me.shjibi.needle.utils.spigot.DragonUtil;
import me.shjibi.needle.utils.spigot.SpigotUtil;
import org.bukkit.plugin.java.JavaPlugin;

/*
*
* TODO: rank
* 给SuperShjiba -> [JIB+++] (JIB颜色: {241, 31, 249}; 加号颜色: {255,25,25}, {50,230,220}, {215,220,60})
*
* */


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
