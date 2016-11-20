package com.i906.mpt.internal;

import com.i906.mpt.intro.MainIntroActivity;
import com.i906.mpt.main.MainActivity;
import com.i906.mpt.mosque.ui.MosqueFragment;
import com.i906.mpt.prayer.ui.PrayerFragment;
import com.i906.mpt.qibla.QiblaFragment;
import com.i906.mpt.settings.ChangeLogActivity;
import com.i906.mpt.settings.DonateActivity;
import com.i906.mpt.settings.LocationFragment;
import com.i906.mpt.settings.NotificationActivity;
import com.i906.mpt.settings.SettingsActivity;
import com.i906.mpt.settings.azanpicker.AzanPickerFragment;
import com.i906.mpt.settings.locationpicker.LocationPickerActivity;
import com.i906.mpt.settings.prayer.ApplyAllDialogFragment;
import com.i906.mpt.settings.prayer.PrayerNotificationFragment;

import dagger.Subcomponent;

/**
 * @author Noorzaini Ilhami
 */
@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
})
public interface ActivityGraph {
    void inject(ApplyAllDialogFragment fragment);
    void inject(AzanPickerFragment fragment);
    void inject(ChangeLogActivity activity);
    void inject(DonateActivity activity);
    void inject(LocationFragment fragment);
    void inject(LocationPickerActivity fragment);
    void inject(MainActivity activity);
    void inject(MainIntroActivity activity);
    void inject(MosqueFragment fragment);
    void inject(NotificationActivity activity);
    void inject(PrayerNotificationFragment fragment);
    void inject(PrayerFragment fragment);
    void inject(QiblaFragment fragment);
    void inject(SettingsActivity activity);
}
