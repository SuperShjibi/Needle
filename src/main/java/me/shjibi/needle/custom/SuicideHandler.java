package me.shjibi.needle.custom;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static me.shjibi.needle.commands.handlers.CommandSuicide.suiciders;

public final class SuicideHandler implements Listener {

    /* 更换自杀时的死亡提示 */
    @EventHandler
    public void onSuicide(PlayerDeathEvent e) {
        String name = e.getEntity().getName();
        if (!suiciders.contains(name)) return;
        e.setDeathMessage(name + "自杀了");
        suiciders.remove(name);
    }

}
