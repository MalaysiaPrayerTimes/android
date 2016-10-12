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
import com.i906.mpt.prayer.PrayerContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class PrayerListAdapter extends BaseAdapter {

    private final SimpleDateFormat mDateFormatter;
    private final String[] mPrayerNames;

    private PrayerContext.ViewSettings mViewSettings;
    private List<Prayer> mPrayerList;
    private int mHighlightedIndex;

    private int mDefaultColor = -1;
    private int mHighlightedColor = -1;

    PrayerListAdapter(String[] prayerNames, String dateFormat) {
        mPrayerNames = prayerNames;
        mDateFormatter = new SimpleDateFormat(dateFormat);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        Context ctx = parent.getContext();
        Prayer p = getItem(position);
        Date d = p.getDate();

        int dc = getDefaultTextColor(ctx);
        int hc = getHighlightedColor(ctx);

        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.row_prayer, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(mPrayerNames[p.getIndex()]);
        holder.time.setText(mDateFormatter.format(d));

        if (getHighlightedIndex() == p.getIndex()) {
            holder.name.setTextColor(hc);
            holder.time.setTextColor(hc);
        } else {
            holder.name.setTextColor(dc);
            holder.time.setTextColor(dc);
        }

        return view;
    }

    void setPrayerList(List<Prayer> prayerList) {
        mPrayerList = prayerList;
        Prayer imsak = null;
        Prayer dhuha = null;

        for (Prayer p : mPrayerList) {
            if (p.getIndex() == Prayer.PRAYER_DHUHA) {
                dhuha = p;
            }

            if (p.getIndex() == Prayer.PRAYER_IMSAK) {
                imsak = p;
            }
        }

        if (!mViewSettings.isImsakEnabled()) {
            mPrayerList.remove(imsak);
        }

        if (!mViewSettings.isDhuhaEnabled()) {
            mPrayerList.remove(dhuha);
        }
    }

    void setViewSettings(PrayerContext.ViewSettings settings) {
        mViewSettings = settings;
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

    private int getHighlightedIndex() {
        int hi = mHighlightedIndex;

        if (!mViewSettings.isCurrentPrayerHighlightMode()) {
            return (hi + 1) % 8;
        }

        return hi;
    }

    void setHighlightedIndex(int index) {
        mHighlightedIndex = index;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPrayerList.size();
    }

    @Override
    public Prayer getItem(int position) {
        return mPrayerList.get(position);
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
