package io.benny.transmogrifier.handler;

import java.io.IOException;

/**
 * Created by benny on 1/28/17.
 */
public interface Handler<S, X extends Throwable> {
    void handle(S s) throws X;
}
