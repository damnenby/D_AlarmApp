package com.example.d_alarmapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AlarmHelper {

    public static final String ACTION_ALARM = "com.example.d_alarmapp.ACTION_ALARM";
    public static final String EXTRA_ALARM_TIME_MILLIS = "alarm_time_millis";

    private static final String PREFS_NAME = "alarm_state";
    private static final String KEY_ACTIVE_ALARM_TIME_MILLIS = "active_alarm_time_millis";
    private static final int REQUEST_CODE_ALARM = 1001;

    public static long scheduleAlarm(Context context, int hour, int minute) {
        long alarmTimeMillis = getNextAlarmTimeMillis(hour, minute);
        scheduleAlarmAt(context, alarmTimeMillis);
        saveActiveAlarmTime(context, alarmTimeMillis);
        return alarmTimeMillis;
    }

    public static long scheduleSnooze(Context context, int snoozeMinutes) {
        long alarmTimeMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(snoozeMinutes);
        scheduleAlarmAt(context, alarmTimeMillis);
        saveActiveAlarmTime(context, alarmTimeMillis);
        return alarmTimeMillis;
    }

    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(createAlarmPendingIntent(context, getActiveAlarmTime(context)));
        }
        clearActiveAlarmTime(context);
    }

    public static long getActiveAlarmTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getLong(KEY_ACTIVE_ALARM_TIME_MILLIS, 0L);
    }

    public static void clearActiveAlarmTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(KEY_ACTIVE_ALARM_TIME_MILLIS).apply();
    }

    public static String formatTime(long timeMillis) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(new Date(timeMillis));
    }

    private static void scheduleAlarmAt(Context context, long alarmTimeMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        PendingIntent pendingIntent = createAlarmPendingIntent(context, alarmTimeMillis);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
        }
    }

    private static PendingIntent createAlarmPendingIntent(Context context, long alarmTimeMillis) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(ACTION_ALARM);
        intent.putExtra(EXTRA_ALARM_TIME_MILLIS, alarmTimeMillis);
        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_ALARM,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static long getNextAlarmTimeMillis(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return calendar.getTimeInMillis();
    }

    private static void saveActiveAlarmTime(Context context, long alarmTimeMillis) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().putLong(KEY_ACTIVE_ALARM_TIME_MILLIS, alarmTimeMillis).apply();
    }
}
