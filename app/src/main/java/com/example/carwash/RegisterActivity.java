package com.example.carwash;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.carwash.fragment.RegisterFragment;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (savedInstanceState == null) {
             //Register
                getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.container, new RegisterFragment())
                    .commit();
        }
    }
}
