import java.util.ArrayList;

public class Parameters {
    public static boolean logging = false;
    public static boolean debug = false;
    public static ArrayList<Integer> networkSizes = new ArrayList<>();

    public static boolean recursiveDoubling = false;

    public static void log(String s) {
        if (logging) {
            System.out.println(s);
        }
    }

    public static void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }
}
