package me.shjibi.needle.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JavaUtil {

    private JavaUtil() {}

    // 对于数组的contains方法
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


    // 如果obj为null，返回replacement，否则返回obj
    public static List<String> allContains(String prefix, String... elements) {
        return Stream.of(elements).filter(str -> str.contains(prefix)).collect(Collectors.toList());
    }

}
