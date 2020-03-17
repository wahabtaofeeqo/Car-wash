package com.example.carwash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.carwash.helper.AppConfig;
import com.example.carwash.helper.DataManager;
import com.example.carwash.helper.Motor;
import com.example.carwash.helper.NetworkChecker;
import com.example.carwash.helper.SessionManager;
import com.example.carwash.helper.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private FusedLocationProviderClient providerClient;
    private Location lastLocation;
    private LocationRequest locationRequest;

    private Marker marker;

    private static final int REQUEST_CODE = 20;
    private static final int INTERVAL = 1000 * 60 * 6;
    private static final int FASTEST  = 1000 * 60 * 3;


    public static String motorName = "toyota";

    private String serviceAddress = "";
    private AddressResult addressResult;
    private Location serviceLocation;

    protected ProgressDialog progressDialog;
    private MaterialButton materialButton;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        sessionManager = new SessionManager(this);

        // Where user want the service
        serviceLocation = new Location(LocationManager.GPS_PROVIDER);

        progressDialog = new ProgressDialog(this);
        providerClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.carRecycler);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//
//        List<Motor> motors = new ArrayList<>();
//        motors.add(new Motor("Benz", R.drawable.car));
//        motors.add(new Motor("Toyota", R.drawable.car1));
//        motors.add(new Motor("Honda", R.drawable.car2));
//        motors.add(new Motor("Venza", R.drawable.car3));
//        motors.add(new Motor("Jeep", R.drawable.car4));
//        motors.add(new Motor("Mazda", R.drawable.car5));
//        motors.add(new Motor("Misibushi", R.drawable.car6));
//
//        CarAdapter carAdapter = new CarAdapter(motors, this);
//        recyclerView.setAdapter(carAdapter);

        materialButton = (MaterialButton) findViewById(R.id.btnContinue);

        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (lastLocation != null) {
            marker = mMap.addMarker(new MarkerOptions().draggable(true).position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).title("My Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 15.5f));
        }

        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();

                serviceLocation.setLatitude(latLng.latitude);
                serviceLocation.setLongitude(latLng.longitude);

                marker = mMap.addMarker(new MarkerOptions().draggable(true).position(latLng).title("My Current Location"));
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                //
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                //
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                serviceLocation.setLongitude(marker.getPosition().longitude);
                serviceLocation.setLatitude(marker.getPosition().latitude);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        providerClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lastLocation = location;
                    Util.showMessage(getApplicationContext(), " " + lastLocation.getLongitude());
                    updateMap(lastLocation);
                }
                else {

                    LocationCallback callback = new LocationCallback(){
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            if (locationResult != null) {
                                for (Location loc: locationResult.getLocations()) {
                                    if (loc != null) {
                                        lastLocation = loc;
                                        updateMap(lastLocation);
                                    }
                                }
                            }
                        }
                    };

                    providerClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        apiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //failed
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        updateMap(location);
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiClient.connect();
        mapFragment.onResume();

        providerClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult != null) {
                    for (Location location: locationResult.getLocations()) {
                        if (location != null) {
                            lastLocation = location;
                            updateMap(lastLocation);
                        }
                    }
                }
            }
        }, Looper.getMainLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapFragment.onPause();
        providerClient.removeLocationUpdates(new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        });
        apiClient.disconnect();
    }

    private void updateMap(Location location) {

        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();

            marker = mMap.addMarker(new MarkerOptions()
            .position(latLng)
                    .title("My Current Location")
            .draggable(true));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f));
        }
    }


    private void sendRequest() {
        if (serviceLocation.getLatitude() != 0 && serviceLocation.getLongitude() != 0) {
           progressDialog.setMessage("Please wait...");
           progressDialog.show();

            if (!Geocoder.isPresent()) {
                Util.showMessage(this, "Can not convert your location to actual Address.");
                return;
            }

            if (NetworkChecker.isNetworkOn(getApplicationContext())) {
                new LongRun().execute(serviceLocation);
            }
            else {
                Util.showMessage(getApplicationContext(), "You are offline. Connect to a WIFI to continue.");
            }
        }
    }


    class AddressResult extends ResultReceiver {
        public AddressResult(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            progressDialog.dismiss();
            Util.showMessage(getApplicationContext(), "Back");

            if (resultData == null) {
                Util.showMessage(getApplicationContext(), "Address was not Found");
            }
            else {

                if (resultCode == AppConfig.RESULT_SUCCESS) {
                    Util.showMessage(getApplicationContext(), "OK " + resultData.getString(AppConfig.RESULT_DATA_KEY));
                }
                else {
                    Util.showMessage(getApplicationContext(), "Error " + resultData.getString(AppConfig.RESULT_DATA_KEY));
                }
            }
        }
    }

    class LongRun extends AsyncTask<Location, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Location... locations) {
            Intent intent = new Intent(getApplicationContext(), AddressCoder.class);

            intent.putExtra(AppConfig.RECEIVER, addressResult);
            intent.putExtra(AppConfig.LOCATION_DATA_EXTRA, serviceLocation);

            if (serviceLocation == null) {
               Util.showMessage(getApplicationContext(), "Please move the Marker to Your service Location");
               return "";
            }

            try {
                List<Address> addresses = Controller.getInstance().getGeocoder().getFromLocation(serviceLocation.getLatitude(), serviceLocation.getLongitude(), 1);

                progressDialog.dismiss();

                if (addresses == null || addresses.size() == 0) {
                    Util.showMessage(getApplicationContext(), "Sorry! No Address Found.");
                    return "";
                }

                String line = addresses.get(0).getAddressLine(0);

                serviceAddress = line;

                publishProgress(line);

            } catch (Exception e) {

                progressDialog.dismiss();
                Util.showMessage(getApplicationContext(), "Error! Could not fetch your location Address. Please try again.");
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            responseDialog(values[0]);
        }
    }

    private void responseDialog(final String string) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Your Address?");
        builder.setMessage(string);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                makeRequest(string);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Util.showMessage(getApplicationContext(), "Move The marker to Your Location Area");
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void makeRequest(final String address) {

        progressDialog.setMessage("Sending request...");
        progressDialog.show();

        final double latitude = serviceLocation.getLatitude();
        final double longitude = serviceLocation.getLongitude();
        final String carName = motorName;
        final String email = sessionManager.getUsername();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getBoolean("error")) {
                        Util.showMessage(getApplicationContext(), jsonObject.getString("errorMessage"));
                    }
                    else {

                        JSONObject data = jsonObject.getJSONObject("data");
                        String created = data.getString("created");

                        //new DataManager(getApplicationContext()).addRequest(email, carName, String.valueOf(R.drawable.car), address, String.valueOf(latitude), String.valueOf(longitude), created);

                        Util.showMessage(getApplicationContext(), jsonObject.getString("message"));
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Util.showMessage(getApplicationContext(), e.getMessage());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Util.showMessage(getApplicationContext(), error.getMessage());
                        error.printStackTrace();

                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("email", email);
                map.put("latitude", String.valueOf(latitude));
                map.put("longitude", String.valueOf(longitude));
                map.put("address", address);
                map.put("carName", carName);
                map.put("time", Util.getTime());
                map.put("page", "car-request");

                return  map;
            }
        };

        Controller.getInstance().addRequestQueue(stringRequest);
    }
}