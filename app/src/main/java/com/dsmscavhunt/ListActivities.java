package com.dsmscavhunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class ListActivities extends AppCompatActivity {


    //Defining android ListView
    ListView mListView;

    //Elements that will be displayed in android ListView
    String[] Hunt = new String[]{"Take A Selfie By the Hamburger Bulldog", "Take A Picture By Meredith",
            "Get A Picture of your food at Drake Diner", "Get a selfie at the Sculpture Park"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mListView = (ListView) findViewById(R.id.list);

        //Declaring Array adapter
        ArrayAdapter<String> huntAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, Hunt);

        //Setting the android ListView's adapter to the newly created adapter
        mListView.setAdapter(huntAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //The position where the list item is clicked is obtained from the
                //the parameter position of the android listview
                int itemPosition = position;

                //Get the String value of the item where the user clicked
                String itemValue = (String) mListView.getItemAtPosition(position);

                //In order to start displaying new activity we need an intent
                Intent intent = new Intent(getApplicationContext(),ListActivities.class);


                //Here we will pass the previously created intent as parameter
                startActivity(intent);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_activities, menu);
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
}
