package com.example.myapplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MarkAbsentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean isMarked = dbHelper.isAttendanceMarked(todayDate);
        db.close();

        if (!isMarked) {
            dbHelper.markAttendanceForAllAbsent(todayDate);
            Log.d("ATTENDANCE", "Marked absent for users who did not mark attendance.");
        }
    }
}
