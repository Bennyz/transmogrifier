package io.benny.transmogrifier.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.benny.transmogrifier.handler.ExceptionHandler;
import io.benny.transmogrifier.handler.Handler;
import io.benny.transmogrifier.handler.PrintingHandler;
import io.benny.transmogrifier.handler.TransmogrifierChannelHandler;

/**
 * Created by benny on 1/28/17.
 */
public class SingleThreadedPollingNonBlockingServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ssc.configureBlocking(false);
        Handler<SocketChannel, IOException> handler =
                new ExceptionHandler<>(
                        new PrintingHandler(
                                new TransmogrifierChannelHandler<>()
                        )
                );

        List<SocketChannel> sockets = new ArrayList<>();

        while (true) {
            SocketChannel newSocket = ssc.accept();

            if (newSocket != null) {
                sockets.add(newSocket);
                System.out.println(String.format("Connected to: %s", newSocket));
                newSocket.configureBlocking(false);
            }

            for (Iterator<SocketChannel> iterator = sockets.iterator(); iterator.hasNext(); ) {
                SocketChannel next =  iterator.next();

                if (next.isConnected()) {
                    handler.handle(next);
                } else {
                    iterator.remove();
                }
            }
        }

    }
}
