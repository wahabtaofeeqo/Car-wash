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
import com.example.carwash.MapsActivity;
import com.example.carwash.R;
import com.example.carwash.helper.AppConfig;
import com.example.carwash.helper.SessionManager;
import com.example.carwash.helper.Util;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements Validator.ValidationListener {


    private MaterialButton btnRegister;

    @NotEmpty
    private TextInputEditText editName;

    @NotEmpty
    private TextInputEditText editEmail;

    @NotEmpty
    private TextInputEditText editPhone;

    @Password(min = 5, scheme = Password.Scheme.NUMERIC)
    private TextInputEditText editPassword;

    @ConfirmPassword
    private TextInputEditText editCPassword;


    private Validator validator;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;

    private AppCompatActivity activity;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validator = new Validator(this);
        validator.setValidationListener(this);
        progressDialog = new ProgressDialog(getContext());

        activity = (AppCompatActivity) getActivity();
        sessionManager = new SessionManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        editName = (TextInputEditText) view.findViewById(R.id.editName);
        editEmail = (TextInputEditText) view.findViewById(R.id.editEmail);
        editPhone = (TextInputEditText) view.findViewById(R.id.editPhone);
        editPassword = (TextInputEditText) view.findViewById(R.id.editPassword);
        editCPassword = (TextInputEditText) view.findViewById(R.id.editCPassword);

        btnRegister = (MaterialButton) view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
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

        register();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Util.showMessage(getContext(), "Make Sure You Provide Valid Data");
    }

    private void register() {

        final String email = editEmail.getText().toString();
        final String name = editName.getText().toString();
        final String phone = editPhone.getText().toString();
        final String password = editPassword.getText().toString();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getBoolean("error")) {
                        Util.showMessage(getContext(), jsonObject.getString("errorMessage"));
                    }
                    else {
                        sessionManager.setIsFirstTime(false);
                        sessionManager.setRegistered(true);
                        sessionManager.setUsername(email);
                        sessionManager.setName(name);

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
                map.put("email", email);
                map.put("name", name);
                map.put("phone", phone);
                map.put("password", password);
                map.put("page", "car-register");

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
                startActivity(new Intent(getContext(), MainActivity.class));
                activity.finish();
            }
        });

        dialog.show();
    }
}
