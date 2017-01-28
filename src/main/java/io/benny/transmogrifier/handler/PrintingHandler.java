package io.benny.transmogrifier.handler;

/**
 * Created by benny on 1/28/17.
 */
public class PrintingHandler<S, X extends Throwable> extends DecoratingHandler<S, X> {
    public PrintingHandler(Handler<S, X> other) {
        super(other);
    }

    @Override
    public void handle(S s) throws X {
        System.out.println(String.format("Connected to: %s", s));

        try {
            super.handle(s);
        } finally {
            System.out.println(String.format("Disconnected from: %s", s));
        }
    }
}
