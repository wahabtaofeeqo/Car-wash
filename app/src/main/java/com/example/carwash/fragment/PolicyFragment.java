package com.example.carwash.fragment;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carwash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PolicyFragment extends Fragment {


    public PolicyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_policy, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        //activity.getSupportActionBar().setTitle("Policy");
        return view;
    }

}
