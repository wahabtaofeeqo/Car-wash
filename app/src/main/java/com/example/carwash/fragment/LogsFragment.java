package com.example.carwash.fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carwash.LogsAdapter;
import com.example.carwash.R;
import com.example.carwash.helper.DataManager;
import com.example.carwash.helper.ServiceRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogsFragment extends Fragment {

    private AppCompatActivity activity;
    private RecyclerView recyclerView;

    public LogsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_logs, container, false);
        activity.getSupportActionBar().setTitle("Logs");

        recyclerView = (RecyclerView) view.findViewById(R.id.logsRecycler);

        DataManager manager = new DataManager(getContext());
        List<ServiceRequest> requests = manager.getRequests();

        LogsAdapter adapter = new LogsAdapter(getContext(), requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        return view;
    }

}
