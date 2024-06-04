package com.example.todoapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationService extends IntentService {
    private static final String CHANNEL_ID = "TODO_APP_CHANNEL";
    private static final int NOTIFICATION_ID = 1;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("NotificationServiceCheck", "Handling intent");

        // Pobierz nazwę zadania i czas wykonania z intentu
        String taskTitle = intent.getStringExtra("taskTitle");
        long executionTimeMillis = intent.getLongExtra("executionTimeMillis", -1);

        // Utwórz kanał powiadomień dla nowszych wersji Androida
        createNotificationChannel();

        Log.d("NotificationServiceCheck", "Creating notification");

        // Sprawdź, czy czas wykonania zadania już minął
        if (executionTimeMillis < System.currentTimeMillis()) {
            // Jeśli tak, wyświetl powiadomienie informujące, że czas minął
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Powiadomienie")
                    .setContentText("Czas na wykonanie zadania: \"" + taskTitle + "\" MINĄŁ!")
                    .setSmallIcon(R.drawable.ic_add)
                    .build();

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(NOTIFICATION_ID, notification);
        } else {
            // W przeciwnym razie oblicz czas do zakończenia zadania
            long timeDiffMillis = executionTimeMillis - System.currentTimeMillis();
            long days = timeDiffMillis / (1000 * 60 * 60 * 24);
            long hours = (timeDiffMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (timeDiffMillis % (1000 * 60 * 60)) / (1000 * 60);

            // Utwórz tekst do wyświetlenia w powiadomieniu
            String timeLeft = "";
            Log.d("NotificationServiceCheck", "Dodano dni do timeLeft: " + days);
            if (days > 0) {
                timeLeft += days + " dni, ";

            }
            timeLeft += hours + " godzin, " + minutes + " minut";

            // Utwórz powiadomienie
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Powiadomienie")
                    .setContentText("Do zakończenia zadania \"" + taskTitle + "\" pozostało: " + timeLeft)
                    .setSmallIcon(R.drawable.ic_add)
                    .build();

            // Wyświetl powiadomienie
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }


    private void createNotificationChannel() {
        Log.d("NotificationService", "Creating notification channel");
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
