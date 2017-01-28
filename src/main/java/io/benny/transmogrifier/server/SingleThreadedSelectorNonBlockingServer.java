package io.benny.transmogrifier.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import io.benny.transmogrifier.handler.ExceptionHandler;
import io.benny.transmogrifier.handler.Handler;
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

        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            for (Iterator<SelectionKey> iterator = selectionKeys.iterator(); iterator.hasNext(); ) {
                SelectionKey key =  iterator.next();
                iterator.remove();

                if (key.isValid()) {
                    if (key.isAcceptable()) {
                        System.out.println("Accepted connection");
                    } else if (key.isReadable()) {
                        System.out.println("Message received");
                    } else if (key.isWritable()) {
                        System.out.println("Socket ready for writing");
                    }
                }
            }
        }
    }
}
