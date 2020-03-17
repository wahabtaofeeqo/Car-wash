package com.example.carwash;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.carwash.helper.AppConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressCoder extends IntentService {

    protected ResultReceiver receiver;

    public AddressCoder() {
        super("Address Coder");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null)
            return;


        String errorMessage = "";
        Location location = intent.getParcelableExtra(AppConfig.LOCATION_DATA_EXTRA);

        List<Address> addresses = null;
        try {
            addresses = Controller.getInstance().getGeocoder().getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }
        catch (IOException io) {
            errorMessage = "Error: " + io.getMessage();
            io.printStackTrace();
            Log.i("ERROR", errorMessage);
        }
        catch (IllegalArgumentException e) {
            errorMessage = "Invalid Latitude and Longitude";
            Log.i("ERROR: ", errorMessage);
        }

        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No address found";

                deliverResultToReceiver(AppConfig.RESULT_FAILURE, errorMessage);
            }
        }
        else {

            Address address = addresses.get(0);
            ArrayList<String> list = new ArrayList<>();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                list.add(address.getAddressLine(i));
            }

            deliverResultToReceiver(AppConfig.RESULT_SUCCESS, TextUtils.join(System.getProperty("line.separator"), list));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {

        Bundle bundle = new Bundle();
        bundle.putString(AppConfig.RESULT_DATA_KEY, message);
        receiver.send(resultCode, bundle);
    }
}
