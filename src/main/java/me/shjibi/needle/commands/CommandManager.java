package me.shjibi.needle.commands;

import me.shjibi.needle.commands.base.BaseCommandHandler;
import me.shjibi.needle.Main;

import java.util.logging.Level;

public final class CommandManager {

    private CommandManager() {}

    private static final String[] NAMES = {
            "PlayTime", "Progress", "Suicide", "TPA", "ZB"
    };

    public static void registerHandlers() {
        for (String name : NAMES) {
            try {
                Class<? extends BaseCommandHandler> clazz = (Class<? extends BaseCommandHandler>) Class.forName("me.shjibi.needle.commands.handlers.Command" + name);
                clazz.getConstructor().newInstance().register();
            } catch (ReflectiveOperationException ignored) {
                Main.getInstance().getLogger().log(Level.SEVERE, "无法加载指令！");
            }
        }
    }

}
