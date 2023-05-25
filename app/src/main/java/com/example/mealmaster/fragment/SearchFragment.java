package com.example.mealmaster.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

import com.example.mealmaster.R;
import com.example.mealmaster.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private View rootView;
    private static final String API_KEY = "af3b71ca41664ff586770e97ce55e795";
    private static final int SEARCH_NUMBER = 25;
    private EditText ingredientTxt;
    ProgressDialog dialog;
    private List<Recipe> recipeList = new ArrayList<>();

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading...");

        ingredientTxt = rootView.findViewById(R.id.ingredientsTxt);


        return rootView;
    }
}