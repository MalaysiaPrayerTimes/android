package com.i906.mpt.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.res.Resources;

import com.i906.mpt.R;

/**
 * @author Noorzaini Ilhami
 */
public class MiniWidgetProvider extends TableWidgetProvider {

    @Override
    protected int getWidgetLayout() {
        return R.layout.widget_mini;
    }

    @Override
    protected Class getWidgetClass() {
        return MiniWidgetProvider.class;
    }

    @Override
    protected String[] getHijriNames(AppWidgetManager awm, Context context, int appWidgetId) {
        Resources r = context.getResources();
        return r.getStringArray(R.array.hijri_months_short);
    }

    @Override
    protected String[] getMasihiNames(AppWidgetManager awm, Context context, int appWidgetId) {
        Resources r = context.getResources();
        return r.getStringArray(R.array.masihi_months_short);
    }

    @Override
    protected String getHijriDate(AppWidgetManager awm, Context context, int appWidgetId, int d, int m, int y) {
        String[] hijriNames = getHijriNames(awm, context, appWidgetId);
        return context.getString(R.string.label_date_short, d, hijriNames[m]);
    }

    @Override
    protected String getMasihiDate(AppWidgetManager awm, Context context, int appWidgetId, int d, int m, int y) {
        String[] masihiNames = getMasihiNames(awm, context, appWidgetId);
        return context.getString(R.string.label_date_short, d, masihiNames[m]);
    }
}
