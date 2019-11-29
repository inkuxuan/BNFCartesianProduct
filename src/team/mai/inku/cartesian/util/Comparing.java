package team.mai.inku.cartesian.util;

public class Comparing {
    public static int firstIndex(String string, int start, char... chars) {
        int firstIndex = -1;
        for (char c : chars) {
            int i = string.indexOf(c, start);
            if (firstIndex == -1 || (i != -1 && firstIndex > i))
                firstIndex = i;
        }
        return firstIndex;
    }

    public static int firstIndex(String string, char... chars) {
        return firstIndex(string, 0, chars);
    }
}
