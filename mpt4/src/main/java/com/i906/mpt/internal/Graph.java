package com.i906.mpt.internal;

import com.i906.mpt.main.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Noorzaini Ilhami on 24/10/2015.
 */
@Singleton
@Component(modules = {
        AppModule.class,
})
public interface Graph {
    void inject(MainActivity activity);
}
