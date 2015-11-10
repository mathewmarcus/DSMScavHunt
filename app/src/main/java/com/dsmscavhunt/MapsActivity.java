package com.dsmscavhunt;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/*
    Mathew Marcus
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MapsInitializer.initialize(getApplicationContext());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        Intent i = getIntent();
        String address = i.getStringExtra("address");
        LatLng scavDestination = stringToLatLong(address);
        // Add markers
        mMap.addMarker(new MarkerOptions().position(scavDestination).title("Bulldog"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(scavDestination));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(scavDestination, 17.0f));
    }

    // Convert string street addresses into corresponding LatLng values
    public LatLng stringToLatLong(String musicVenue) {
        Geocoder geo = new Geocoder(this);
        List<Address> venueAddressList = null;
        Address venueAddress = null;
        LatLng venueLatLng = null;
        try {
            venueAddressList = geo.getFromLocationName(musicVenue, 1);
            venueAddress = venueAddressList.get(0);
            venueLatLng = new LatLng(venueAddress.getLatitude(), venueAddress.getLongitude());
        } catch (IOException e) {
            Log.i(TAG, "Failed to convert address to coordinates", e);
        }

        return venueLatLng;

    }

}
