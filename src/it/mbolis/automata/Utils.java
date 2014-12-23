package it.mbolis.automata;

public abstract class Utils {

    public static <T> int indexOf(T[] array, T element) {
        if (element == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (element.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }
}
