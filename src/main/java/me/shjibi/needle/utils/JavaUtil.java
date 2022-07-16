package me.shjibi.needle.utils;

import me.shjibi.needle.Main;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JavaUtil {

    private JavaUtil() {}

    private static final Random random = new Random();

    /** 对于数组的contains方法 */
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


    /** 返回一个数组中所有包含了s的字符串 */
    public static List<String> allContains(String s, String... elements) {
        return Stream.of(elements).filter(str -> str.contains(s)).collect(Collectors.toList());
    }

    /** 返回一个数组中所有以prefix开头的字符串 */
    public static List<String> allStartsWith(String prefix, String... elements) {
        return Stream.of(elements).filter(str -> str.startsWith(prefix)).collect(Collectors.toList());
    }

    /** 抽奖,有 (1 + bonus)/chance 的概率抽中 */
    public static boolean roll(int chance, int bonusLuck) {
        return random.nextInt(chance) >= (chance - 1 - bonusLuck);
    }

    /** 抽奖,有 1/chance 的概率抽中 */
    public static boolean roll(int chance) {
        return roll(chance, 0);
    }

    /** 抽奖,有1/2的概率抽中 */
    public static boolean roll() {
        return roll(2, 0);
    }

    /** 数组中随机一个元素 */
    @SafeVarargs
    public static <T> T randomElement(T... array) {
        if (array.length == 0) throw new ArrayIndexOutOfBoundsException("Empty array!");
        return array[random.nextInt(array.length)];
    }

    /** 列表中随机一个元素 */
    public static <T> T randomElement(List<T> list) {
        if (list.isEmpty()) return null;
        return list.get(random.nextInt(list.size()));
    }

    /** 随机整数 */
    public static int randomInt(int start, int end) {
        return random.nextInt(start - 1, end) + 1;
    }

    /** 随机布尔值 */
    public static boolean randomBool() {
        return random.nextBoolean();
    }

    /** 随机浮点数 */
    public static double randomDouble(double start, double end) {
        return random.nextDouble(start - 1, end) + 1;
    }

     /** 用Logger输出严重的错误 */
    public static void logSevere(String msg) {
        Main.getInstance().getLogger().log(Level.SEVERE, msg);
    }

    /** 用Logger输出信息 */
    public static void logInfo(String msg) {
        Main.getInstance().getLogger().info(msg);
    }

    /** 在游戏聊天框中输出debug信息 */
    public static void debug(String msg) {
        Bukkit.broadcastMessage(StringUtil.color("&l[DEBUG] " + msg));
    }

    /** 四舍五入 */
    public static double round(double n, int digits) {
        double a = Math.pow(10, digits);
        return Math.round(n * a) / a;
    }

}
