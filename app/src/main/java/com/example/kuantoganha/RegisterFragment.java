package com.example.kuantoganha;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterFragment extends Fragment {

    private EditText editTextUsername, editTextPassword, editTextEmail, editTextConfirmPassword;
    private Button btnRegister;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize UI components
        editTextUsername = view.findViewById(R.id.editTextRegisterUsername);
        editTextPassword = view.findViewById(R.id.editTextRegisterPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextRegisterConfirmPassword);
        editTextEmail = view.findViewById(R.id.editTextRegisterEmail);
        btnRegister = view.findViewById(R.id.buttonRegister);

        btnRegister.setOnClickListener(v -> attemptRegistration());

        return view;
    }

    private void attemptRegistration() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show();
        } else {
            registerUser(username, password, confirmPassword, email);
        }
    }

    private void registerUser(String username, String password, String confirmPassword, String email) {
        String confUrl = Config.url + "register.php";
        new Thread(() -> {
            try {
                URL url = new URL(confUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("username", username);
                json.put("password", password);
                json.put("confirm_password", confirmPassword);
                json.put("email", email);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(json.toString());
                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream responseStream = connection.getInputStream();
                    String response = streamToString(responseStream);

                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                        if (response.contains("success")) {
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).onRegistrationSuccess();
                            }
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), getString(R.string.error_during_registration), Toast.LENGTH_LONG).show();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private String streamToString(InputStream stream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append('\n');
        }
        return stringBuilder.toString();
    }
}