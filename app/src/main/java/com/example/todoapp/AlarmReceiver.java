package com.example.todoapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "TODO_APP_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        String taskId = intent.getStringExtra("taskId");
        String taskTitle = intent.getStringExtra("taskTitle");
        String taskDescription = intent.getStringExtra("taskDescription");
        String taskCategory = intent.getStringExtra("taskCategory");
        long executionDate = intent.getLongExtra("executionDate", -1);
        String attachmentPath = intent.getStringExtra("attachmentPath");
        long notificationTimeMillis = intent.getLongExtra("notificationTimeMillis", 5 * 60 * 1000);

        createNotificationChannel(context);

        Intent updateIntent = new Intent(context, UpdateActivity.class);
        updateIntent.putExtra("id", taskId);
        updateIntent.putExtra("title", taskTitle);
        updateIntent.putExtra("description", taskDescription);
        updateIntent.putExtra("category", taskCategory);
        updateIntent.putExtra("execution_date", executionDate);
        updateIntent.putExtra("attachment_path", attachmentPath);
        updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(),
                updateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String notificationText = getNotificationText(notificationTimeMillis, taskTitle);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Przypomnienie")
                .setContentText(notificationText)
                .setSmallIcon(R.drawable.ic_add)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    private String getNotificationText(long notificationTimeMillis, String taskTitle) {
        int minutesBefore = (int) (notificationTimeMillis / 60000);
        return minutesBefore + " minut do końca zadania: \"" + taskTitle + "\"";
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TODO App Channel";
            String description = "Channel for TODO App";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
