package com.login.helper;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Created by TecKNork on 3/22/2015.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "shopin";

    // Login table name
    private static final String TABLE_LOGIN = "login";
    private static final String TABLE_SHOP = "shop";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_API = "uid";
    private static final String KEY_CREATED_AT = "created_at";

//    private static final String KEY_ID = "id";
    private static final String KEY_SHOPNAME = "shopname";
    private static final String KEY_WIFINAME = "wifiname";
    private static final String KEY_WIFIPASS = "password";
    private static final String KEY_CONTACTINFO = "contactinfo";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_SHOPID = "shopid";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_CITY = "city";





    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_API + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_SHOP_TABLE = "CREATE TABLE " + TABLE_SHOP + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SHOPNAME + " TEXT,"
                + KEY_WIFINAME + " TEXT," + KEY_WIFIPASS + " TEXT,"
                + KEY_CONTACTINFO + " TEXT," + KEY_IMAGE + " TEXT,"
                + KEY_CITY + " TEXT," + KEY_LON + " TEXT,"
                + KEY_LAT+ " TEXT," + KEY_SHOPID+ " TEXT" + ")";
        db.execSQL(CREATE_SHOP_TABLE);


        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOP);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_API, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }


    public void addShop(String shopname, String wifiname, String wifipass,String contact,String image,String lat,String lon,String shopid,String city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SHOPNAME, shopname); // Name
        values.put(KEY_WIFINAME, wifiname); // Email
        values.put(KEY_WIFIPASS, wifipass); // Email
        values.put(KEY_CONTACTINFO, contact); // Email
        values.put(KEY_IMAGE, image); // Email
        values.put(KEY_SHOPID, shopid); // Created At
        values.put(KEY_LAT, lat); // Created At
        values.put(KEY_LON, lon); // Created At
        values.put(KEY_CITY, city); // Created At

        // Inserting Row
        long id = db.insert(TABLE_SHOP, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New Shop inserted into sqlite: " + id);
    }


    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public HashMap<String, String> getShopDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_SHOP;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("shopname", cursor.getString(1));
            user.put("wifiname", cursor.getString(2));
            user.put("wifipass", cursor.getString(3));
            user.put("contactinfo", cursor.getString(4));
            user.put("image", cursor.getString(5));
            user.put("city", cursor.getString(6));
            user.put("lon", cursor.getString(7));
            user.put("lat", cursor.getString(8));
            user.put("shopid", cursor.getString(9));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }


    /**
     * Getting user login status return true if rows are there in table
     */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }


    public void deleteShop() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_SHOP, null, null);
        db.close();
        Log.d(TAG, "Deleted all Shops info from sqlite");
    }



}



