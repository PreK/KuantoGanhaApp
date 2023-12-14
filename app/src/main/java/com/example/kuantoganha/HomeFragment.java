package com.example.kuantoganha;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private SharedPreferencesManager preferencesManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesManager = new SharedPreferencesManager(getContext());
        // Outras inicializações
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String username = preferencesManager.getUsername();
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView welcomeMessage = view.findViewById(R.id.welcomeMessage);
        welcomeMessage.setText("Bem-vindo, " + username + "!");
        return view;
    }
}