package com.i906.mpt.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.RemoteViews;

import com.i906.mpt.R;
import com.i906.mpt.extension.Extension;
import com.i906.mpt.prayer.Prayer;
import com.i906.mpt.prayer.PrayerContext;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Noorzaini Ilhami on 24/10/2015.
 */
public class BarWidgetProvider extends AppWidgetProvider {

    private final static String FORMAT_24 = "kk:mm";
    private final static String FORMAT_12 = "hh:mm";

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

    @Override
    public void onReceive(final Context context, Intent widgetIntent) {
        super.onReceive(context, widgetIntent);
        final String action = widgetIntent.getAction();

        if (Extension.ACTION_PRAYER_TIME_UPDATED.equals(action)) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, BarWidgetProvider.class);
            onUpdate(context, mgr, mgr.getAppWidgetIds(cn));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int i = 0; i < appWidgetIds.length; ++i) {
            RemoteViews layout = buildLayout(appWidgetManager, context, appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i], layout);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        RemoteViews layout;
        layout = buildLayout(appWidgetManager, context, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, layout);
    }

    private RemoteViews buildLayout(AppWidgetManager awm, Context context, int appWidgetId) {
        Intent intent = new Intent(Extension.ACTION_MAIN_SCREEN);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_bar);
        Resources r = context.getResources();
        String[] prayerNames = r.getStringArray(R.array.prayer_names);
        String[] hijriNames;
        String[] masihiNames;
        int dateString;
        boolean showImsak;
        boolean showDhuha;

        if (useLongDates(awm, appWidgetId)) {
            hijriNames = r.getStringArray(R.array.hijri_months);
            masihiNames = r.getStringArray(R.array.masihi_months);
            dateString = R.string.label_date;
            showImsak = true;
            showDhuha = true;
        } else {
            hijriNames = r.getStringArray(R.array.hijri_months_short);
            masihiNames = r.getStringArray(R.array.masihi_months_short);
            dateString = R.string.label_date_short;
            showImsak = false;
            showDhuha = false;
        }

        int highlight = ContextCompat.getColor(context, R.color.colorAccent);
        int normal = ContextCompat.getColor(context, android.R.color.white);

        rv.setOnClickPendingIntent(R.id.widget_header, pendingIntent);

        Cursor c = context.getContentResolver()
                .query(Extension.PRAYER_CONTEXT_URI, null, null, null, null);

        if (c != null && c.moveToFirst()) {
            PrayerContext prayerContext = PrayerContext.Mapper.fromCursor(c);
            c.close();

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
            rv.setTextViewText(R.id.tv_hijri_date, context.getString(dateString, hd, hijriNames[hm], hy));
            rv.setTextViewText(R.id.tv_masihi_date, context.getString(dateString, md, masihiNames[mm], my));
            rv.setViewVisibility(R.id.progress_bar, View.GONE);
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
        } else {
            Timber.w("cursor null");
        }

        return rv;
    }

    private boolean useLongDates(AppWidgetManager awm, int id) {
        Bundle opt = awm.getAppWidgetOptions(id);
        int minWidth = opt.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        return minWidth > 350;
    }

    private String getFormattedDate(Context context, Date date) {
        String f = DateFormat.is24HourFormat(context) ? FORMAT_24 : FORMAT_12;
        return DateFormat.format(f, date).toString();
    }
}
