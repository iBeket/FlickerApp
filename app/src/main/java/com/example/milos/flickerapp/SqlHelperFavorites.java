package com.example.milos.flickerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milos on 24-Aug-17.
 */

public class SqlHelperFavorites extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "filckerAppFav";
    private static final String TABLE_CONTACTS = "favImagesFav";

    private static final String KEY_IMAGE = "image";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_DATE = "date";
    private static final String KEY_TITLE = "title";
    private static final String LOCAL_PATH = "localPath";

    public SqlHelperFavorites(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_IMAGE + " TEXT, "
                + KEY_AUTHOR + " TEXT, " + KEY_TAGS + " TEXT, " + KEY_DATE + " TEXT, " + KEY_TITLE + " TEXT, " + LOCAL_PATH + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addContact(FlickrModel flickrModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE, flickrModel.getMedia());
        values.put(KEY_AUTHOR, flickrModel.getAuthor());
        values.put(KEY_TAGS, flickrModel.getTags());
        values.put(KEY_DATE, flickrModel.getDate_taken());
        values.put(KEY_TITLE, flickrModel.getTitle());
        values.put(LOCAL_PATH, flickrModel.getLocalPath());
        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    //Delete items from the database
    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, null, null);
        db.execSQL("delete from " + TABLE_CONTACTS);
        db.close();
    }

    //  Getting single item
    public FlickrModel getSingleItem(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_IMAGE, KEY_AUTHOR, KEY_TAGS, KEY_DATE, KEY_TITLE, LOCAL_PATH}, KEY_IMAGE + "=?",
                new String[]{String.valueOf(name)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        try {
            FlickrModel flickrModel = new FlickrModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
            // return single item
            return flickrModel;
        } catch (Exception e) {
            return null;
        }
    }

    // Getting All Contacts
    public List<FlickrModel> getAllInfo() {
        List<FlickrModel> contactList = new ArrayList<FlickrModel>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FlickrModel information = new FlickrModel();
                // contactPhone.setID(Integer.parseInt(cursor.getString(0)));
                information.setMedia(cursor.getString(0));
                information.setAuthor(cursor.getString(1));
                information.setTags(cursor.getString(2));
                information.setDate_taken(cursor.getString(3));
                information.setTitle(cursor.getString(4));
                information.setLocalPath(cursor.getString(5));

                // Adding contact to list
                contactList.add(information);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return contactList;
    }

    //checks if item is already added
    public boolean ifExists(FlickrModel flickrModel) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT " + KEY_IMAGE + " FROM " + TABLE_CONTACTS + " WHERE " + KEY_IMAGE + "= '" + flickrModel.getMedia() + "'";
        cursor = db.rawQuery(checkQuery, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    //number of rows in database
    public long getCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_CONTACTS);
    }

    //deletes particular row from database
    public void deleteFromBase(FlickrModel flickrModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CONTACTS + " WHERE " + KEY_IMAGE + "= '" + flickrModel.getMedia() + "'");
        db.close();
    }
}
