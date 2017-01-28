
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

import io.benny.transmogrifier.util.Util;

/**
 * Created by benny on 1/28/17.
 */
public class ReadHandler implements Handler<SelectionKey, IOException> {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public ReadHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
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
            Util.transmogrify(buf);
            pendingData.get(sc).add(buf);
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        }
    }
}
