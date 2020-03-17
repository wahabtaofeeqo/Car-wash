package com.example.carwash.helper;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.carwash.MainActivity;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class UserLocation {

    private Context context;

    private FusedLocationProviderClient locationProviderCLient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location location;

    private static final int INTERVAL = 1000 * 60 * 3;
    private static final int FASTEST  = 1000 * 60;

    public UserLocation(Context context) {
        this.context = context;
        locationProviderCLient = LocationServices.getFusedLocationProviderClient(context);
    }

    public LocationRequest createLocationRequest() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(INTERVAL);
        request.setFastestInterval(FASTEST);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return request;
    }

    private LocationCallback createCallback() {

        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult != null) {
                    for(Location loc : locationResult.getLocations()) {
                        if (loc != null)
                            updateUI(loc);
                    }
                }
            }
        };
    }

    public Location getLocation() {
        locationProviderCLient.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location loc) {
                if (loc != null) {
                    updateUI(loc);
                    location = loc;
                }
            }
        });

        return location;
    }

    private void updateUI(Location location) {
        Util.showMessage(context, "Lat: " + location.getLatitude() + " " + " Long: " + location.getLongitude());
    }

    public void startLocation() {

        if (locationRequest == null)
            locationRequest = createLocationRequest();

        if (locationCallback == null)
            locationCallback = createCallback();

        locationProviderCLient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void stopLocation() {
        locationProviderCLient.removeLocationUpdates(locationCallback);
    }
}
