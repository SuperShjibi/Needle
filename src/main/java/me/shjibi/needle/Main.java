package me.shjibi.needle;

import me.shjibi.needle.commands.CommandManager;
import me.shjibi.needle.commands.tpa.TPAManager;
import me.shjibi.needle.event.EventManager;
import me.shjibi.needle.utils.SpigotUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        SpigotUtil.loadTranslation();

        CommandManager.registerHandlers();
        EventManager.registerListeners();

        TPAManager.getInstance().runCleaningTask();

        getLogger().info("启动了Needle插件~");
    }

    @Override
    public void onDisable() {
        instance = null;
        getLogger().info("禁用了Needle插件~");
    }

    public static Main getInstance() {
        return instance;
    }

}
