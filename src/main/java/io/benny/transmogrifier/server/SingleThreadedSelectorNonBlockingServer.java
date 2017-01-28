package io.benny.transmogrifier.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import io.benny.transmogrifier.handler.AcceptHandler;
import io.benny.transmogrifier.handler.ExceptionHandler;
import io.benny.transmogrifier.handler.Handler;
import io.benny.transmogrifier.handler.ReadHandler;
import io.benny.transmogrifier.handler.TransmogrifierChannelHandler;

/**
 * Created by benny on 1/28/17.
 */
public class SingleThreadedSelectorNonBlockingServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        Handler<SocketChannel, IOException> handler =
                new ExceptionHandler<>(
                        new TransmogrifierChannelHandler()
                );

        Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
        Handler<SelectionKey, IOException> acceptHandler = new AcceptHandler(pendingData);
        Handler<SelectionKey, IOException> readHandler = new ReadHandler(pendingData);
        Handler<SelectionKey, IOException> writeHandler = new WriteHandler();

        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            for (Iterator<SelectionKey> iterator = selectionKeys.iterator(); iterator.hasNext(); ) {
                SelectionKey key =  iterator.next();
                iterator.remove();

                if (key.isValid()) {
                    if (key.isAcceptable()) {
                        acceptHandler.handle(key);
                    } else if (key.isReadable()) {
                        readHandler.handle(key);
                    } else if (key.isWritable()) {
                        writeHandler.handle(key);
                    }
                }
            }
        }
    }
}
