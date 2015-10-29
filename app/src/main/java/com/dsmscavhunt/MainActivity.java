package com.dsmscavhunt;

import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
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

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button button;
    ImageView imageView;
    static final int CAM_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView scavNumber = (TextView)findViewById(R.id.scavNumber);
        TextView scavName = (TextView)findViewById(R.id.scavName);
        TextView scavAddress = (TextView)findViewById(R.id.scavAddress);
        TextView scavDirections = (TextView)findViewById(R.id.scavDirections);
        ImageView scavImage = (ImageView)findViewById(R.id.scavImage);

        DBHandler dbh = new DBHandler(this);


        dbh.addScavItem(new ScavItem("Aliber", "2847 University Avenue, Des Moines, IA 50311", "left", R.drawable.aliberbulldog));
        dbh.addScavItem(new ScavItem("Meredith", "2805 University Avenue, Des Moines, IA 50311", "right", R.drawable.meredith));
        dbh.addScavItem(new ScavItem("Olmsted (outside)", "2875 University Avenue, Des Moines, IA 50311", "diagonal", R.drawable.olmsted));
        dbh.addScavItem(new ScavItem("Cheeseburger", "1315 30th Street, Des Moines, IA 50311", "straight", R.drawable.cheeseburger));
        dbh.addScavItem(new ScavItem("West Village One", "1325 31st Street, Des Moines, IA 50311", "crooked", R.drawable.westvillage1));
        dbh.addScavItem(new ScavItem("West Village Two", "1325 31st Street, Des Moines, IA 50311", "sharp left", R.drawable.westvillage2));
        dbh.addScavItem(new ScavItem("Bookstore", "3003 Forest Ave, Des Moines, IA 50311", "sharp right", R.drawable.bulldog1));
        dbh.addScavItem(new ScavItem("Lab Coat (Cline)", "2802 Forest Avenue, Des Moines, IA 50311", "merge right", R.drawable.labcoat));
        dbh.addScavItem(new ScavItem("President", "2507 University Avenue, Des Moines, IA 50311", "merge left", R.drawable.president));
        dbh.addScavItem(new ScavItem("Biker", "2507 University Avenue, IA 50311", "go in circles", R.drawable.biker));


        List<ScavItem> scavItems = dbh.getAllScavItems();
        scavNumber.setText(" " + String.valueOf(scavItems.get(0).get_id()));
        scavName.setText(scavItems.get(0).get_name());
        scavAddress.setText(scavItems.get(0).get_address());
        scavDirections.setText(scavItems.get(0).get_directions());
        scavImage.setImageResource(scavItems.get(0).get_image());

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
                File camFile = getFile();
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(camFile));
                startActivityForResult(camera_intent,CAM_REQUEST);
            }
        });
    }

    public File getFile()
    {
        String path = "";
        String lat = "";
        String lon = "";

        File folder = new File("sdcard/camera_app");
        if (!folder.exists())
        {
            folder.mkdir();
        }
        File image_file = new File(folder, "cam_image1.jpg");
        for(int i=1; image_file.exists(); i++){
            image_file = new File(folder, "cam_image" + i + ".jpg");
            path = "sdcard/camera_app/cam_image" + i + ".jpg";
        }

        try {
            ExifInterface exifInterface = new ExifInterface("sdcard/camera_app/cam_image1.jpg");
            lat = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            TextView latText = (TextView) findViewById(R.id.lat);
            latText.setText(lat);
            lon = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            }
         catch(IOException ie) {
             ie.printStackTrace();
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
            TextView scavName = (TextView) findViewById(R.id.scavName);
            TextView scavAddress = (TextView) findViewById(R.id.scavAddress);
            TextView scavDirections = (TextView) findViewById(R.id.scavDirections);
            ImageView scavImage = (ImageView) findViewById(R.id.scavImage);

            int rowNumbertoDisplay = Integer.parseInt(((String) scavNumber.getText()).trim());

            if (rowNumbertoDisplay >= 9) {
                rowNumbertoDisplay = 0;
            }


            scavNumber.setText(" " + String.valueOf(scavItems.get(rowNumbertoDisplay).get_id()));
            scavName.setText(scavItems.get(rowNumbertoDisplay).get_name());
            scavAddress.setText(scavItems.get(rowNumbertoDisplay).get_address());
            scavDirections.setText(scavItems.get(rowNumbertoDisplay).get_directions());
            scavImage.setImageResource(scavItems.get(rowNumbertoDisplay).get_image());

            rowNumbertoDisplay++;
        }
        else if (v.getId() == R.id.mapButton) {
            DBHandler dbh = new DBHandler(this);
            int numRows = dbh.getScavItemCount();
            List<ScavItem> scavItems = dbh.getAllScavItems();


            TextView scavNumber = (TextView) findViewById(R.id.scavNumber);

            int rowNumbertoDisplay = Integer.parseInt(((String) scavNumber.getText()).trim());

            Intent i = new Intent(this, MapsActivity.class);
            i.putExtra("address", scavItems.get(rowNumbertoDisplay).get_address());
            startActivity(i);
        }
    }
}
