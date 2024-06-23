package com.example.todoapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

public class NotificationService extends IntentService {
    private static final String CHANNEL_ID = "TODO_APP_CHANNEL";
    private static final String TAG = "NotificationServiceCheck";

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Handling intent");


        String taskId = intent.getStringExtra("taskId");

        String taskTitle = intent.getStringExtra("taskTitle");
        String taskDescription = intent.getStringExtra("taskDescription");
        String taskCategory = intent.getStringExtra("taskCategory");
        long executionTimeMillis = intent.getLongExtra("executionTimeMillis", -1);

        String attachmentPath = intent.getStringExtra("attachmentPath");


        createNotificationChannel();

        // Cancel any existing alarm for the task
        cancelAlarm(taskId);


        // Get notification time preference
        SharedPreferences sharedPreferences = getSharedPreferences("SettingsPreferences", MODE_PRIVATE);
        int notificationTimePosition = sharedPreferences.getInt("notificationTime", 1);
        long notificationTimeMillis = getNotificationTimeInMillis(notificationTimePosition);

        // Set alarm before the task is due
        long alarmTime = executionTimeMillis - notificationTimeMillis;
        Log.d(TAG, "Execution time: " + executionTimeMillis + ", Alarm time: " + alarmTime);


        if (alarmTime > System.currentTimeMillis()) {
            setAlarm(alarmTime, taskId, taskTitle, taskDescription, taskCategory, executionTimeMillis, attachmentPath, notificationTimeMillis);
        }
    }

    private long getNotificationTimeInMillis(int position) {
        switch (position) {
            case 0: return 1 * 60 * 1000; // 1 minute
            case 1: return 5 * 60 * 1000; // 5 minutes
            case 2: return 10 * 60 * 1000; // 10 minutes
            case 3: return 30 * 60 * 1000; // 30 minutes
            case 4: return 60 * 60 * 1000; // 60 minutes
            default: return 5 * 60 * 1000; // default to 5 minutes
        }
    }

    private void cancelAlarm(String taskId) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, taskId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void setAlarm(long alarmTime, String taskId, String taskTitle, String taskDescription, String taskCategory, long executionDate, String attachmentPath, long notificationTimeMillis) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("taskId", taskId);
        intent.putExtra("taskTitle", taskTitle);
        intent.putExtra("taskDescription", taskDescription);
        intent.putExtra("taskCategory", taskCategory);
        intent.putExtra("executionDate", executionDate);
        intent.putExtra("attachmentPath", attachmentPath);
        intent.putExtra("notificationTimeMillis", notificationTimeMillis);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, taskId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Log.d(TAG, "Setting alarm for task: " + taskTitle + " at " + alarmTime);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
            }
        }
    }


    private void createNotificationChannel() {
        Log.d(TAG, "Creating notification channel");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TODO App Channel";
            String description = "Channel for TODO App";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
