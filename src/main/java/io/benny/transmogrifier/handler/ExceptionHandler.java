package io.benny.transmogrifier.handler;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by benny on 1/28/17.
 */
public class ExceptionHandler<S, X extends Throwable> extends DecoratingHandler<S, X> {

    private BiConsumer<S, Throwable> exceptionConsumer;

    public ExceptionHandler(Handler<S, X> other, BiConsumer<S, Throwable> exceptionConsumer) {
        super(other);
        this.exceptionConsumer = exceptionConsumer;
    }

    public ExceptionHandler(Handler<S, X> other) {
        this(other, (s, x) -> System.err.println(String.format("Issue with %s, error: %s", s, x)));
    }

    @Override
    public void handle(S s) {
        try {
            super.handle(s);
        } catch (Throwable x) {
            exceptionConsumer.accept(s, x);
        }
    }
}
