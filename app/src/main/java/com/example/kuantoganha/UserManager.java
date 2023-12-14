package com.example.kuantoganha;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserManager {

    private final SharedPreferencesManager preferencesManager;

    public UserManager(Context context) {
        // Inicialize o SharedPreferencesManager aqui
        preferencesManager = new SharedPreferencesManager((Context) context);
    }

    public void logout(Context context) {
        preferencesManager.setLoggedIn(false);

        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            preferencesManager.setLoggedIn(false);

            // Limpe a pilha de fragmentos
            activity.getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            // Redirecione para o LoginFragment
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        }
    }
}
