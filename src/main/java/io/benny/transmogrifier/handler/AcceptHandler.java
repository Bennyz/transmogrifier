package io.benny.transmogrifier.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

/**
 * Created by benny on 1/28/17.
 */
public class AcceptHandler implements Handler<SelectionKey, IOException> {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public AcceptHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
        SocketChannel sc = ssc.accept();

        System.out.println("Incoming connection");
        sc.configureBlocking(false);
        pendingData.put(sc, new ArrayDeque<>());

        sc.register(selectionKey.selector(), SelectionKey.OP_READ);
    }
}
