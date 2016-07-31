package com.i906.mpt.prayer.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.i906.mpt.R;
import com.i906.mpt.prayer.Prayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class PrayerListAdapter extends BaseAdapter {

    private final SimpleDateFormat mDateFormatter;
    private final List<Prayer> mPrayerList;
    private final String[] mPrayerNames;
    private int mHighlightedIndex;

    private int mDefaultColor = -1;
    private int mHighlightedColor = -1;

    PrayerListAdapter(List<Prayer> prayerList, String[] prayerNames, String dateFormat) {
        mPrayerList = prayerList;
        mPrayerNames = prayerNames;
        mDateFormatter = new SimpleDateFormat(dateFormat);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        Context ctx = parent.getContext();
        Date d = getItem(position);

        int dc = getDefaultTextColor(ctx);
        int hc = getHighlightedColor(ctx);

        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.row_prayer, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(mPrayerNames[position]);
        holder.time.setText(mDateFormatter.format(d));

        if (mHighlightedIndex == position) {
            holder.name.setTextColor(hc);
            holder.time.setTextColor(hc);
        } else {
            holder.name.setTextColor(dc);
            holder.time.setTextColor(dc);
        }

        return view;
    }

    private int getHighlightedColor(Context ctx) {

        if (mHighlightedColor != -1) return mHighlightedColor;
        mHighlightedColor = ContextCompat.getColor(ctx, R.color.colorAccent);

        return mHighlightedColor;
    }

    private int getDefaultTextColor(Context ctx) {
        if (mDefaultColor != -1) return mDefaultColor;

        TypedValue typedValue = new TypedValue();
        ctx.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);

        int[] textColorAttr = new int[] {android.R.attr.textColorPrimary};
        TypedArray a = ctx.obtainStyledAttributes(typedValue.data, textColorAttr);
        mDefaultColor = a.getColor(0, -1);
        a.recycle();

        return mDefaultColor;
    }

    public void setHighlightedIndex(int index) {
        mHighlightedIndex = index;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPrayerList.size();
    }

    @Override
    public Date getItem(int position) {
        return mPrayerList.get(position).getDate();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {

        @BindView(R.id.tv_prayer_name)
        protected TextView name;

        @BindView(R.id.tv_prayer_time)
        protected TextView time;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
