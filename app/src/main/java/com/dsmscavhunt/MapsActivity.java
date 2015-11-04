package com.dsmscavhunt;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location myLocation = null;
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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        Intent i = getIntent();
        String address = i.getStringExtra("address");
        LatLng scavDestination = stringToLatLong(address);
        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(scavDestination).title("Bulldog"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(scavDestination));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(scavDestination, 17.0f));
    }

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

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        myLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(myLocation.getLatitude(), myLocation.getLongitude())), 13.0f));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Error. Location services failed");
    }
}
