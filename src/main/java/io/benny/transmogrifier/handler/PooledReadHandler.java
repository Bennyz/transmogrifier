package io.benny.transmogrifier.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

import io.benny.transmogrifier.util.Util;

/**
 * Created by benny on 1/30/17.
 */
public class PooledReadHandler implements Handler<SelectionKey, IOException> {

    private final ExecutorService pool;
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;
    private Queue<Runnable> selectorActions;

    public PooledReadHandler(ExecutorService pool, Map<SocketChannel, Queue<ByteBuffer>> pendingData, Queue<Runnable> selectorActions) {
        this.pool = pool;
        this.pendingData = pendingData;
        this.selectorActions = selectorActions;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        SocketChannel sc = (SocketChannel) selectionKey.channel();
        ByteBuffer buf = ByteBuffer.allocateDirect(80);
        int read = sc.read(buf);

        if (read == -1) {
            pendingData.remove(sc);
            return;
        }

        if (read > 0) {
            pool.submit(() -> {
                Util.transmogrify(buf);
                pendingData.get(sc).add(buf);
                selectorActions.add(() -> selectionKey.interestOps(SelectionKey.OP_WRITE));
                selectionKey.selector().wakeup();
            });
        }
    }
}
