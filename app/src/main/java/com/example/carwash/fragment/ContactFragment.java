package com.example.carwash.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
import com.example.carwash.MainActivity;
import com.example.carwash.R;
import com.example.carwash.helper.AppConfig;
import com.example.carwash.helper.SessionManager;
import com.example.carwash.helper.Util;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment implements Validator.ValidationListener {


    private AppCompatActivity activity;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    private Validator validator;

    @NotEmpty
    private TextInputEditText subject;

    @NotEmpty
    private TextInputEditText message;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (AppCompatActivity) getActivity();
        sessionManager = new SessionManager(getContext());
        progressDialog = new ProgressDialog(getActivity());

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        subject = (TextInputEditText) view.findViewById(R.id.subject);
        message = (TextInputEditText) view.findViewById(R.id.message);

        //activity.getSupportActionBar().setTitle("Contact Us");
        MaterialButton button = (MaterialButton) view.findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

        return view;
    }

    @Override
    public void onValidationSucceeded() {

        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        contact(sessionManager.getUsername(), subject.getText().toString(), message.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Util.showMessage(activity, "Provide The Required Data");
    }

    private void contact(final String email, final String sub, final String msg) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.CONTACT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getBoolean("error")) {
                        Util.showMessage(getContext(), jsonObject.getString("errorMessage"));
                    }
                    else {

                        subject.setText("");
                        message.setText("");

                        responseDialog(jsonObject.getString("message"));
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
                map.put("page", "car-contact");
                map.put("email", email);
                map.put("subject", sub);
                map.put("message", msg);

                return  map;
            }
        };

        Controller.getInstance().addRequestQueue(stringRequest);
    }

    private void responseDialog(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage(message);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        dialog.show();
    }
}
