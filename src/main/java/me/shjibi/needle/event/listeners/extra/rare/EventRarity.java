package me.shjibi.needle.event.listeners.extra.rare;

import static me.shjibi.needle.utils.StringUtil.color;

public enum EventRarity {

    COMMON("&a&l普通事件"), RARE("&9&l稀有事件"), VERY_RARE("&6&l罕见事件");

    private final String text;
    EventRarity(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
