package com.example.mealmaster.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mealmaster.Adapter.FavoriteItemsAdapter;
import com.example.mealmaster.R;
import com.example.mealmaster.User;
import com.example.mealmaster.model.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FavoriteFragment extends Fragment  {

    View view;

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference, fvrtRef , fvrt_ListRef;
    FirebaseDatabase database;

    Boolean fvrtChecker = false;

    FavoriteItemsAdapter adapter;
    ArrayList<Recipe> fvrtRecipe;

    RecyclerView recyclerView;

    ImageView imageView;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return view;
    }

}