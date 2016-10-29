package com.i906.mpt.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action1;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

/**
 * @author Noorzaini Ilhami
 */
class RxFusedLocation {

    private GoogleApiClient mClient;
    private ApiCallbacks mCallbacks;

    RxFusedLocation(Context context) {
        mCallbacks = new ApiCallbacks();
        mClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(mCallbacks)
                .addOnConnectionFailedListener(mCallbacks)
                .build();
    }

    public void connect() {
        if (!mClient.isConnected()) {
            mClient.connect();
        }
    }

    public Observable<Location> getLocation(LocationRequest request) {
        return Observable.fromEmitter(new LocationUpdates(request),
                AsyncEmitter.BackpressureMode.LATEST);
    }

    public void disconnect() {
        if (mClient.isConnected() || mClient.isConnecting()) {
            mClient.disconnect();
        }
    }

    private class LocationUpdates implements Action1<AsyncEmitter<Location>>,
            AsyncEmitter.Cancellable,
            LocationListener {

        private final LocationRequest request;
        private AsyncEmitter<Location> emitter;

        LocationUpdates(LocationRequest request) {
            this.request = request;
        }

        @Override
        public void call(AsyncEmitter<Location> emitter) {
            this.emitter = emitter;
            this.emitter.setCancellation(this);
            mCallbacks.addObserver(this);

            if (mClient.isConnected()) {
                requestLocationUpdates();
            }
        }

        private void requestLocationUpdates() {
            try {
                checkLocationSettings();
                FusedLocationApi.requestLocationUpdates(mClient, request, this);
            } catch (SecurityException e) {
                this.emitter.onError(e);
            } catch (IllegalStateException e) {
                this.emitter.onError(new LocationDisabledException());
            } catch (Exception e) {
                this.emitter.onError(e);
            }
        }

        private void checkLocationSettings() {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(request);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            emitter.onError(new LocationDisabledException(status));
                            break;
                    }
                }
            });
        }

        @Override
        public void onLocationChanged(Location location) {
            emitter.onNext(location);
        }

        private void onConnected() {
            if (emitter != null) {
                requestLocationUpdates();
            }
        }

        private void onError(Throwable e) {
            if (emitter != null) {
                emitter.onError(e);
            }
        }

        @Override
        public void cancel() throws Exception {
            if (mClient.isConnected()) {
                FusedLocationApi.removeLocationUpdates(mClient, this);
            }
            mCallbacks.removeCallback(this);
        }
    }

    private class ApiCallbacks implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        private List<LocationUpdates> observers;

        ApiCallbacks() {
            observers = new CopyOnWriteArrayList<>();
        }

        void addObserver(LocationUpdates observer) {
            if (!observers.contains(observer)) observers.add(observer);
            connect();
        }

        void removeCallback(LocationUpdates observer) {
            observers.remove(observer);

            if (observers.isEmpty()) {
                disconnect();
            }
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            for (LocationUpdates o : observers) {
                o.onConnected();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            for (LocationUpdates o : observers) {
                o.onError(new ConnectionSuspendedException(i));
            }
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult result) {
            for (LocationUpdates o : observers) {
                o.onError(new ConnectionException("Failed to connect to GoogleApi.", result));
            }
        }
    }
}
