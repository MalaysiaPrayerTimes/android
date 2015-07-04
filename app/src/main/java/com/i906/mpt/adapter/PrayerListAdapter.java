package com.i906.mpt.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.i906.mpt.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PrayerListAdapter extends BaseAdapter {

    protected SimpleDateFormat mDateFormatter;
    protected List<Date> mDateList;
    protected String[] mPrayerNames;
    protected int mHighlightedIndex;

    protected int mDefaultColor = -1;
    protected int mHighlightedColor = -1;

    public PrayerListAdapter(List<Date> dateList, String[] prayerNames) {
        mDateList = dateList;
        mPrayerNames = prayerNames;
        mDateFormatter = new SimpleDateFormat("hh:mm");
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

    protected int getHighlightedColor(Context ctx) {

        if (mHighlightedColor != -1) return mHighlightedColor;
        Resources r = ctx.getResources();
        mHighlightedColor = r.getColor(R.color.mpt_color_accent);

        return mHighlightedColor;
    }

    protected int getDefaultTextColor(Context ctx) {

        if (mDefaultColor != -1) return mDefaultColor;

        TypedValue typedValue = new TypedValue();
        ctx.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);

        int[] textColorAttr = new int[] { android.R.attr.textColorPrimary };
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
        return mDateList.size();
    }

    @Override
    public Date getItem(int position) {
        return mDateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {

        @Bind(R.id.tv_prayer_name)
        protected TextView name;

        @Bind(R.id.tv_prayer_time)
        protected TextView time;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
