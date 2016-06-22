package com.i906.mpt.internal;

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
}
