package ru.kodep.vlad.potok.utils;

/**
 * Created by vlad on 12.03.18
 */

public class ObjectUtils {

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

}
