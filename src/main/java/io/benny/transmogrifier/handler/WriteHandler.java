package io.benny.transmogrifier.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

/**
 * Created by benny on 1/28/17.
 */
public class WriteHandler implements Handler<SelectionKey, IOException> {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public WriteHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        SocketChannel sc = (SocketChannel) selectionKey.channel();
        Queue<ByteBuffer> queue = pendingData.get(sc);

        while (!queue.isEmpty()) {
            ByteBuffer buf = queue.peek();
            int written = sc.write(buf);

            if (written == -1) {
                sc.close();
                pendingData.remove(sc);
                return;
            }

            if (buf.hasRemaining()) {
                return;
            } else {
                queue.remove();
            }

            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }
}
