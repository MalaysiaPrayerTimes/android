package com.i906.mpt.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.RemoteViews;

import com.i906.mpt.R;
import com.i906.mpt.extension.Extension;
import com.i906.mpt.prayer.Prayer;
import com.i906.mpt.prayer.PrayerContext;

/**
 * @author Noorzaini Ilhami
 */
public class TextWidgetProvider extends MptWidgetProvider {

    @Override
    protected Class getWidgetClass() {
        return TextWidgetProvider.class;
    }

    @Override
    protected RemoteViews buildLayout(AppWidgetManager awm, Context context, int appWidgetId, PrayerContext prayerContext) {
        Intent intent = new Intent(Extension.ACTION_MAIN_SCREEN);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_text);
        rv.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
        rv.setViewVisibility(R.id.progress_bar, View.VISIBLE);
        rv.setViewVisibility(R.id.btn_retry, View.GONE);

        if (prayerContext == null) {
            return rv;
        }

        Resources r = context.getResources();
        String[] prayerNames = r.getStringArray(R.array.prayer_names);

        Prayer np = prayerContext.getNextPrayer();
        int npi = np.getIndex();

        rv.setTextViewText(R.id.tv_prayer_name, prayerNames[npi]);
        rv.setTextViewText(R.id.tv_prayer_time, getFormattedDate(context, np.getDate()));
        rv.setTextViewText(R.id.tv_location, prayerContext.getLocationName());
        rv.setViewVisibility(R.id.progress_bar, View.GONE);
        rv.setViewVisibility(R.id.prayerlist, View.VISIBLE);

        return rv;
    }

    @Override
    protected RemoteViews buildErrorLayout(AppWidgetManager awm, Context context, int appWidgetId, String error) {
        Intent intent = new Intent(Extension.ACTION_MAIN_SCREEN);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Intent retryIntent = new Intent(context, getWidgetClass());
        PendingIntent retryPendingIntent = PendingIntent.getBroadcast(context, 0, retryIntent, 0);

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_text);
        rv.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
        rv.setOnClickPendingIntent(R.id.btn_retry, retryPendingIntent);
        rv.setViewVisibility(R.id.progress_bar, View.GONE);
        rv.setViewVisibility(R.id.btn_retry, View.VISIBLE);
        rv.setViewVisibility(R.id.prayerlist, View.GONE);

        return rv;
    }
}
