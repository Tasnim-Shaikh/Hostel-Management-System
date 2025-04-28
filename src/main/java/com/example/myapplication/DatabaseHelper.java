package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDB";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";

    private static final String COLUMN_PHOTO = "photo"; // Store image as BLOB

    private static final String TABLE_ATTENDANCE = "attendance";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_ATTENDANCE = "attendance"; // 'P' or 'A'

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_PHONE +" TEXT,"
                + COLUMN_PHOTO + " BLOB)";


        String CREATE_ATTENDANCE_TABLE = "CREATE TABLE " + TABLE_ATTENDANCE + " ("
                + COLUMN_NAME + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_ATTENDANCE + " TEXT, "
                + "PRIMARY KEY (" + COLUMN_NAME + ", " + COLUMN_DATE + "))"; // Prevent duplicates

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ATTENDANCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        onCreate(db);
    }

    // Insert User Data
    // Change the method signature to accept byte[] instead of Bitmap
    public boolean insertUser(String name, String password, String phone,byte[] photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHONE,phone);
        values.put(COLUMN_PHOTO, photo);  // No need to convert

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        if (result == -1) {
            Log.e("DB_INSERT", "Failed to insert user.");
            return false;
        } else {
            Log.d("DB_INSERT", "User registered: " + name);
            return true;
        }
    }


    // Convert Bitmap to Byte Array
    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // Get User Photo as Bitmap
    public Bitmap getUserPhoto(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PHOTO + " FROM " + TABLE_USERS + " WHERE name=?", new String[]{name});

        if (cursor.moveToFirst()) {
            byte[] imageBytes = cursor.getBlob(0);
            cursor.close();
            if (imageBytes != null && imageBytes.length > 0) {
                return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            }
        }
        cursor.close();
        return null;
    }

    // Mark Attendance (Prevent Duplicate Entries)
    public boolean markAttendance(String name, String date, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if attendance already exists
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + COLUMN_NAME + "=? AND " + COLUMN_DATE + "=?", new String[]{name, date});

        if (cursor.getCount() > 0) {
            cursor.close();
            Log.d("ATTENDANCE", "Attendance already marked for " + name + " on " + date);
            return false;
        }

        // If no duplicate entry, mark attendance
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_ATTENDANCE, status);
        db.insert(TABLE_ATTENDANCE, null, values);
        db.close();

        Log.d("ATTENDANCE", "Attendance marked: " + status + " for " + name + " on " + date);
        return true;
    }

    // Debug: Print all user names
    public void debugCheckUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM " + TABLE_USERS, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Log.d("DB_CHECK", "User Registered: " + cursor.getString(0)); // Logs username
            }
        } else {
            Log.e("DB_CHECK", "No users found!");
        }
        cursor.close();
    }

    // Check if user exists
    public boolean isUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM " + TABLE_USERS + " WHERE name=?", new String[]{username});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    // Check if attendance exists for a given date
    public boolean isAttendanceMarked(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + COLUMN_DATE + "=?", new String[]{date});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Mark all users absent who haven't recorded attendance
    public void markAttendanceForAllAbsent(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_USERS, null);

        while (cursor.moveToNext()) {
            String userName = cursor.getString(0);

            // Check if this user has already marked attendance
            Cursor checkCursor = db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + COLUMN_NAME + "=? AND " + COLUMN_DATE + "=?", new String[]{userName, date});

            if (checkCursor.getCount() == 0) {
                // Insert "A" (Absent) if no entry exists
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME, userName);
                values.put(COLUMN_DATE, date);
                values.put(COLUMN_ATTENDANCE, "A");
                db.insert(TABLE_ATTENDANCE, null, values);
            }
            checkCursor.close();
        }

        cursor.close();
        db.close();
    }

}
