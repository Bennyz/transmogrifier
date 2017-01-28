package io.benny.transmogrifier.util;

/**
 * Created by benny on 1/28/17.
 */
public class Util {
    public static int transmogrify(int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}
