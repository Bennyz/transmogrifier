package io.benny.transmogrifier.handler;

/**
 * Created by benny on 1/28/17.
 */
public abstract class DecoratingHandler<S, X extends Throwable> implements Handler<S, X> {
    private final Handler<S, X> other;

    protected DecoratingHandler(Handler<S, X> other) {
        this.other = other;
    }

    @Override
    public void handle(S s) throws X {
        other.handle(s);
    }
}
