package me.shjibi.needle.commands;

import me.shjibi.needle.commands.base.BaseCommandHandler;
import me.shjibi.needle.Main;
import me.shjibi.needle.commands.handlers.*;

import java.util.logging.Level;

public final class CommandManager {

    private CommandManager() {}

    /** 所有指令处理者的类 */
    public static final Class<?>[] HANDLERS = {
            CommandPlayTime.class, CommandProgress.class,
            CommandSuicide.class, CommandTPA.class, CommandZB.class,
            CommandShowoff.class
    };


    /** 注册指令处理者(用BaseCommandHandler的register方法) */
    public static void registerHandlers() {
        for (Class<?> clazz : HANDLERS) {
            try {
                Object obj = clazz.getConstructor().newInstance();
                if (!(obj instanceof BaseCommandHandler handler)) return;
                handler.register();
            } catch (ReflectiveOperationException ignored) {
                Main.getInstance().getLogger().log(Level.SEVERE, "无法加载指令!");
            }
        }
    }

}
