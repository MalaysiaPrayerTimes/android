package com.i906.mpt;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

/**
 * from: http://alexismas.com/blog/2015/05/20/unit-testing-rxjava/
 */
public class RxJavaResetRule implements TestRule {

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                //before: plugins reset, execution and schedulers hook defined
                RxJavaPlugins.getInstance().reset();
                RxJavaPlugins.getInstance().registerSchedulersHook(new SchedulerHook());

                RxAndroidPlugins.getInstance().reset();
                RxAndroidPlugins.getInstance().registerSchedulersHook(new AndroidSchedulersHook());

                base.evaluate();

                //after: clean up
                RxJavaPlugins.getInstance().reset();
                RxAndroidPlugins.getInstance().reset();
            }
        };
    }

    private class SchedulerHook extends RxJavaSchedulersHook {
        @Override
        public Scheduler getIOScheduler() {
            return Schedulers.immediate();
        }

        @Override
        public Scheduler getNewThreadScheduler() {
            return Schedulers.immediate();
        }

        @Override
        public Scheduler getComputationScheduler() {
            return Schedulers.immediate();
        }
    }

    private class AndroidSchedulersHook extends RxAndroidSchedulersHook {
        @Override
        public Scheduler getMainThreadScheduler() {
            return Schedulers.immediate();
        }
    }
}
