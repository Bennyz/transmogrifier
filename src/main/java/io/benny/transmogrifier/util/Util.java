package io.benny.transmogrifier.util;

import java.nio.ByteBuffer;

/**
 * Created by benny on 1/28/17.
 */
public class Util {
    public static int transmogrify(int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }

    public static void transmogrify(ByteBuffer buf) {
        buf.flip();

        for (int i = 0; i < buf.limit(); i++) {
            buf.put(i, (byte)transmogrify(buf.get(i)));
        }
    }
}
