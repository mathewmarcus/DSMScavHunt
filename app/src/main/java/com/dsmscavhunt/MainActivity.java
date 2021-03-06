package com.dsmscavhunt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private Location myLocation = null;
    private GoogleApiClient mGoogleApiClient;
    Button button;
    ImageView imageView;
    static final int CAM_REQUEST = 1;
    private LocationRequest mLocationRequest;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface helvetica = Typeface.createFromAsset(getAssets(), "Helvetica.dfont");
        TextView directionsView = (TextView) findViewById(R.id.scavDirections);
        directionsView.setTypeface(helvetica);

        // Create view objects to store database contents
        TextView scavNumber = (TextView)findViewById(R.id.scavNumber);
        TextView scavDirections = (TextView)findViewById(R.id.scavDirections);
        ImageView scavImage = (ImageView)findViewById(R.id.scavImage);

        // Access database
        DBHandler dbh = new DBHandler(this);

        // Add items to database
        dbh.addScavItem(new ScavItem("Aliber", "2847 University Avenue, Des Moines, IA 50311", "Task One: \n This bulldog means business, \n wearing the suit and tie of dogs \n - a simple black collar.", R.drawable.aliberdog));
        dbh.addScavItem(new ScavItem("Meredith", "2805 University Avenue, Des Moines, IA 50311", "Task Two: \n A dog is a man’s best friend. \n You can find this jersey-wearing \n bulldog with Meredith.", R.drawable.meredithdog));
        dbh.addScavItem(new ScavItem("Olmsted (outside)", "2875 University Avenue, Des Moines, IA 50311", "Task Three: \n Drake’s ‘signature’ bulldog lives right \n outside the campus hub for students.", R.drawable.olmstedoutsidedog));
        dbh.addScavItem(new ScavItem("Olmsted (inside)", "2875 University Avenue, Des Moines, IA 50311", "Task Four: \n A picture’s worth a thousand words? \n The bulldog, at home in the epicenter of \n student life, has a least a million things to say.", R.drawable.olmstedinsidedog));
        dbh.addScavItem(new ScavItem("Cheeseburger", "1315 30th Street, Des Moines, IA 50311", "Task Five: \n You are what you eat. \n Or at least the cheeseburger bulldog is.", R.drawable.hubbelldog));
        dbh.addScavItem(new ScavItem("West Village One", "1325 31st Street, Des Moines, IA 50311", "Task Six: \n To the west, to the west. \n Every bulldog at the village to the west.", R.drawable.westvillagedog));
        dbh.addScavItem(new ScavItem("Bookstore", "3003 Forest Ave, Des Moines, IA 50311", "Task Seven: \n Don't judge a bulldog by its cover. \n Or was it books?", R.drawable.bookstoredog));
        dbh.addScavItem(new ScavItem("Lab Coat (Cline)", "2802 Forest Avenue, Des Moines, IA 50311", "Task Eight: \n Bulldogs in lab coats are inCLINEd \n to be more science-savvy than most.", R.drawable.clinedog));
        dbh.addScavItem(new ScavItem("Stadium", "2719 Forest Avenue", "Task Nine: \n The ultimate Bulldog fan. \n Find this bronze bulldog that \n supports all athletic teams.", R.drawable.stadiumdog));
        dbh.addScavItem(new ScavItem("Basketball", "2601 Forest Avenue, IA 50311", "Task Ten: \n Shivers me timbers. \n This bronze bulldog spends his time \n hanging with the basketball team.", R.drawable.basketballdog));


        // Store items in list of ScavItem objects
        List<ScavItem> scavItems = dbh.getAllScavItems();

         /*
            If scav hunt is in progress, load current scav item,
            else start the hunt from the beginning
         */
        if (savedInstanceState != null) {
            Log.i(TAG, "SavedInstanceState is not null");
            int rowNumbertoDisplay = savedInstanceState.getInt("ID");
            scavNumber.setText(" " + String.valueOf(scavItems.get(rowNumbertoDisplay).get_id()));
            scavDirections.setText(scavItems.get(rowNumbertoDisplay).get_directions());
            scavImage.setImageResource(scavItems.get(rowNumbertoDisplay).get_image());

        }
        else {

            Log.i(TAG, "SavedInstanceState is null");
            scavNumber.setText(" " + String.valueOf(scavItems.get(0).get_id()));
            scavDirections.setText(scavItems.get(0).get_directions());
            scavImage.setImageResource(scavItems.get(0).get_image());
        }

        for (ScavItem cn : scavItems) {
            String log = "Id: "+cn.get_id()+" ,Name: " + cn.get_name() + " ,Address: " + cn.get_address() + " ,Directions: " + cn.get_directions() + " ,Image: " + cn.get_image();
            // Writing Contacts to log
            Log.d("Name: ", log);
        }

        // Register Google API Client to listen for location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        button = (Button) findViewById(R.id.cam_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getFile();
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera_intent,CAM_REQUEST);
            }
        });



    }

    /* When camera intent fires, if the user is not connected to cell network or wifi, a toast
        asks them to do so
      */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!isconnected()) {
            Context context = getApplicationContext();
            CharSequence error = "Network error. Please connect to a cell network or wifi and try again.";
            int duration = Toast.LENGTH_LONG;

            Toast result = Toast.makeText(context, error, duration);
            result.show();
            return;
        }

        DBHandler dbh = new DBHandler(this);
        int numRows = dbh.getScavItemCount();
        List<ScavItem> scavItems = dbh.getAllScavItems();


        TextView scavNumber = (TextView) findViewById(R.id.scavNumber);

        int rowNumbertoDisplay = Integer.parseInt(((String) scavNumber.getText()).trim());

        LatLng bulldogLatLng = stringToLatLong(scavItems.get(rowNumbertoDisplay).get_address());

        Location bulldogLocation = new Location("scavItem");
        bulldogLocation.setLatitude(bulldogLatLng.latitude);
        bulldogLocation.setLongitude(bulldogLatLng.longitude);

        // Check if location
        equals(bulldogLocation, myLocation, scavItems);
        }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }


    private File getFile()
    {
        File folder = new File("sdcard/camera_app");
        if (!folder.exists())
        {
            folder.mkdir();
        }
        File image_file = new File(folder, "cam_image.jpg");
        for(int i=1; image_file.exists(); i++){
            image_file = new File(folder, "cam_image" + i + ".jpg");
        }
        return image_file;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // On click listener
    @Override
    public void onClick(View v) {
         if (v.getId() == R.id.mapButton) { // Handles Map button clicks
            if (!isconnected()) {
                Context context = getApplicationContext();
                CharSequence error = "Network error. Please connect to a cell network or wifi and try again.";
                int duration = Toast.LENGTH_LONG;

                Toast result = Toast.makeText(context, error, duration);
                result.show();
                return;
            }

            DBHandler dbh = new DBHandler(this);
            int numRows = dbh.getScavItemCount();
            List<ScavItem> scavItems = dbh.getAllScavItems();


            TextView scavNumber = (TextView) findViewById(R.id.scavNumber);

            int rowNumbertoDisplay = Integer.parseInt(((String) scavNumber.getText()).trim());

            // Pass address of current item to be displayed in MapsActivity
            Intent i = new Intent(this, MapsActivity.class);
            i.putExtra("address", scavItems.get(rowNumbertoDisplay).get_address());
            startActivity(i);
        }
    }

    // Saves the users current state in case if they leave the activity
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "Method: onSaveInstanceState");
        TextView scavNumber = (TextView) findViewById(R.id.scavNumber);
        int rowNumbertoDisplay = Integer.parseInt(((String) scavNumber.getText()).trim());


        // Save the user's current game state
        savedInstanceState.putInt("ID", rowNumbertoDisplay);
        Log.i(TAG, " " + savedInstanceState.getInt("ID"));


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "Method: onSaveInstanceState");

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        myLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect");
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

    /* Check if stored location equals location of user when he/she took a picture
        Radius of error is set to allow the user to be within 100m of target
     */
    public void equals(Location bulldogLocation, Location currentLocation, List<ScavItem> scavItems) {
        float radiusOfError = 300;
        Context context = getApplicationContext();
        CharSequence success = "Success!";
        CharSequence failure = "Incorrect, take another picture and try again.";
        int duration = Toast.LENGTH_LONG;
        if (bulldogLocation.distanceTo(currentLocation) <= radiusOfError) {
            Toast result = Toast.makeText(context, success, duration);
            result.show();

            TextView scavNumber = (TextView) findViewById(R.id.scavNumber);
            //TextView scavName = (TextView) findViewById(R.id.scavName);
            //TextView scavAddress = (TextView) findViewById(R.id.scavAddress);
            TextView scavDirections = (TextView) findViewById(R.id.scavDirections);
            ImageView scavImage = (ImageView) findViewById(R.id.scavImage);

            int rowNumbertoDisplay = Integer.parseInt(((String) scavNumber.getText()).trim());

            if (rowNumbertoDisplay == 10) {
                Intent i = new Intent(this, CallToAction.class);
                startActivity(i);
            }


            scavNumber.setText(" " + String.valueOf(scavItems.get(rowNumbertoDisplay).get_id()));
            //scavName.setText(scavItems.get(rowNumbertoDisplay).get_name());
            //scavAddress.setText(scavItems.get(rowNumbertoDisplay).get_address());
            scavDirections.setText(scavItems.get(rowNumbertoDisplay).get_directions());
            scavImage.setImageResource(scavItems.get(rowNumbertoDisplay).get_image());

            rowNumbertoDisplay++;
        }
        else {
            Toast result = Toast.makeText(context, failure, duration);
            result.show();
        }
    }

    // Check if network connection exists
    public boolean isconnected() {
        Context context = getApplicationContext();
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Location services unavailable. Please connect");
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        handleNewLocation(myLocation);
    }
}
