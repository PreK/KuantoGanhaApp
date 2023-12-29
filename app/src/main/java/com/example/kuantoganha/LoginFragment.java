package com.example.kuantoganha;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private EditText editTextUsername, editTextPassword;
    private Button btnLogin;
    private CheckBox checkBoxSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        editTextUsername = view.findViewById(R.id.editTextLogin);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        btnLogin = view.findViewById(R.id.buttonLogin);
        checkBoxSave = view.findViewById(R.id.checkBoxSave);

        btnLogin.setOnClickListener(v -> attemptLogin());

        return view;
    }

    private void attemptLogin() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show();
        } else {
            loginUser(username, password);
        }
    }
    private void loginUser(String username, String password) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String confUrl = Config.url + "login.php";

        JSONObject postData = new JSONObject();
        try {
            postData.put("username", username);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, confUrl, postData,
                this::handleLoginResponse,
                error -> Toast.makeText(getContext(),  getString(R.string.network_error) + ": " + error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                return params;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void handleLoginResponse(JSONObject response) {
        try {
            boolean success = response.getBoolean("success");
            String message = response.getString("message");
            SharedPreferencesManager preferencesManager = new SharedPreferencesManager(getContext());

            if (success) {
                if (checkBoxSave.isChecked()) {
                    preferencesManager.setLoggedIn(true);
                }
                Toast.makeText(getContext(), getString(R.string.login_successful), Toast.LENGTH_SHORT).show();


                // Trocar para o HomeFragment
                HomeFragment homeFragment = new HomeFragment();
                preferencesManager.setUsername(editTextUsername.getText().toString().trim());
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .commit();


                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
                bottomNav.getMenu().clear();
                bottomNav.inflateMenu(R.menu.logged_in_menu);
            } else {
                Toast.makeText(getContext(), getString(R.string.login_failed) + " " + message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}