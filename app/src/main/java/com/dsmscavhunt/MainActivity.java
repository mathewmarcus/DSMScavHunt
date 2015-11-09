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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    public static final String TAG = MainActivity.class.getSimpleName();
    private Location myLocation = null;
    private GoogleApiClient mGoogleApiClient;
    Button button;
    ImageView imageView;
    static final int CAM_REQUEST = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface helvetica = Typeface.createFromAsset(getAssets(), "Helvetica.dfont");
        TextView directionsView = (TextView) findViewById(R.id.scavDirections);
        directionsView.setTypeface(helvetica);

        TextView scavNumber = (TextView)findViewById(R.id.scavNumber);
        TextView scavDirections = (TextView)findViewById(R.id.scavDirections);
        ImageView scavImage = (ImageView)findViewById(R.id.scavImage);

        DBHandler dbh = new DBHandler(this);


        dbh.addScavItem(new ScavItem("Aliber", "2847 University Avenue, Des Moines, IA 50311", "This bulldog means business, wearing the suit and tie of dogs - a simple black collar", R.drawable.aliberbulldog));
        dbh.addScavItem(new ScavItem("Meredith", "2805 University Avenue, Des Moines, IA 50311", "A dog is a man’s best friend. You can find this jersey-wearing bulldog with Meredith", R.drawable.meredith));
        dbh.addScavItem(new ScavItem("Olmsted (outside)", "2875 University Avenue, Des Moines, IA 50311", "Drake’s ‘signature’ bulldog lives right outside the campus hub for students", R.drawable.olmsted));
        dbh.addScavItem(new ScavItem("Olmsted (inside)", "2875 University Avenue, Des Moines, IA 50311", "Riddle/Clue: A picture’s worth a thousand words? The bulldog, at home in the epicenter of student life, has a least a million things to say", R.drawable.westvillage2));
        dbh.addScavItem(new ScavItem("Cheeseburger", "1315 30th Street, Des Moines, IA 50311", "You are what you eat. Or at least the cheeseburger bulldog is.", R.drawable.cheeseburger));
        dbh.addScavItem(new ScavItem("West Village One", "1325 31st Street, Des Moines, IA 50311", "To the west, to the west. Every bulldog at the village to the west.", R.drawable.westvillage1));
//        dbh.addScavItem(new ScavItem("West Village Two", "1325 31st Street, Des Moines, IA 50311", "sharp left", R.drawable.westvillage2));
        dbh.addScavItem(new ScavItem("Bookstore", "3003 Forest Ave, Des Moines, IA 50311", "Need some cliff notes or textbooks? Find this bulldog buying his books", R.drawable.bulldog1));
        dbh.addScavItem(new ScavItem("Lab Coat (Cline)", "2802 Forest Avenue, Des Moines, IA 50311", "Bulldogs in lab coats are inCLINEd to be more science-savvy than most.", R.drawable.labcoat));
        dbh.addScavItem(new ScavItem("Stadium", "2719 Forest Avenue", "The ultimate Bulldog fan. Find this bronze bulldog that supports all athletic teams", R.drawable.president));
        dbh.addScavItem(new ScavItem("Basketball", "2601 Forest Avenue, IA 50311", "Shivers me timbers. This bronze bulldog spends his time hanging with the basketball team", R.drawable.biker));


        List<ScavItem> scavItems = dbh.getAllScavItems();

        if (savedInstanceState != null) {
            Log.i(TAG, "SavedInstanceState is not null");
            int rowNumbertoDisplay = savedInstanceState.getInt("ID");
            scavNumber.setText(" " + String.valueOf(scavItems.get(rowNumbertoDisplay).get_id()));
            //scavName.setText(scavItems.get(rowNumbertoDisplay).get_name());
            //scavAddress.setText(scavItems.get(rowNumbertoDisplay).get_address());
            scavDirections.setText(scavItems.get(rowNumbertoDisplay).get_directions());
            scavImage.setImageResource(scavItems.get(rowNumbertoDisplay).get_image());

        }
        else {

            Log.i(TAG, "SavedInstanceState is null");
            scavNumber.setText(" " + String.valueOf(scavItems.get(0).get_id()));
            //scavName.setText(scavItems.get(0).get_name());
            //scavAddress.setText(scavItems.get(0).get_address());
            scavDirections.setText(scavItems.get(0).get_directions());
            scavImage.setImageResource(scavItems.get(0).get_image());
        }

        for (ScavItem cn : scavItems) {
            String log = "Id: "+cn.get_id()+" ,Name: " + cn.get_name() + " ,Address: " + cn.get_address() + " ,Directions: " + cn.get_directions() + " ,Image: " + cn.get_image();
            // Writing Contacts to log
            Log.d("Name: ", log);
        }

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Context context = getApplicationContext();
//        CharSequence text = "Activity Finished";
//        int duration = Toast.LENGTH_SHORT;
//        Toast result = Toast.makeText(context, text, duration);
//        result.show();

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

        Location bulldogLocation = new Location("test");
        bulldogLocation.setLatitude(bulldogLatLng.latitude);
        bulldogLocation.setLongitude(bulldogLatLng.longitude);

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nextButton) {

            DBHandler dbh = new DBHandler(this);
            int numRows = dbh.getScavItemCount();
            List<ScavItem> scavItems = dbh.getAllScavItems();


            TextView scavNumber = (TextView) findViewById(R.id.scavNumber);
            //TextView scavName = (TextView) findViewById(R.id.scavName);
            //TextView scavAddress = (TextView) findViewById(R.id.scavAddress);
            TextView scavDirections = (TextView) findViewById(R.id.scavDirections);
            ImageView scavImage = (ImageView) findViewById(R.id.scavImage);

            int rowNumbertoDisplay = Integer.parseInt(((String) scavNumber.getText()).trim());

            if (rowNumbertoDisplay == 10) {
                Intent i = new Intent(this, CallToAction.class);
                startActivity(i);
                return;
            }


            scavNumber.setText(" " + String.valueOf(scavItems.get(rowNumbertoDisplay).get_id()));
            //scavName.setText(scavItems.get(rowNumbertoDisplay).get_name());
            //scavAddress.setText(scavItems.get(rowNumbertoDisplay).get_address());
            scavDirections.setText(scavItems.get(rowNumbertoDisplay).get_directions());
            scavImage.setImageResource(scavItems.get(rowNumbertoDisplay).get_image());

            rowNumbertoDisplay++;
        } else if (v.getId() == R.id.mapButton) {
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

            Intent i = new Intent(this, MapsActivity.class);
            i.putExtra("address", scavItems.get(rowNumbertoDisplay).get_address());
            startActivity(i);
        } else if (v.getId() == R.id.checkButton) {
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

            Location bulldogLocation = new Location("test");
            bulldogLocation.setLatitude(bulldogLatLng.latitude);
            bulldogLocation.setLongitude(bulldogLatLng.longitude);

            equals(bulldogLocation, myLocation, scavItems);
        }
    }


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
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect");
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

    public void equals(Location bulldogLocation, Location currentLocation, List<ScavItem> scavItems) {
        float radiusOfError = 100;
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

    }
}
