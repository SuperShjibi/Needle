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

    /* 去除用&代替§的颜色 */
    public static String stripUnformatted(String s) {
        return colorPattern.matcher(s).replaceAll("");
    }

    /* 去除用§表示的颜色 */
    public static String stripFormatted(String s) {
        return stripPattern.matcher(s).replaceAll("");
    }

    /* 将s中符合hexPattern的子字符串替换成我的世界中表示十六进制颜色的字符串 */
    public static String hexColor(String s) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = hexPattern.matcher(s);
        while (matcher.find()) {
            String color = matcher.group(1);
            StringBuilder formatted = new StringBuilder("§x");
            for (char c : color.toCharArray()) {
                formatted.append("§").append(c);
            }
            matcher.appendReplacement(result, formatted.toString());
        }
        return matcher.appendTail(result).toString();
    }

    /* 对字符串s进行color()和hexColor() */
    public static String fullyColorize(String s) {
        return hexColor(color(s));
    }


}
