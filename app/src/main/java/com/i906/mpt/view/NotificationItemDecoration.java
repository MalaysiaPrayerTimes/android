package com.i906.mpt.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.i906.mpt.R;

/**
 * Created by Noorzaini Ilhami on 17/10/2015.
 */
public class NotificationItemDecoration extends RecyclerView.ItemDecoration {

    private int mMargin;

    public NotificationItemDecoration(Context context) {
        mMargin = (int) context.getResources().getDimension(R.dimen.card_spacing);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, mMargin, 0, 0);
    }
}
