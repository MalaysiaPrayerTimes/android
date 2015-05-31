package com.i906.mpt.di;

import com.i906.mpt.MptApplication;
import com.i906.mpt.adapter.ExtensionsAdapter;
import com.i906.mpt.fragment.BaseFragment;
import com.i906.mpt.ui.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        MptModule.class,
        SystemModule.class
})
public interface MptComponent {

    void inject(BaseFragment fragment);
    void inject(BaseActivity activity);
    void inject(ExtensionsAdapter adapter);

    final class Initializer {
        public static MptComponent init(MptApplication app) {
            return DaggerMptComponent.builder()
                    .mptModule(new MptModule(app))
                    .systemModule(new SystemModule(app))
                    .build();
        }
    }
}
