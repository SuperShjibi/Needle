package me.shjibi.needle.utils;

import java.util.regex.Pattern;

public final class StringUtil {

    private StringUtil() {}

    private static final Pattern colorPattern = Pattern.compile("(&([0-9a-fk-o]|r))");

    // 用正则匹配格式化代码(&a,&7这种)
    public static String color(String s) {
        return colorPattern.matcher(s).replaceAll("§$2");
    }

    public static String stripColor(String s) {
        return colorPattern.matcher(s).replaceAll("");
    }


}
