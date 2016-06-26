package com.i906.mpt.internal;

import com.i906.mpt.main.mosque.MosqueFragment;

import dagger.Subcomponent;

/**
 * @author Noorzaini Ilhami
 */
@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
})
public interface ActivityGraph {
    void inject(MosqueFragment fragment);
}
