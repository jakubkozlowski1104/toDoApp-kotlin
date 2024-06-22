package com.example.todoapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
            Cursor cursor = dbHelper.readAllData("");
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String taskId = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String taskTitle = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TITLE));
                @SuppressLint("Range") String taskDescription = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DESCRIPTION));
                @SuppressLint("Range") String taskCategory = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_CATEGORY));
                @SuppressLint("Range") long executionDate = cursor.getLong(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DUE_AT));
                @SuppressLint("Range") String attachmentPath = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ATTACHMENT_PATH));

                // Reschedule the alarm
                Intent notificationIntent = new Intent(context, NotificationService.class);
                notificationIntent.putExtra("taskId", taskId);
                notificationIntent.putExtra("taskTitle", taskTitle);
                notificationIntent.putExtra("taskDescription", taskDescription);
                notificationIntent.putExtra("taskCategory", taskCategory);
                notificationIntent.putExtra("executionTimeMillis", executionDate);
                notificationIntent.putExtra("attachmentPath", attachmentPath);
                context.startService(notificationIntent);
            }
            cursor.close();
        }
    }
}
