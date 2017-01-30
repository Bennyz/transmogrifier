package io.benny.transmogrifier.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.benny.transmogrifier.handler.AcceptHandler;
import io.benny.transmogrifier.handler.ExceptionHandler;
import io.benny.transmogrifier.handler.Handler;
import io.benny.transmogrifier.handler.PooledReadHandler;
import io.benny.transmogrifier.handler.TransmogrifierChannelHandler;
import io.benny.transmogrifier.handler.WriteHandler;

/**
 * Created by benny on 1/30/17.
 */
public class SelectorNonBlockingWithWorkerPool {
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

        ExecutorService pool = Executors.newFixedThreadPool(10);
        Queue<Runnable> selectorActions = new ConcurrentLinkedDeque<>();

        Map<SocketChannel, Queue<ByteBuffer>> pendingData = new ConcurrentHashMap<>();
        Handler<SelectionKey, IOException> acceptHandler = new AcceptHandler(pendingData);
        Handler<SelectionKey, IOException> readHandler = new PooledReadHandler(pool, pendingData, selectorActions);
        Handler<SelectionKey, IOException> writeHandler = new WriteHandler(pendingData);

        while (true) {
            selector.select();
            
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            processSelectorActions(selectorActions);
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

    private static void processSelectorActions(Queue<Runnable> selectorActions) {
        Runnable action;

        while ((action = selectorActions.poll()) != null) {
            action.run();
        }
    }
}
