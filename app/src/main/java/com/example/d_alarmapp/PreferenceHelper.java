package com.example.d_alarmapp;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    private static final String PREFS_NAME = "alarm_preferences";
    private static final String KEY_SNOOZE_MINUTES = "snooze_minutes";
    public static final int DEFAULT_SNOOZE_MINUTES = 5;

    public static int getSnoozeMinutes(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_SNOOZE_MINUTES, DEFAULT_SNOOZE_MINUTES);
    }

    public static void saveSnoozeMinutes(Context context, int minutes) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(KEY_SNOOZE_MINUTES, minutes).apply();
    }
}
