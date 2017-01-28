package io.benny.transmogrifier.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.benny.transmogrifier.handler.BlockingChannelHandler;
import io.benny.transmogrifier.handler.ExecutorServiceHandler;
import io.benny.transmogrifier.handler.Handler;
import io.benny.transmogrifier.handler.PrintingHandler;
import io.benny.transmogrifier.handler.TransmogrifierChannelHandler;

/**
 * Created by benny on 1/28/17.
 */
public class BlockingNioServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ExecutorService pool = Executors.newCachedThreadPool();
        Handler<SocketChannel, IOException> handler =
                new ExecutorServiceHandler<>(
                        pool,
                        new PrintingHandler<>(
                                new BlockingChannelHandler<>(
                                    new TransmogrifierChannelHandler()
                                )
                        )
                );

        while (true) {
            SocketChannel sc = ssc.accept();
            handler.handle(sc);
        }
    }
}
