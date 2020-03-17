package com.example.carwash.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DataManager  extends SQLiteOpenHelper {

    private static final String NAME = "carWash.db";
    private static final int VERSION = 1;

    private String TABLE_REQUEST = "request";
    private String COLUMN_ID = "id";
    private String COLUMN_EMAIL = "email";
    private String COLUMN_LOGO = "car_logo";
    private String COLUMN_CAR = "car_name";
    private String COLUMN_ADDRESS = "address";
    private String COLUMN_LATITUDE = "latitude";
    private String COLUMN_LONGITUDE = "longitude";
    private String COLUMN_CREATED = "created";

    public DataManager(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_REQUEST + "( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_EMAIL + " VARCHAR(100) NOT NULL, "
                + COLUMN_CAR + " VARCHAR(100) NOT NULL, "
                + COLUMN_LOGO + " VARCHAR(100), "
                + COLUMN_ADDRESS + " TEXT, "
                + COLUMN_LATITUDE + " VARCHAR(100), "
                + COLUMN_LONGITUDE + " VARCHAR(100),  "
                + COLUMN_CREATED + " VARCHAR(100)"
                + " );";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addRequest(String email, String car, String image, String address, String latitude, String longitude, String created) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_CAR, car);
        values.put(COLUMN_LOGO, image);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_CREATED, created);

        database.insert(TABLE_REQUEST, null, values);
        database.close();
        Log.i("INSERT", "Record Added Successfully");
    }

    public ArrayList<ServiceRequest> getRequests() {
        SQLiteDatabase database = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_REQUEST;
        Cursor cursor = database.rawQuery(sql, null);

        ArrayList<ServiceRequest> requests = new ArrayList<>();

        cursor.moveToFirst();

        while (cursor.moveToFirst()) {

            ServiceRequest request = new ServiceRequest();
            request.setEmail(cursor.getString(1));
            request.setCarName(cursor.getString(2));
            request.setCarThumbnail(cursor.getInt(3));
            request.setAddress(cursor.getString(4));
            request.setLatitude(Double.parseDouble(cursor.getString(5)));
            request.setLongitude(Double.parseDouble(cursor.getString(6)));
            request.setDate(cursor.getString(7));

            requests.add(request);
        }

        cursor.close();
        database.close();

        return requests;
    }

    public void temp() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUEST);

        onCreate(db);
    }

    public ServiceRequest lastRequest() {
        SQLiteDatabase database = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_REQUEST;
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        ServiceRequest request = null;

        while (cursor.moveToNext()) {

            request = new ServiceRequest();

            request.setEmail(cursor.getString(1));
            request.setCarName(cursor.getString(2));
            request.setCarThumbnail(cursor.getInt(3));
            request.setAddress(cursor.getString(4));
            request.setLatitude(Double.parseDouble(cursor.getString(5)));
            request.setLongitude(Double.parseDouble(cursor.getString(6)));
            request.setDate(cursor.getString(7));
        }

        cursor.close();
        database.close();

        return request;
    }
}
