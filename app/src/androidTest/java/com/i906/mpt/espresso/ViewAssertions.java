package com.i906.mpt.espresso;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.hamcrest.StringDescription;

/**
 * @author Noorzaini Ilhami
 */
public class ViewAssertions {

    private static final String TAG = ViewAssertions.class.getSimpleName();

    private ViewAssertions() {
    }

    public static ViewAssertion haveItemCount(final int count) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                StringDescription description = new StringDescription();
                description.appendText("'");

                if (noViewFoundException != null) {
                    description.appendText(String.format(
                            "' check could not be performed because view '%s' was not found.\n",
                            noViewFoundException.getViewMatcherDescription()));
                    Log.e(TAG, description.toString());
                    throw noViewFoundException;
                } else {
                    int actualCount = -1;

                    if (view instanceof RecyclerView) {
                        actualCount = getItemCount((RecyclerView) view);
                    }

                    if (actualCount == -1) {
                        throw new AssertionError("Cannot get view item count.");
                    } else if (actualCount != count) {
                        throw new AssertionError("View has " + actualCount +
                                "items while expected " + count);
                    }
                }
            }
        };
    }

    private static int getItemCount(RecyclerView view) {
        if (view != null && view.getAdapter() != null) {
            return view.getAdapter().getItemCount();
        }

        return -1;
    }
}
