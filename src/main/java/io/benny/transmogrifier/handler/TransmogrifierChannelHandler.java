package io.benny.transmogrifier.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import io.benny.transmogrifier.util.Util;

/**
 * Created by benny on 1/28/17.
 */
public class TransmogrifierChannelHandler<S, X> implements Handler<SocketChannel, IOException> {

    @Override
    public void handle(SocketChannel s) throws IOException {
        ByteBuffer buf = ByteBuffer.allocateDirect(80);
        int read = s.read(buf);

        if (read == -1) {
            s.close();
            return;
        }

        if (read > 0) {
            Util.transmogrify(buf);

            while (buf.hasRemaining()) {
                s.write(buf);
            }
        }
    }
}
