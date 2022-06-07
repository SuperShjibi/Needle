package me.shjibi.needle.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JavaUtil {

    private JavaUtil() {}

    /* 对于数组的contains方法 */
    public static <T> boolean contains(T[] array, T element) {
        boolean result = false;
        for (T t : array) {
            if (t.equals(element)) {
                result = true;
                break;
            }
        }
        return result;
    }


    /* 返回一个数组中所有包含了s的字符串 */
    public static List<String> allContains(String s, String... elements) {
        return Stream.of(elements).filter(str -> str.contains(s)).collect(Collectors.toList());
    }

    /* 返回一个数组中所有以prefix开头的字符串 */
    public static List<String> allStartsWith(String prefix, String... elements) {
        return Stream.of(elements).filter(str -> str.startsWith(prefix)).collect(Collectors.toList());
    }

}
