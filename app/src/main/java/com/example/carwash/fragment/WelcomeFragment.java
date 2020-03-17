package com.example.carwash.fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carwash.OnFragmentChangeListener;
import com.example.carwash.R;
import com.example.carwash.helper.SessionManager;
import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {

    private OnFragmentChangeListener listener;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (OnFragmentChangeListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        final SessionManager manager = new SessionManager(getContext());

        MaterialButton button = (MaterialButton) view.findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SessionManager(getContext()).setIsFirstTime(false);
                manager.setIsFirstTime(false);
                listener.onFragmentChange(new LoginFragment(), true);
            }
        });

        return view;
    }

}
