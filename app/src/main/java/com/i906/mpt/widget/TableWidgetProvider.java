package com.i906.mpt.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.i906.mpt.R;
import com.i906.mpt.extension.Extension;
import com.i906.mpt.prayer.Prayer;
import com.i906.mpt.prayer.PrayerContext;

import java.util.Calendar;
import java.util.List;

/**
 * @author Noorzaini Ilhami
 */
public abstract class TableWidgetProvider extends MptWidgetProvider {

    private final static int[] PRAYER_ROW = {
            R.id.row_1,
            R.id.row_2,
            R.id.row_3,
            R.id.row_4,
            R.id.row_5,
            R.id.row_6,
            R.id.row_7,
            R.id.row_8
    };

    private final static int[] PRAYER_NAME = {
            R.id.row_name_1,
            R.id.row_name_2,
            R.id.row_name_3,
            R.id.row_name_4,
            R.id.row_name_5,
            R.id.row_name_6,
            R.id.row_name_7,
            R.id.row_name_8
    };

    private final static int[] PRAYER_TIME = {
            R.id.row_time_1,
            R.id.row_time_2,
            R.id.row_time_3,
            R.id.row_time_4,
            R.id.row_time_5,
            R.id.row_time_6,
            R.id.row_time_7,
            R.id.row_time_8
    };

    protected abstract int getWidgetLayout();

    protected String[] getHijriNames(AppWidgetManager awm, Context context, int appWidgetId) {
        Resources r = context.getResources();
        return r.getStringArray(R.array.hijri_months);
    }

    protected String[] getMasihiNames(AppWidgetManager awm, Context context, int appWidgetId) {
        Resources r = context.getResources();
        return r.getStringArray(R.array.masihi_months);
    }

    protected boolean isImsakEnabled(AppWidgetManager awm, Context context, int appWidgetId) {
        return mWidgetPreferences.isImsakEnabled();
    }

    protected boolean isDhuhaEnabled(AppWidgetManager awm, Context context, int appWidgetId) {
        return mWidgetPreferences.isDhuhaEnabled();
    }

    protected String getHijriDate(AppWidgetManager awm, Context context, int appWidgetId, int d, int m, int y) {
        String[] hijriNames = getHijriNames(awm, context, appWidgetId);
        return context.getString(R.string.label_date, d, hijriNames[m], y);
    }

    protected String getMasihiDate(AppWidgetManager awm, Context context, int appWidgetId, int d, int m, int y) {
        String[] masihiNames = getMasihiNames(awm, context, appWidgetId);
        return context.getString(R.string.label_date, d, masihiNames[m], y);
    }

