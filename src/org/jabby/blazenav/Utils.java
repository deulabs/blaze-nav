package org.jabby.blazenav;

import java.util.Arrays;

class Utils {
    static <T> T[] mergeVargs(T[] first, T... second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
