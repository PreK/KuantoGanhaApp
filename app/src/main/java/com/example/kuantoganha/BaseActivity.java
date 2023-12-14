package com.example.kuantoganha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MenuDebug", "Menu is being created");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("MenuDebug", "Selected item ID: " + item.getItemId());
        if (item.getItemId() == R.id.nav_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void logoutUser() {
        // Limpar preferências partilhadas ou qualquer outro dado de sessão
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("saveLogin");
        editor.apply();

        // Navegar de volta para a atividade de login ou outro ecrã apropriado
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Fecha a atividade atual
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = preferences.getBoolean("saveLogin", false);
        menu.findItem(R.id.nav_logout).setVisible(isLoggedIn);
        return super.onPrepareOptionsMenu(menu);
    }
}