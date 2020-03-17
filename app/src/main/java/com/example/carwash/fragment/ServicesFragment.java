package com.example.carwash.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.carwash.Controller;
import com.example.carwash.R;
import com.example.carwash.RequestData;
import com.example.carwash.ServiceAdapter;
import com.example.carwash.helper.AppConfig;
import com.example.carwash.helper.SessionManager;
import com.example.carwash.helper.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServicesFragment extends Fragment {

    private ProgressDialog progressDialog;
    private List<RequestData> list;
    private ServiceAdapter adapter;

    private SessionManager manager;

    public ServicesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        manager = new SessionManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);

        list = new ArrayList<>();
        adapter = new ServiceAdapter(getContext(), list, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        recyclerView.setAdapter(adapter);

        getServices();

        return view;
    }

    private void getServices() {

        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        final String email = manager.getUsername();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Util.BASE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("error")) {
                            Util.showMessage(getContext(), jsonObject.getString("errorMessage"));
                        }
                        else {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject row = jsonArray.getJSONObject(i);
                                RequestData data = new RequestData();
                                data.setName(row.getString("name"));
                                data.setPhone(row.getString("phone"));
                                data.setAddress(row.getString("address"));
                                data.setTime(row.getString("time"));
                                data.setCreated(row.getString("created"));
                                data.setStatus(row.getInt("status"));
                                data.setId(row.getInt("id"));
                                data.setUserId(row.getInt("user_id"));

                                list.add(data);
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("page", "car-my-requests");
                map.put("email", email);

                return map;
            }
        };

        Controller.getInstance().addRequestQueue(stringRequest);
    }

    public void confirm(RequestData data) {

        final String id = String.valueOf(data.getId());
        final String userId = String.valueOf(data.getUserId());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Util.BASE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("error")) {
                        Util.showMessage(getContext(), object.getString("errorMessage"));
                    }
                    else {

                        Util.showMessage(getContext(), object.getString("message"));
                        list.clear();
                        getServices();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("page", "car-confirm");
                map.put("id", id);
                map.put("userId", userId);

                return map;
            }
        };

        Controller.getInstance().addRequestQueue(stringRequest);
    }
}