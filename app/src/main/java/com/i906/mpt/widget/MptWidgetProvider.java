package com.i906.mpt.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

import com.i906.mpt.internal.Dagger;
import com.i906.mpt.prayer.PrayerContext;
import com.i906.mpt.prefs.WidgetPreferences;

import java.util.Date;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_DELETED;

/**
 * @author Noorzaini Ilhami
 */
public abstract class MptWidgetProvider extends AppWidgetProvider {

    private final static String FORMAT_24 = "kk:mm";
    private final static String FORMAT_12 = "hh:mm";

    protected WidgetPreferences mWidgetPreferences;

    @Override
    public void onReceive(final Context context, Intent widgetIntent) {
        mWidgetPreferences = Dagger.getGraph(context).getWidgetPreferences();
        super.onReceive(context, widgetIntent);

        final String action = widgetIntent.getAction();

        if (WidgetService.ACTION_PRAYER_TIME_UPDATED.equals(action)) {
            PrayerContext prayerContext = widgetIntent.getParcelableExtra("prayer_context");
            updateWithPrayerContext(context, prayerContext);
        } else if (WidgetService.ACTION_PRAYER_TIME_ERROR.equals(action)) {
            String error = widgetIntent.getStringExtra("type");
            updateWithError(context, error);
        } else if (!ACTION_APPWIDGET_DELETED.equals(action)) {
            updateWithPrayerContext(context, null);
            WidgetService.start(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int i = 0; i < appWidgetIds.length; ++i) {
            RemoteViews layout = buildLayout(appWidgetManager, context, appWidgetIds[i], null);
            appWidgetManager.updateAppWidget(appWidgetIds[i], layout);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        RemoteViews layout;
        layout = buildLayout(appWidgetManager, context, appWidgetId, null);
        appWidgetManager.updateAppWidget(appWidgetId, layout);
    }

    protected void updateWithPrayerContext(Context context, PrayerContext prayerContext) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, getWidgetClass());
        int[] appWidgetIds = mgr.getAppWidgetIds(cn);

        if (appWidgetIds.length == 0) return;

        for (int appWidgetId : appWidgetIds) {
            RemoteViews layout = buildLayout(mgr, context, appWidgetId, prayerContext);
            mgr.updateAppWidget(appWidgetId, layout);
        }
    }

    protected void updateWithError(Context context, String error) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, getWidgetClass());
        int[] appWidgetIds = mgr.getAppWidgetIds(cn);

        if (appWidgetIds.length == 0) return;

        for (int appWidgetId : appWidgetIds) {
            RemoteViews layout = buildErrorLayout(mgr, context, appWidgetId, error);
            mgr.updateAppWidget(appWidgetId, layout);
        }
    }

    protected String getFormattedDate(Context context, Date date) {
        String f = DateFormat.is24HourFormat(context) ? FORMAT_24 : FORMAT_12;
        return DateFormat.format(f, date).toString();
    }

    protected abstract Class getWidgetClass();

    protected abstract RemoteViews buildLayout(AppWidgetManager awm, Context context, int appWidgetId, PrayerContext prayerContext);

    protected abstract RemoteViews buildErrorLayout(AppWidgetManager awm, Context context, int appWidgetId, String error);
}
