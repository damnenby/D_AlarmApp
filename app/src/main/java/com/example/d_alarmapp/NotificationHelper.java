package com.example.d_alarmapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    private static final String ALARM_CHANNEL_ID = "alarm_channel";
    private static final String CHANNEL_ID = "snooze_channel";
    public static final int ALARM_NOTIFICATION_ID = 2000;
    private static final int NOTIFICATION_ID = 2001;
    private static final int REQUEST_CODE_ALARM_ACTIVITY = 2003;
    private static final int REQUEST_CODE_MAIN = 2002;

    public static void showAlarmNotification(Context context, long alarmTimeMillis) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationManagerCompat.from(context).notify(ALARM_NOTIFICATION_ID, createAlarmNotification(context, alarmTimeMillis));
    }

    public static Notification createAlarmNotification(Context context, long alarmTimeMillis) {
        createAlarmNotificationChannel(context);
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.putExtra(AlarmHelper.EXTRA_ALARM_TIME_MILLIS, alarmTimeMillis);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                REQUEST_CODE_ALARM_ACTIVITY,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.alarm_notification_title))
                .setContentText(context.getString(R.string.alarm_notification_text, AlarmHelper.formatTime(alarmTimeMillis)))
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        return builder.build();
    }

    public static void showSnoozeNotification(Context context, long alarmTimeMillis) {
        createSnoozeNotificationChannel(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                REQUEST_CODE_MAIN,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.snooze_notification_title))
                .setContentText(context.getString(R.string.snooze_notification_text, AlarmHelper.formatTime(alarmTimeMillis)))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
    }

    public static void cancelSnoozeNotification(Context context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
    }

    public static void cancelAlarmNotification(Context context) {
        NotificationManagerCompat.from(context).cancel(ALARM_NOTIFICATION_ID);
    }

    private static void createAlarmNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                ALARM_CHANNEL_ID,
                context.getString(R.string.alarm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static void createSnoozeNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.snooze_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}
