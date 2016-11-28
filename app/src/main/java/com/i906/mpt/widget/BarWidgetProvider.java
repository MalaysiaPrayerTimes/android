package com.i906.mpt.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.i906.mpt.R;

/**
 * Created by Noorzaini Ilhami on 24/10/2015.
 */
public class BarWidgetProvider extends TableWidgetProvider {

    @Override
    protected int getWidgetLayout() {
        return R.layout.widget_bar;
    }

    @Override
    protected Class getWidgetClass() {
        return BarWidgetProvider.class;
    }

    @Override
    protected String[] getHijriNames(AppWidgetManager awm, Context context, int appWidgetId) {
        Resources r = context.getResources();

        if (useLongDates(awm, appWidgetId)) {
            return r.getStringArray(R.array.hijri_months);
        } else {
            return r.getStringArray(R.array.hijri_months_short);
        }
    }

    @Override
    protected String[] getMasihiNames(AppWidgetManager awm, Context context, int appWidgetId) {
        Resources r = context.getResources();

        if (useLongDates(awm, appWidgetId)) {
            return r.getStringArray(R.array.masihi_months);
        } else {
            return r.getStringArray(R.array.masihi_months_short);
        }
    }

    @Override
    protected String getHijriDate(AppWidgetManager awm, Context context, int appWidgetId, int d, int m, int y) {
        int s = useLongDates(awm, appWidgetId) ? R.string.label_date : R.string.label_date_short;
        String[] hijriNames = getHijriNames(awm, context, appWidgetId);
        return context.getString(s, d, hijriNames[m], y);
    }

    @Override
    protected String getMasihiDate(AppWidgetManager awm, Context context, int appWidgetId, int d, int m, int y) {
        int s = useLongDates(awm, appWidgetId) ? R.string.label_date : R.string.label_date_short;
        String[] masihiNames = getMasihiNames(awm, context, appWidgetId);
        return context.getString(s, d, masihiNames[m], y);
    }

    private boolean useLongDates(AppWidgetManager awm, int id) {
        Bundle opt = awm.getAppWidgetOptions(id);
        int minWidth = opt.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        return minWidth > 350;
    }
}
