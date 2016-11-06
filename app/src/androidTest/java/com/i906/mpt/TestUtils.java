package com.i906.mpt;

import android.support.test.InstrumentationRegistry;

import com.i906.mpt.espresso.RecyclerViewMatcher;

/**
 * @author Noorzaini Ilhami
 */
public class TestUtils {

    private TestUtils() {
    }

    public static MptApplication app() {
        return (MptApplication) InstrumentationRegistry.getTargetContext().getApplicationContext();
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }
}
