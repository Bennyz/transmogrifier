package io.benny.transmogrifier.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.benny.transmogrifier.handler.ExecutorServiceHandler;
import io.benny.transmogrifier.handler.Handler;
import io.benny.transmogrifier.handler.PrintingHandler;
import io.benny.transmogrifier.handler.TransmogrifyHandler;

/**
 * Created by benny on 1/28/17.
 */
public class ExecutorServiceBlockingServer {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8080);
        ExecutorService pool = Executors.newCachedThreadPool();
        Handler<Socket, IOException> handler =
                new ExecutorServiceHandler<>(
                        pool,
                        new PrintingHandler<>(
                                new TransmogrifyHandler()
                        ));
        while (true) {
            Socket s = ss.accept();
            handler.handle(s);
        }
    }
}