    @Override
    protected RemoteViews buildLayout(AppWidgetManager awm, Context context, int appWidgetId, PrayerContext prayerContext) {
        Intent intent = new Intent(Extension.ACTION_MAIN_SCREEN);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        int backgroundColor = mWidgetPreferences.getBackgroundColor();

        RemoteViews rv = new RemoteViews(context.getPackageName(), getWidgetLayout());
        rv.setOnClickPendingIntent(R.id.widget_header, pendingIntent);
        rv.setInt(R.id.widget_header, "setBackgroundColor", backgroundColor);
        rv.setViewVisibility(R.id.progress_bar, View.VISIBLE);
        rv.setViewVisibility(R.id.btn_retry, View.GONE);

        if (prayerContext == null) {
            return rv;
        }

        Resources r = context.getResources();

        String[] prayerNames = r.getStringArray(R.array.prayer_names);

        boolean showImsak = isImsakEnabled(awm, context, appWidgetId);
        boolean showDhuha = isDhuhaEnabled(awm, context, appWidgetId);
        boolean showMasihi = mWidgetPreferences.isMasihiDateEnabled();
        boolean showHijri = mWidgetPreferences.isHijriDateEnabled();

        int highlight = backgroundColor | 0xFF000000;
        int normal = ContextCompat.getColor(context, android.R.color.white);

        List<Prayer> cdpt = prayerContext.getCurrentPrayerList();
        List<Integer> hdate = prayerContext.getHijriDate();
        Calendar imsak = Calendar.getInstance();
        imsak.setTime(cdpt.get(0).getDate());

        Prayer cp = prayerContext.getCurrentPrayer();
        Prayer np = prayerContext.getNextPrayer();

        int cpi = cp.getIndex();
        int npi = np.getIndex();

        int hd = hdate.get(Prayer.HIJRI_DATE);
        int hm = hdate.get(Prayer.HIJRI_MONTH);
        int hy = hdate.get(Prayer.HIJRI_YEAR);
        int md = imsak.get(Calendar.DATE);
        int mm = imsak.get(Calendar.MONTH);
        int my = imsak.get(Calendar.YEAR);

        rv.setTextViewText(R.id.tv_prayer_name, prayerNames[npi]);
        rv.setTextViewText(R.id.tv_prayer_time, getFormattedDate(context, np.getDate()));
        rv.setTextViewText(R.id.tv_location, prayerContext.getLocationName());
        rv.setTextViewText(R.id.tv_hijri_date, getHijriDate(awm, context, appWidgetId, hd, hm, hy));
        rv.setTextViewText(R.id.tv_masihi_date, getMasihiDate(awm, context, appWidgetId, md, mm, my));
        rv.setViewVisibility(R.id.tv_hijri_date, showHijri ? View.VISIBLE : View.GONE);
        rv.setViewVisibility(R.id.tv_masihi_date, showMasihi ? View.VISIBLE : View.GONE);
        rv.setViewVisibility(R.id.progress_bar, View.GONE);
        rv.setViewVisibility(R.id.prayerlist, View.VISIBLE);
        rv.setViewVisibility(PRAYER_ROW[Prayer.PRAYER_IMSAK], showImsak ? View.VISIBLE : View.GONE);
        rv.setViewVisibility(PRAYER_ROW[Prayer.PRAYER_DHUHA], showDhuha ? View.VISIBLE : View.GONE);

        for (int i = 0; i < prayerNames.length; i++) {
            rv.setTextViewText(PRAYER_NAME[i], prayerNames[i]);
            rv.setTextViewText(PRAYER_TIME[i], getFormattedDate(context, cdpt.get(i).getDate()));

            if (i == cpi || (!showImsak && i == Prayer.PRAYER_ISYAK && cpi == Prayer.PRAYER_IMSAK) ||
                    (!showDhuha && i == Prayer.PRAYER_SYURUK && cpi == Prayer.PRAYER_DHUHA)) {
                rv.setTextColor(PRAYER_NAME[i], highlight);
                rv.setTextColor(PRAYER_TIME[i], highlight);
            } else {
                rv.setTextColor(PRAYER_NAME[i], normal);
                rv.setTextColor(PRAYER_TIME[i], normal);
            }
        }

        return rv;
    }

    @Override
    protected RemoteViews buildErrorLayout(AppWidgetManager awm, Context context, int appWidgetId, String error) {
        Intent intent = new Intent(Extension.ACTION_MAIN_SCREEN);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Intent retryIntent = new Intent(context, getWidgetClass());
        PendingIntent retryPendingIntent = PendingIntent.getBroadcast(context, 0, retryIntent, 0);

        RemoteViews rv = new RemoteViews(context.getPackageName(), getWidgetLayout());
        rv.setOnClickPendingIntent(R.id.widget_header, pendingIntent);
        rv.setOnClickPendingIntent(R.id.btn_retry, retryPendingIntent);
        rv.setViewVisibility(R.id.progress_bar, View.GONE);
        rv.setViewVisibility(R.id.btn_retry, View.VISIBLE);
        rv.setViewVisibility(R.id.prayerlist, View.GONE);

        return rv;
    }
}
