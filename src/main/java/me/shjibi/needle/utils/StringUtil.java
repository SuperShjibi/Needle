package me.shjibi.needle.utils;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

    private StringUtil() {}

    private static final Pattern colorPattern = Pattern.compile("&([\\da-fk-or])");
    private static final Pattern stripPattern = Pattern.compile("§([\\da-fk-or])");
    private static final Pattern hexPattern = Pattern.compile("\\{#([a-fA-F\\d]{6})}");

    /** 标题化字符串 */
    public static String title(String s) {
        String[] parts = s.split(" ");
        StringBuilder sb = new StringBuilder(s.length());
        for(String part : parts){
            if(part.length() > 1 )
                sb.append( part.substring(0, 1).toUpperCase()).append( part.substring(1).toLowerCase() );
            else
                sb.append(part.toUpperCase());
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    /** 给字符串上色 */
    public static String color(String s) {
        return colorPattern.matcher(s).replaceAll("§$1");
    }

    /** 去除 "&a" 这样的颜色 */
    public static String stripUnformatted(String s) {
        return colorPattern.matcher(s).replaceAll("");
    }

    /** 去除 "§a" 这样的颜色 */
    public static String stripFormatted(String s) {
        return stripPattern.matcher(s).replaceAll("");
    }

    /** 将s中符合hexPattern的子字符串替换成我的世界中表示十六进制颜色的格式 */
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

    /** 对字符串s进行color()和hexColor() */
    public static String fullyColorize(String s) {
        return hexColor(color(s));
    }

    /** 通过给定的String返回一个TextComponent */
    public static TextComponent toTextComponent(String s) {
        return new TextComponent(TextComponent.fromLegacyText(s));
    }


}
