package com.dsmscavhunt;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView scavNumber = (TextView)findViewById(R.id.scavNumber);
        TextView scavName = (TextView)findViewById(R.id.scavName);
        TextView scavAddress = (TextView)findViewById(R.id.scavAddress);
        TextView scavDirections = (TextView)findViewById(R.id.scavDirections);
        ImageView scavImage = (ImageView)findViewById(R.id.scavImage);

        DBHandler dbh = new DBHandler(this);


        dbh.addScavItem(new ScavItem("Meredith", "5678 Forest Avenue", "up", R.drawable.bulldog1));
        dbh.addScavItem(new ScavItem("Olmstead", "1234 University Boulevard", "up", R.drawable.bulldog2));
        dbh.addScavItem(new ScavItem("Cowles", "3734 28th Street", "up", R.drawable.bulldog3));
        dbh.addScavItem(new ScavItem("Howard", "504 Park Avenue", "up", R.drawable.bulldog4));


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

            if (rowNumbertoDisplay >= 4) {
                rowNumbertoDisplay = 0;
            }


            scavNumber.setText(" " + String.valueOf(scavItems.get(rowNumbertoDisplay).get_id()));
            scavName.setText(scavItems.get(rowNumbertoDisplay).get_name());
            scavAddress.setText(scavItems.get(rowNumbertoDisplay).get_address());
            scavDirections.setText(scavItems.get(rowNumbertoDisplay).get_directions());
            scavImage.setImageResource(scavItems.get(rowNumbertoDisplay).get_image());

            rowNumbertoDisplay++;
        }
    }
}
