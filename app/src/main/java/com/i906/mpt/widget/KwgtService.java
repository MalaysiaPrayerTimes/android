package com.i906.mpt.widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;

import com.i906.mpt.R;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.internal.ServiceModule;
import com.i906.mpt.prayer.Prayer;
import com.i906.mpt.prayer.PrayerContext;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Noorzaini Ilhami
 */
public class KwgtService extends Service implements WidgetHandler {

    private static final String KUSTOM_ACTION = "org.kustom.action.SEND_VAR";
    private static final String KUSTOM_ACTION_EXT_NAME = "org.kustom.action.EXT_NAME";
    private static final String KUSTOM_ACTION_VAR_NAME_ARRAY = "org.kustom.action.VAR_NAME_ARRAY";
    private static final String KUSTOM_ACTION_VAR_VALUE_ARRAY = "org.kustom.action.VAR_VALUE_ARRAY";

    private static final String[] NAME_ARRAY = new String[] {
            "loc",
            "cpi",
            "npi",
            "cpn",
            "npn",
            "cpt",
            "npt",
            "pt0",
            "pt1",
            "pt2",
            "pt3",
            "pt4",
            "pt5",
            "pt6",
            "pt7",
    };

    @Inject
    WidgetDelegate mPresenter;

    String[] mPrayerNames;

    @Override
    public void onCreate() {
        super.onCreate();

        Dagger.getGraph(this)
                .serviceGraph(new ServiceModule(this))
                .inject(this);

        mPresenter.setHandler(this);

        mPrayerNames = getResources()
                .getStringArray(R.array.prayer_names);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendStatusUpdate("started");

        mPresenter.refreshPrayerContext();
        return START_STICKY;
    }

    @Override
    public void handlePrayerContext(PrayerContext pc) {
        Prayer cp = pc.getCurrentPrayer();
        Prayer np = pc.getNextPrayer();

        int cpi = cp.getIndex();
        int npi = np.getIndex();

        String[] infoArray = new String[] {
                pc.getLocationName(),
                Integer.toString(cpi),
                Integer.toString(npi),
                this.mPrayerNames[cpi],
                this.mPrayerNames[npi],
                this.getFormattedDate(cp.getDate()),
                this.getFormattedDate(np.getDate()),
        };

        String[] timeArray = new String[8];
        List<Prayer> prayerList = pc.getCurrentPrayerList();

        for (int i = 0; i < prayerList.size(); i++) {
            timeArray[i] = this.getFormattedDate(prayerList.get(i).getDate());
        }

        String[] valueArray = concat(infoArray, timeArray);

        Intent intent = new Intent(KUSTOM_ACTION);

        intent.putExtra(KUSTOM_ACTION_EXT_NAME, "mpt");
        intent.putExtra(KUSTOM_ACTION_VAR_NAME_ARRAY, NAME_ARRAY);
        intent.putExtra(KUSTOM_ACTION_VAR_VALUE_ARRAY, valueArray);

        sendBroadcast(intent);
        sendStatusUpdate("completed");
        stopSelf();
    }

    @Override
    public void handleError(Throwable throwable) {
        sendStatusUpdate("error: " + throwable.getMessage());
        stopSelf();
    }

    private String getFormattedDate(Date date) {
        if (date == null) {
            return "";
        }

        return DateFormat.format("yyyy'y'MM'M'dd'd'HH'h'mm'm'ss's'", date).toString();
    }

    private void sendStatusUpdate(String status) {
        Intent intent = new Intent(KUSTOM_ACTION);
        intent.putExtra(KUSTOM_ACTION_EXT_NAME, "mpt");

        intent.putExtra(KUSTOM_ACTION_VAR_NAME_ARRAY, new String[] {
                "slm",
                "slu",
        });

        intent.putExtra(KUSTOM_ACTION_VAR_VALUE_ARRAY, new String[] {
                status,
                getFormattedDate(new Date())
        });

        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static void start(Context context) {
        Intent alarm = new Intent(context, KwgtService.class);
        context.startService(alarm);
    }
}
