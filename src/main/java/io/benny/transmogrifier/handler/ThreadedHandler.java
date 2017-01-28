package io.benny.transmogrifier.handler;

import java.util.function.BiConsumer;

/**
 * Created by benny on 1/28/17.
 */
public class ThreadedHandler<S, X extends Throwable> extends ExceptionHandler<S, X> {

    public ThreadedHandler(Handler<S, X> other) {
        super(other);
    }

    public ThreadedHandler(Handler<S, X> other, BiConsumer<S, Throwable> exceptionConsumer) {
        super(other, exceptionConsumer);
    }

    @Override
    public void handle(S s) {
        new Thread(() -> super.handle(s)).start();
    }
}
