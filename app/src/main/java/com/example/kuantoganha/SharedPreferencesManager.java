package com.example.kuantoganha;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREFERENCES_SUFFIX = ".Preferences";
    private static final String IS_LOGGED_IN_KEY = "IsLoggedIn";
    private static final String USERNAME = "";
    private final SharedPreferences sharedPreferences;


    public SharedPreferencesManager(Context context) {
        String preferencesFileKey = context.getPackageName() + PREFERENCES_SUFFIX;
        this.sharedPreferences = context.getSharedPreferences(preferencesFileKey, Context.MODE_PRIVATE);
    }


    public void setLoggedIn(boolean isLoggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN_KEY, isLoggedIn);
        editor.apply();
    }


    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false);
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, username);
        editor.apply();
    }
}
