package com.example.carwash;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.carwash.fragment.LoginFragment;
import com.example.carwash.fragment.MainFragment;
import com.example.carwash.fragment.NoNetworkFragment;
import com.example.carwash.fragment.RegisterFragment;
import com.example.carwash.fragment.WelcomeFragment;
import com.example.carwash.helper.DataManager;
import com.example.carwash.helper.NetworkChecker;
import com.example.carwash.helper.SessionManager;
import com.example.carwash.helper.UserLocation;
import com.example.carwash.helper.Util;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity implements OnFragmentChangeListener {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        if (sessionManager.isFirstTime()) {
           changeFragment(new WelcomeFragment(), false);
        }
        else {
            if (NetworkChecker.isNetworkOn(this)) {
                changeFragment(new LoginFragment(), false);
            }
            else {
                changeFragment(new NoNetworkFragment(), false);
            }
        }
    }

    private void changeFragment(Fragment fragment, boolean backTrack) {

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .add(R.id.container, fragment);
        if (backTrack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentChange(Fragment fragment, boolean backTrack) {
        changeFragment(fragment, backTrack);
    }
}
