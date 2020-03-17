package com.example.carwash.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.BaseKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chaos.view.PinView;
import com.example.carwash.Controller;
import com.example.carwash.MainActivity;
import com.example.carwash.MapsActivity;
import com.example.carwash.R;
import com.example.carwash.RegisterActivity;
import com.example.carwash.helper.AppConfig;
import com.example.carwash.helper.SessionManager;
import com.example.carwash.helper.Util;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    private ProgressDialog progressDialog;
    private AppCompatActivity activity;
    private SessionManager sessionManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        activity = (AppCompatActivity) getActivity();
        sessionManager = new SessionManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextView textView = (TextView) view.findViewById(R.id.textView);
        final PinView pinView = (PinView) view.findViewById(R.id.pinView);
        MaterialButton button = (MaterialButton) view.findViewById(R.id.btnRegister);

        final TextInputEditText text = (TextInputEditText) view.findViewById(R.id.email);
        text.setText(sessionManager.getUsername());

        textView.setText(sessionManager.getUsername());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RegisterActivity.class));
            }
        });


        // Boxes
        final int counts = pinView.getItemCount();

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == counts) {

                    if (!text.getText().toString().equalsIgnoreCase("")) {
                        login(charSequence.toString(), text.getText().toString());
                    }
                    else {
                        Util.showMessage(getContext(), "Provide Email");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void login(final String password, final String email) {

        if (password != null) {

            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("error")) {
                            Util.showMessage(getContext(), jsonObject.getString("errorMessage"));
                        }
                        else {

                            sessionManager.setLogin(true);
                            startActivity(new Intent(getContext(), MainActivity.class));
                            activity.finish();
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

                            Util.showMessage(getContext(), error.getMessage());
                            progressDialog.dismiss();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();
                    map.put("email", email);
                    map.put("password", password);
                    map.put("page", "car-login");

                    return  map;
                }
            };

            Controller.getInstance().addRequestQueue(stringRequest);
        }
    }
}