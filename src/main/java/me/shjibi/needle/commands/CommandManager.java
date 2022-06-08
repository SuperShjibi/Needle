package me.shjibi.needle.commands;

import me.shjibi.needle.commands.base.BaseCommandHandler;
import me.shjibi.needle.Main;

import java.util.logging.Level;

public final class CommandManager {

    private CommandManager() {}

    /* 所有指令处理者的名字 */
    private static final String[] names = {
            "PlayTime", "Progress", "Suicide", "TPA", "ZB"
    };


    /* 注册指令处理者(用BaseCommandHandler的register方法) */
    public static void registerHandlers() {
        for (String name : names) {
            try {
                Class<?> clazz = Class.forName("me.shjibi.needle.commands.handlers.Command" + name);
                Object obj = clazz.getConstructor().newInstance();
                if (!(obj instanceof BaseCommandHandler handler)) return;
                handler.register();
            } catch (ReflectiveOperationException ignored) {
                Main.getInstance().getLogger().log(Level.SEVERE, "无法加载指令!");
            }
        }
    }

}
