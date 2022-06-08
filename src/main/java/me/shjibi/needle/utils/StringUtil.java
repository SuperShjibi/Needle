package me.shjibi.needle.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

    private StringUtil() {}

    private static final Pattern colorPattern = Pattern.compile("&([0-9a-fk-or])");
    private static final Pattern stripPattern = Pattern.compile("§([0-9a-fk-or])");
    private static final Pattern hexPattern = Pattern.compile("\\{#([a-fA-F0-9]{6})}");

    /* 给字符串上色 */
    public static String color(String s) {
        return colorPattern.matcher(s).replaceAll("§$1");
    }

    /* 去除没有格式化(&->§)的颜色 */
    public static String stripUnformattedColor(String s) {
        return colorPattern.matcher(s).replaceAll("");
    }

    public static String stripColor(String s) {
        return stripPattern.matcher(s).replaceAll("");
    }

    public static String hexColor(String s) {
        Matcher matcher = hexPattern.matcher(s);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String color = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : color.toCharArray()) {
                replacement.append("§").append(c);
            }
            matcher.appendReplacement(result, replacement.toString());
        }
        return matcher.appendTail(result).toString();
    }

    public static String fullColorize(String s) {
        return hexColor(color(s));
    }

    public static String stripHexColor(String s) {
        return hexPattern.matcher(s).replaceAll("");
    }


}
