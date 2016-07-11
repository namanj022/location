package com.survey.worklake.lo;

/**
 * Created by namanjain
 */

        import android.Manifest;
        import android.content.Context;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.support.v4.content.ContextCompat;

        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;


public class LocationProvider implements GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    public static interface Provider {
        public void setNewLocation(Location location);
    }

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Context mContext;
    private Provider mProvider;

    public LocationProvider(Context context,
                            Provider provider) {
        mContext = context;
        mProvider = provider;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        mLocationRequest = LocationRequest.create()
                .setInterval(10000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(5000);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                mProvider.setNewLocation(location);
            }
        }
    }

    public void connect() {
        mGoogleApiClient.connect();
    }
    public void disconnect() {
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mProvider.setNewLocation(location);
    }
}
