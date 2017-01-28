package io.benny.transmogrifier.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import io.benny.transmogrifier.handler.Handler;
import io.benny.transmogrifier.handler.PrintingHandler;
import io.benny.transmogrifier.handler.ThreadedHandler;
import io.benny.transmogrifier.handler.TransmogrifyHandler;

/**
 * Created by benny on 1/28/17.
 */
public class MultiThreadedBlockingServer {
    public static void main(String... args) throws IOException {
        ServerSocket ss = new ServerSocket(8080);
        Handler<Socket, IOException> handler =
                new ThreadedHandler<>(
                        new PrintingHandler<>(
                                new TransmogrifyHandler()
                        ));
        while (true) {
            Socket s = ss.accept();
            handler.handle(s);
        }
    }

}
