package com.example.carwash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.carwash.fragment.AboutFragment;
import com.example.carwash.fragment.ContactFragment;
import com.example.carwash.fragment.LoginFragment;
import com.example.carwash.fragment.LogsFragment;
import com.example.carwash.fragment.MainFragment;
import com.example.carwash.fragment.PolicyFragment;
import com.example.carwash.fragment.ProfileFragment;
import com.example.carwash.fragment.RegisterFragment;
import com.example.carwash.fragment.ServicesFragment;
import com.example.carwash.fragment.SettingsFragment;
import com.example.carwash.helper.AppConfig;
import com.example.carwash.helper.DataManager;
import com.example.carwash.helper.NetworkChecker;
import com.example.carwash.helper.SessionManager;
import com.example.carwash.helper.UserLocation;
import com.example.carwash.helper.Util;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private DrawerLayout drawerLayout;

    private static final int REQUEST_CODE = 20;
    private static final int INTERVAL = 1000 * 60 * 6;
    private static final int FASTEST  = 1000 * 60 * 3;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Session Manager
        sessionManager = new SessionManager(this);
        DataManager manager = new DataManager(getApplicationContext());


        // Check for Permissions
        if (!hasPermission())
            requestPermission();


        // Location checking
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            checkSettings();


        if (NetworkChecker.isNetworkOn(this)) {

            setContentView(R.layout.activity_main);

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

            drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

            setupNavigation();
            changeFragment(new MainFragment());
        }
        else {

            setContentView(R.layout.no_network);
            AppCompatButton btnRetry = (AppCompatButton) findViewById(R.id.btnRetry);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if (NetworkChecker.isNetworkOn(getApplicationContext())) {

                       changeFragment(new MainFragment());
                   }
                   else
                       Util.showMessage(MainActivity.this, "You are still Offline.");
                }
            });
        }
    }

    private void changeFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.container, fragment)
                .commit();
    }

    private void setupNavigation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav);
        View view = navigationView.getHeaderView(0);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView email = (TextView) view.findViewById(R.id.email);
        email.setText(sessionManager.getUsername());
        name.setText(sessionManager.getName());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.navHome:
                        changeFragment(new MainFragment());
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.navContact:
                        changeFragment(new ContactFragment());
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.navAbout:
                        changeFragment(new AboutFragment());
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.navPolicy:
                        changeFragment(new PolicyFragment());
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.navServices:
                        changeFragment(new ServicesFragment());
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.logout:
                        sessionManager.setLogin(false);
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                }

                item.setChecked(true);

                return false;
            }
        });
    }

    private boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION
        , Manifest.permission.INTERNET,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void checkSettings() {

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.setAlwaysShow(false);

        LocationRequest request = LocationRequest.create();
        request.setInterval(INTERVAL);
        request.setFastestInterval(FASTEST);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        builder.addLocationRequest(request);
        Task<LocationSettingsResponse> settingsResponseTask = settingsClient.checkLocationSettings(builder.build());

        settingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            }
        });

        settingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException apiException = (ResolvableApiException) e;

                        apiException.startResolutionForResult(MainActivity.this, REQUEST_CODE);
                    }
                    catch (IntentSender.SendIntentException e1) {
                        Util.logMessage(MainActivity.this, e1.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }
}
