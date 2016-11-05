package com.i906.mpt.common;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.Status;
import com.i906.mpt.location.LocationDisabledException;
import com.i906.mpt.location.LocationTimeoutException;

/**
 * @author Noorzaini Ilhami
 */
public abstract class LocationFragment extends BaseFragment {

    protected static final int DEFAULT_LOCATION_REQUEST_CODE = 4079;
    protected static final int DEFAULT_PERMISSIONS_REQUEST_CODE = 4857;
    protected static final int DEFAULT_RESOLUTION_REQUEST_CODE = 2540;

    private boolean mPermissionError = false;
    private boolean mLocationError = false;

    private LocationDisabledException mLocationDisabledException;

    protected void requestLocationPermissions() {
        requestPermissions(new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, DEFAULT_PERMISSIONS_REQUEST_CODE);
    }

    protected void openLocationSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, DEFAULT_LOCATION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == DEFAULT_PERMISSIONS_REQUEST_CODE) {
            clearErrorFlags();
            recheckLocation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DEFAULT_LOCATION_REQUEST_CODE ||
                requestCode == DEFAULT_RESOLUTION_REQUEST_CODE) {
            clearErrorFlags();
            recheckLocation();
        }
    }

    protected boolean hasLocationResolution() {
        return mLocationDisabledException != null && mLocationDisabledException.hasStatus();
    }

    protected IntentSender getLocationResolution() {
        if (!hasLocationResolution()) return null;

        Status status = mLocationDisabledException.getStatus();
        PendingIntent pi = status.getResolution();

        return pi.getIntentSender();
    }

    protected void setErrorFlags(Throwable error) {
        if (error instanceof SecurityException) {
            mPermissionError = true;
            mLocationError = false;
        } else if (error instanceof LocationDisabledException || error instanceof LocationTimeoutException) {
            if (error instanceof LocationDisabledException) {
                mLocationDisabledException = (LocationDisabledException) error;
            }

            mPermissionError = false;
            mLocationError = true;
        } else {
            clearErrorFlags();
        }
    }

    protected void clearErrorFlags() {
        mPermissionError = false;
        mLocationError = false;
    }

    protected boolean hasPermissionError() {
        return mPermissionError;
    }

    protected boolean hasLocationError() {
        return mLocationError;
    }

    protected abstract void recheckLocation();
}
