package io.benny.transmogrifier.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import io.benny.transmogrifier.util.Util;

/**
 * Created by benny on 1/28/17.
 */
public class TransmogrifyHandler implements Handler<Socket, IOException> {
    @Override
    public void handle(Socket s) throws IOException {
        try (
                InputStream in = s.getInputStream();
                OutputStream out = s.getOutputStream()
        ) {
            int data;

            while ((data = in.read()) != -1) {
                out.write(Util.transmogrify(data));
            }
        }
    }
}
