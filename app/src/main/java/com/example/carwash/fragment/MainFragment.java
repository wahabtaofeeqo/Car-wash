package com.example.carwash.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.carwash.Controller;
import com.example.carwash.MapsActivity;
import com.example.carwash.R;
import com.example.carwash.helper.DataManager;
import com.example.carwash.helper.ServiceRequest;
import com.example.carwash.helper.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private AppCompatActivity activity;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (AppCompatActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //activity.getSupportActionBar().setTitle("");
        DataManager manager = new DataManager(getContext());
        ServiceRequest request = manager.lastRequest();

        final TextView text = (TextView) view.findViewById(R.id.text);
        MaterialButton btnRequest = (MaterialButton) view.findViewById(R.id.btnRequest);

        text.setText(getResources().getString(R.string.welcomeText));

        btnRequest.setText(getResources().getString(R.string.makeRequest));
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MapsActivity.class));
            }
        });

        setupFirebase();

        return view;
    }

    private void setupFirebase() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {

                    String token = task.getResult().getToken();
                }
            }
        });
    }
}