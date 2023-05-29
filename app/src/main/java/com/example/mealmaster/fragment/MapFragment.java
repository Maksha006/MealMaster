package com.example.mealmaster.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.mealmaster.MapsRecipes;
import com.example.mealmaster.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    View rootView;

    GoogleMap gMaps;
    FrameLayout map;

    HashMap<LatLng, String> countryTags = new HashMap<>();

    LatLng RDC= new LatLng(-2.9814344,23.8222636);
    LatLng USA= new LatLng( 39.7837304,-100.445882);
    LatLng China= new LatLng(35.000074,104.999927);
    LatLng French= new LatLng(46.603354,1.8883335);
    LatLng Italia= new LatLng(42.6384261,12.674297);
    LatLng Japan= new LatLng(36.5748441,139.2394179);
    LatLng Mexico= new LatLng(23.6585116,-102.0077097);



    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        map = rootView.findViewById(R.id.map);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        countryTags.put(RDC, "african");
        countryTags.put(USA, "american");
        countryTags.put(China, "chinese");
        countryTags.put(French, "french");
        countryTags.put(Italia, "italian");
        countryTags.put(Japan, "japanese");
        countryTags.put(Mexico, "mexican");

        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMaps = googleMap;
        for (Map.Entry<LatLng, String> entry : countryTags.entrySet()) {
            LatLng latLng = entry.getKey();
            String tag = entry.getValue();
            Marker marker = gMaps.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            marker.setTag(tag);  // Associez le tag au marqueur
        }

        gMaps.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                List<String> tags = new ArrayList<>();

                for (Map.Entry<LatLng, String> entry : countryTags.entrySet()) {
                    LatLng latLng = entry.getKey();
                    String tag = entry.getValue();

                    if (marker.getPosition().equals(latLng)) {
                        tags.add(tag);
                    }
                }

                // Passer la liste de tags à l'activité MapsRecipes
                Intent intent = new Intent(MapFragment.this.getActivity(), MapsRecipes.class);
                intent.putStringArrayListExtra("CuisineTypes", (ArrayList<String>) tags);
                startActivity(intent);

                return false;
            }
        });
    }
}