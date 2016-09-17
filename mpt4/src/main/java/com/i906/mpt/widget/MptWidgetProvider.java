package com.i906.mpt.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

import com.i906.mpt.extension.Extension;

import java.util.Date;

/**
 * @author Noorzaini Ilhami
 */
public abstract class MptWidgetProvider extends AppWidgetProvider {

    private final static String FORMAT_24 = "kk:mm";
    private final static String FORMAT_12 = "hh:mm";

    @Override
    public void onReceive(final Context context, Intent widgetIntent) {
        super.onReceive(context, widgetIntent);
        final String action = widgetIntent.getAction();

        if (Extension.ACTION_PRAYER_TIME_UPDATED.equals(action)) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, getWidgetClass());
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

    protected String getFormattedDate(Context context, Date date) {
        String f = DateFormat.is24HourFormat(context) ? FORMAT_24 : FORMAT_12;
        return DateFormat.format(f, date).toString();
    }

    protected abstract Class getWidgetClass();

    protected abstract RemoteViews buildLayout(AppWidgetManager awm, Context context, int appWidgetId);
}
