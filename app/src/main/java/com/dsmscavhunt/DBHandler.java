package com.dsmscavhunt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 10/10/15.
 *
 * Java Class which extends SQLite class to create, connect to, insert into, and pull from database
 */
public class DBHandler extends SQLiteOpenHelper {

    // All member variables are static
    // Database Version
    private static final int DATABASE_VERSION = 9;

    // Database Name
    private static final String DATABASE_NAME = "scavHunt";

    // Table Name
    private static final String TABLE_SCAV_ITEMS = "drakeOrientation";

    // Column Names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DIRECTIONS = "directions";
    private static final String KEY_IMAGE = "image";

    // Logging TAG
    private static final String TAG = "DBHandler";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create a table of scavenger hunt items
        String CREATE_SCAV_ITEMS_TABLE = "CREATE TABLE " + TABLE_SCAV_ITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_NAME + " TEXT UNIQUE,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_DIRECTIONS + " TEXT UNIQUE,"
                + KEY_IMAGE + " INTEGER UNIQUE" + ")";

        // Execute SQL
        try {
            db.execSQL(CREATE_SCAV_ITEMS_TABLE);
            Log.v(TAG, "Table Created!");
        } catch (Exception e) {
            // Log errors if table cannot be created
            Log.e(TAG, "Unable to create Scav Item table", e);
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if it exists
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCAV_ITEMS);
            Log.v(TAG, "Table dropped!");
        } catch (Exception e) {
            Log.e(TAG, "Unable to drop Scav Item table", e);
        }

        // Re-create table
        onCreate(db);

    }

    // Add new ScavItem
    public void addScavItem(ScavItem scavItem) {
        // get a handle on the current database
        SQLiteDatabase db = this.getWritableDatabase();

        // ContentValues object to store ScavItem info
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, scavItem.get_name()); // ScavItem name
        values.put(KEY_ADDRESS, scavItem.get_address()); // ScavItem address
        values.put(KEY_DIRECTIONS, scavItem.get_directions()); // ScavItem directions
        values.put(KEY_IMAGE, scavItem.get_image()); // ScavItem image


        // Insert row to database
        try {
            db.insert(TABLE_SCAV_ITEMS, null, values);
            Log.v(TAG, "Row inserted!");
        } catch (Exception e) {
            Log.e(TAG, "Unable to insert row", e);
        }

        db.close();
    }

    // Return single ScavItem (one row from table)
    public ScavItem getScavItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCAV_ITEMS,
                new String[]{KEY_ID, KEY_NAME, KEY_ADDRESS, KEY_DIRECTIONS, KEY_IMAGE},
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor == null) {
            Log.w(TAG, "Cursor failed to return query");
        }

        Log.v(TAG, "SELECT success!");
        cursor.moveToFirst();
        ScavItem scavItem = new ScavItem(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                Integer.parseInt(cursor.getString(4)));
        cursor.close();
        db.close();
        return scavItem;



    }

    // Return all ScavItems in table
    public List<ScavItem> getAllScavItems() {
        List<ScavItem> scavItemList = new ArrayList<ScavItem>();

        // Select entire table
        String selectQuery = "SELECT * FROM " + TABLE_SCAV_ITEMS;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows in cursor object, add them to list
        if (cursor.moveToFirst()) {
            do {
                ScavItem scavItem = new ScavItem();
                scavItem.set_id(Integer.parseInt(cursor.getString(0)));
                scavItem.set_name(cursor.getString(1));
                scavItem.set_address(cursor.getString(2));
                scavItem.set_directions(cursor.getString(3));
                scavItem.set_image(Integer.parseInt(cursor.getString(4)));
                // Add ScavItem to list
                scavItemList.add(scavItem);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scavItemList;
    }

    // Return the number of ScavItems (rows) in table
    public int getScavItemCount() {
        String countQuery = "SELECT * FROM " + TABLE_SCAV_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        // Return count

        return cursor.getCount();

    }

    // Update single ScavItem
    public void updateScavItem(ScavItem scavItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, db.toString());

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, scavItem.get_name()); // ScavItem name
        values.put(KEY_ADDRESS, scavItem.get_address()); // ScavItem address
        values.put(KEY_DIRECTIONS, scavItem.get_directions()); // ScavItem directions
        values.put(KEY_IMAGE, scavItem.get_image()); // ScavItem image

        // Update the corresponding row
        try {
            int i = db.update(TABLE_SCAV_ITEMS,
                    values,
                    KEY_ID + "=?",
                    new String[] { String.valueOf(scavItem.get_id())});
            if (i <= 0) {
                Log.e(TAG, "Update failed in a weird way");
            }

        } catch (Exception e) {
            Log.e(TAG, "Update failed", e);
        }
        db.close();



    }

    // Delete single ScavItem
    public void deleteScavItem (ScavItem scavItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_SCAV_ITEMS,
                    KEY_ID + " = ?",
                    new String[] { String.valueOf(scavItem.get_id())});
        } catch (Exception e) {
            Log.e(TAG, "Delete failed", e);
        }
        db.close();



    }




}
