package com.example.mealmaster.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mealmaster.AuthActivity;
import com.example.mealmaster.LoginActivity;
import com.example.mealmaster.R;
import com.example.mealmaster.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private View rootView;

    private FirebaseAuth mAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){
            rootView = inflater.inflate(R.layout.auth_activity, container, false);
            Button loginButton = rootView.findViewById(R.id.buttonLogin);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Ouvrir la page de connexion
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });

            Button RegisterButton = rootView.findViewById(R.id.buttonRegister);
            RegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Ouvrir la page de connexion
                    Intent intent = new Intent(getActivity(), RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }else {
            // L'utilisateur est connecté, afficher l'interface de profil
            rootView = inflater.inflate(R.layout.fragment_profile, container, false);

            // Get the user's email from shared preferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("user_email", "");

            // Get a reference to the TextView where you want to display the email
            TextView emailTextView = rootView.findViewById(R.id.profile_name); // replace "emailTextView" with the actual ID of your TextView

            // Set the email in the TextView
            emailTextView.setText(userEmail);
        }
        return rootView;
    }

}