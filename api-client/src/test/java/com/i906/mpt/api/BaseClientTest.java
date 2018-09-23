package com.i906.mpt.api;

import rx.Observable;
import rx.observers.TestSubscriber;

/**
 * @author Noorzaini Ilhami
 */
public abstract class BaseClientTest {

    protected  <O> Throwable assertError(Observable<O> o, Class<? extends Throwable> e) {
        TestSubscriber<O> ts = TestSubscriber.create();
        o.subscribe(ts);
        ts.assertError(e);

        return ts.getOnErrorEvents().get(0);
    }

    protected <O> void assertCompleted(Observable<O> o) {
        TestSubscriber<O> ts = TestSubscriber.create();
        o.subscribe(ts);
        ts.assertNoErrors();
        ts.assertCompleted();
    }
}
