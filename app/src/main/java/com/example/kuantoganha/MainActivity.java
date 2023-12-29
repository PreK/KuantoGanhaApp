package com.example.kuantoganha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;



public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        SharedPreferencesManager preferencesManager = new SharedPreferencesManager(this);
        boolean isLoggedIn = preferencesManager.isLoggedIn();

        initializeApp();

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_logout:
                        UserManager userManager = new UserManager(getApplicationContext());
                        userManager.logout(getApplicationContext());
                        loadFragment(new LoginFragment());
                        return true;
                    case R.id.nav_register:
                        loadFragment(new RegisterFragment());
                        return true;

                    case R.id.nav_login:
                        loadFragment(new LoginFragment());
                        return true;
                }
                return false;

            }
        });
    }


    //
    public void loadFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }

        if (fragment instanceof HomeFragment) {
            try {
                bottomNav.getMenu().clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bottomNav.inflateMenu(R.menu.logged_in_menu);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
        if (fragment instanceof LoginFragment) {
            try {
                bottomNav.getMenu().clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bottomNav.inflateMenu(R.menu.menu_main);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }


    }

    private void initializeApp() {
        SharedPreferencesManager preferencesManager = new SharedPreferencesManager(this);
        boolean isLoggedIn = preferencesManager.isLoggedIn();

        if (isLoggedIn) {
            loadFragment(new HomeFragment());
        }
    }

    public void onRegistrationSuccess() {
        loadFragment(new LoginFragment());
    }
}