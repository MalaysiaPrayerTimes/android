package com.i906.mpt.di;

import com.i906.mpt.MptApplication;
import com.i906.mpt.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        MptModule.class,
        SystemModule.class
})
public interface MptComponent {

    void inject(MainActivity activity);

    final class Initializer {
        public static MptComponent init(MptApplication app) {
            return Dagger_MptComponent.builder()
                    .mptModule(new MptModule(app))
                    .systemModule(new SystemModule(app))
                    .build();
        }
    }
}
