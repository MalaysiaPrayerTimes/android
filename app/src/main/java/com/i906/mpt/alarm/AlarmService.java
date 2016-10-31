package com.i906.mpt.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.i906.mpt.BuildConfig;
import com.i906.mpt.date.DateTimeHelper;
import com.i906.mpt.extension.Extension;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.internal.ServiceModule;
import com.i906.mpt.prayer.Prayer;
import com.i906.mpt.prayer.PrayerContext;
import com.i906.mpt.prayer.PrayerManager;
import com.i906.mpt.prefs.NotificationPreferences;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;

/**
 * @author Noorzaini Ilhami
 */
public class AlarmService extends Service {

    public static final String ACTION_UPDATE_REMINDER = BuildConfig.APPLICATION_ID + ".action.ACTION_UPDATE_REMINDER";

    public static final String ACTION_NOTIFICATION_PRAYER = BuildConfig.APPLICATION_ID + ".action.NOTIFICATION_PRAYER";
    public static final String ACTION_NOTIFICATION_REMINDER = BuildConfig.APPLICATION_ID + ".action.NOTIFICATION_REMINDER";
    public static final String ACTION_NOTIFICATION_CANCEL = BuildConfig.APPLICATION_ID + ".action.NOTIFICATION_CANCEL";

    public static final String EXTRA_PRAYER_INDEX = "prayer_index";
    public static final String EXTRA_PRAYER_TIME = "prayer_time";
    public static final String EXTRA_PRAYER_LOCATION = "prayer_location";

    private final CompositeSubscription mSubscription = new CompositeSubscription();

    private AlarmManager mAlarmManager;
    private PrayerContext mPrayerContext;
    private Deque<Pair<String, Integer>> mQueue = new ArrayDeque<>();

    @Inject
    DateTimeHelper mDateHelper;

    @Inject
    NotificationPreferences mNotificationPreferences;

    @Inject
    NotificationHelper mNotificationHelper;

    @Inject
    PrayerManager mPrayerManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Dagger.getGraph(this)
                .serviceGraph(new ServiceModule(this))
                .inject(this);

        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        startObservable();
    }

    private void startObservable() {
        Subscription s = mPrayerManager.getPrayerContext(false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PrayerContext>() {
                    @Override
                    public void call(PrayerContext prayerContext) {
                        handleUpdatedPrayerContext(prayerContext);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.w(throwable, "Unable to get prayer context.");
                        stopSelf();
                    }
                });

        mSubscription.add(s);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();

            if (mPrayerContext == null) {
                mQueue.add(Pair.create(action, startId));
            } else {
                handleAction(action, mPrayerContext, startId);
            }

            return START_STICKY;
        }

        stopSelf();
        return START_NOT_STICKY;
    }

    private void handleUpdatedPrayerContext(PrayerContext prayerContext) {
        mPrayerContext = prayerContext;

        while (!mQueue.isEmpty()) {
            Pair<String, Integer> pair = mQueue.remove();
            handleAction(pair.first, mPrayerContext, pair.second);
        }
    }

    private void handleAction(String action, PrayerContext prayerContext, int startId) {
        if (Extension.ACTION_PRAYER_CONTEXT_UPDATED.equals(action)) {
            setupInitialAlarm(prayerContext);
            stopSelf(startId);
        } else if (ACTION_UPDATE_REMINDER.equals(action)) {
            updateReminderAlarm(prayerContext, startId);
        }
    }

    private void setupInitialAlarm(PrayerContext prayerContext) {
        Prayer next = prayerContext.getNextPrayer();
        String location = prayerContext.getLocationName();

        long appearBefore = mNotificationPreferences.getAppearBeforeDuration();
        long alarmOffset = mNotificationPreferences.getAlarmOffset();
        long clearAfter = mNotificationPreferences.getClearAfterDuration();
        long reminderTime = -appearBefore + alarmOffset;
        long notificationTime = alarmOffset + 250;
        long clearTime = clearAfter + alarmOffset;

        int npi = next.getIndex();
        long npt = next.getDate().getTime();

        if (appearBefore > 0) {
            setAlarm(ACTION_NOTIFICATION_REMINDER, npi, npt, reminderTime, location);
        }

        setAlarm(ACTION_NOTIFICATION_PRAYER, npi, npt, notificationTime, location);

        if (clearAfter > 0) {
            setAlarm(ACTION_NOTIFICATION_CANCEL, npi, npt, clearTime, location);
        }
    }

    private void updateReminderAlarm(PrayerContext prayerContext, final int startId) {
        Prayer next = prayerContext.getNextPrayer();
        final String location = prayerContext.getLocationName();

        long appearBefore = mNotificationPreferences.getAppearBeforeDuration();
        final long alarmOffset = mNotificationPreferences.getAlarmOffset();
        int updates = (int) (appearBefore / 60000);

        final int npi = next.getIndex();
        final long npt = next.getDate().getTime();

        Subscription s = Observable.range(0, updates)
                .concatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        long initial = integer == 0 ? 0 : TimeUnit.MINUTES.toMillis(1);
                        long delay = initial + alarmOffset;
                        return Observable.just(integer)
                                .delay(delay, TimeUnit.MILLISECONDS);
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mNotificationHelper.showPrayerReminder(npi, npt, location, true);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.w(throwable);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        stopSelf(startId);
                    }
                });

        mSubscription.add(s);
    }

    private void setAlarm(String action, int index, long time, long triggerOffset, String location) {
        Intent i = createIntent(action, index, time, location);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        if (SDK_INT < Build.VERSION_CODES.M) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, time + triggerOffset, pi);
        } else {
            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time + triggerOffset, pi);
        }

        Timber.i("Created alarm %s: %s %s %s", index, action, new Date(time + triggerOffset), location);
    }

    private Intent createIntent(String action, int index, long time, String location) {
        Intent h = new Intent(this, AlarmReceiver.class);
        h.setAction(action);
        h.setData(Uri.parse("mpt://" + action + "/" + index));
        h.putExtra(EXTRA_PRAYER_INDEX, index);
        h.putExtra(EXTRA_PRAYER_TIME, time);
        h.putExtra(EXTRA_PRAYER_LOCATION, location);
        return h;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription.clear();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
