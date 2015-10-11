package com.dsmscavhunt;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;


/*
Katie Roth
Created a list with check marks on the side
Used http://www.androidinterview.com/android-custom-listview-with-checkbox-example/
as a reference
*/


public class List extends ListActivity {

    //List of scavengerHunt activites
    String[] huntList = {
            "Take a selfie with Burger Bulldog",
            "Take a pic of a milkshake at the Drake Dinner",
            "Find and sculpture park and take selfie there",
            "Take a picture of a textbook at the bookstore"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Display mode of the ListView
        //Allow Multiple Items to be checked
        ListView listview= getListView();
        listview.setChoiceMode(listview.CHOICE_MODE_MULTIPLE);


        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked,huntList));
    }

    //Inform the user when they have completed an activity
    //with toast
    public void onListItemClick(ListView parent, View v,int position,long id){
        CheckedTextView item = (CheckedTextView) v;
        Toast.makeText(this, "You Completed :" + huntList[position], Toast.LENGTH_SHORT).show();
    }
}