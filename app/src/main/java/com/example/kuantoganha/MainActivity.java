package com.example.kuantoganha;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText username, password;
    Button btnLogin;
    CheckBox checkSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean saveLogin = preferences.getBoolean("saveLogin", false);
        if(saveLogin){
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
            finish();
        }

        username = findViewById(R.id.editTextLogin);
        password = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        checkSave = findViewById(R.id.checkBoxSave);
    }



    private void loginUser(String username, String password) {
        // Create a request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create a JSON Object containing the user's credentials
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", username);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a JSON Object Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.url, postData,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        if (success) {
                            // Handle successful login
                            // You can also extract other data from the response if necessary
                            Toast.makeText(this, "teste", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle login failure
                            Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Handle JSON parsing error
                        Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle network error
                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                return params;
            }
        };

        // Add the request to the request queue
        queue.add(jsonObjectRequest);
    }
    public void login(View view) {
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        loginUser(username, password);
    }
}