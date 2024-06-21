package com.example.todoapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationService extends IntentService {
    private static final String CHANNEL_ID = "TODO_APP_CHANNEL";
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "NotificationServiceCheck";

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Handling intent");

        String taskTitle = intent.getStringExtra("taskTitle");
        long executionTimeMillis = intent.getLongExtra("executionTimeMillis", -1);

        createNotificationChannel();

        // Set alarm 5 minutes before the task is due
        long alarmTime = executionTimeMillis - 5 * 60 * 1000; // 5 minutes before due date
        Log.d(TAG, "Execution time: " + executionTimeMillis + ", Alarm time: " + alarmTime);

        if (alarmTime > System.currentTimeMillis()) {
            setAlarm(alarmTime, taskTitle);
        }
    }

    private void setAlarm(long alarmTime, String taskTitle) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("taskTitle", taskTitle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Log.d(TAG, "Setting alarm for task: " + taskTitle + " at " + alarmTime);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }

    private void createNotificationChannel() {
        Log.d(TAG, "Creating notification channel");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "TODO App Channel";
            String description = "Channel for TODO App";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
