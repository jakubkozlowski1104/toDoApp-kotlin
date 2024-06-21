package com.example.todoapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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

        // Ustawienie alarmu 1 minutę przed zakończeniem zadania
        long alarmTime = executionTimeMillis - 1 * 60 * 1000;
        Log.d(TAG, "Execution time: " + executionTimeMillis + ", Alarm time: " + alarmTime);
        if (alarmTime > System.currentTimeMillis()) {
            setAlarm(alarmTime, taskTitle);
        }

        Log.d(TAG, "Creating notification");

        if (executionTimeMillis < System.currentTimeMillis()) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Powiadomienie")
                    .setContentText("Czas na wykonanie zadania: \"" + taskTitle + "\" MINĄŁ!")
                    .setSmallIcon(R.drawable.ic_add)
                    .build();

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(NOTIFICATION_ID, notification);
        } else {
            long timeDiffMillis = executionTimeMillis - System.currentTimeMillis();
            long days = timeDiffMillis / (1000 * 60 * 60 * 24);
            long hours = (timeDiffMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (timeDiffMillis % (1000 * 60 * 60)) / (1000 * 60);

            String timeLeft = "";
            Log.d(TAG, "Dodano dni do timeLeft: " + days);
            if (days > 0) {
                timeLeft += days + " dni, ";
            }
            timeLeft += hours + " godzin, " + minutes + " minut";

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Powiadomienie")
                    .setContentText("Do zakończenia zadania \"" + taskTitle + "\" pozostało: " + timeLeft)
                    .setSmallIcon(R.drawable.ic_add)
                    .build();

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private void setAlarm(long alarmTime, String taskTitle) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("taskTitle", taskTitle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Setting alarm for task: " + taskTitle + " at " + alarmTime);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
                } else {
                    Log.e(TAG, "Brak uprawnień do ustawienia dokładnych alarmów");
                }
            } else {
                Log.d(TAG, "Setting alarm for task: " + taskTitle + " at " + alarmTime);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
            }
        }
    }

    private void createNotificationChannel() {
        Log.d(TAG, "Creating notification channel");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
