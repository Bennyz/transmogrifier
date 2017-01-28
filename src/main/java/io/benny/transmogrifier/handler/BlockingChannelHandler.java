package io.benny.transmogrifier.handler;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Created by benny on 1/28/17.
 */
public class BlockingChannelHandler<S, X> extends DecoratingHandler<SocketChannel, IOException> {

    public BlockingChannelHandler(Handler<SocketChannel, IOException> other) {
        super(other);
    }

    @Override
    public void handle(SocketChannel sc) throws IOException {
        while (sc.isConnected()) {
            super.handle(sc);
        }
    }
}
