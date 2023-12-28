package com.example.kuantoganha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/*  para resolver: android Constant expression required
    adicionar em gradle.properties » android.nonFinalResIds=false
*/

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
                // Trate os cliques dos itens do menu aqui
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
        // Carregar fragment
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
        // Carregar menu consoante o framgment
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


    }

    private void initializeApp() {
        SharedPreferencesManager preferencesManager = new SharedPreferencesManager(this);
        boolean isLoggedIn = preferencesManager.isLoggedIn();

        if (isLoggedIn) {
            loadFragment(new HomeFragment()); // Carregar HomeFragment
        }
    }

    public void onRegistrationSuccess() {
        loadFragment(new LoginFragment());
    }
}