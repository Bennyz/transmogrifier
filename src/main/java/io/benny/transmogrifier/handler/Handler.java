package io.benny.transmogrifier.handler;

/**
 * Created by benny on 1/28/17.
 */
public interface Handler<S, X extends Throwable> {
    void handle(S s) throws X;
}
