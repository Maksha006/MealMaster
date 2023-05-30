package com.example.mealmaster.fragment;

import static android.content.ContentValues.TAG;

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
import com.google.firebase.database.ValueEventListener;

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
        view = inflater.inflate(R.layout.fragment_favorite, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
        fvrtRef = databaseReference.child(user.getUid()).child("favorites");

        // Initialisez votre RecyclerView et votre ArrayList
        recyclerView = view.findViewById(R.id.favorite_list_recyclerView); // Remplacez par l'ID de votre RecyclerView
        fvrtRecipe = new ArrayList<>();

        adapter = new FavoriteItemsAdapter(getContext(), fvrtRecipe);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lire les recettes favorites de l'utilisateur de Firebase

        final DatabaseReference favoritesRef = database.getReference("Users").child(user.getUid()).child("favorites");
        fvrtRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String recipeId = dataSnapshot.getKey();  // Ici vous obtenez l'ID de la recette
                getRecipeDetails(recipeId);  // Appel de la méthode pour récupérer les détails de la recette
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Lorsqu'une recette est supprimée des favoris, retirez-la de l'ArrayList et mettez à jour l'adaptateur
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                fvrtRecipe.remove(recipe);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void getRecipeDetails(String recipeId) {
        DatabaseReference recipeRef = database.getReference("recipes").child(recipeId);
        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                if (recipe != null) {
                    fvrtRecipe.add(recipe); // Ajouter la recette à votre liste de recettes favorites
                    adapter.notifyDataSetChanged(); // Avertir l'adapter que les données ont changé
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs ici
            }
        });
    }



}