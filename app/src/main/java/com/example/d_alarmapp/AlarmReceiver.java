package com.example.d_alarmapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!AlarmHelper.ACTION_ALARM.equals(intent.getAction())) {
            return;
        }

        long alarmTimeMillis = intent.getLongExtra(AlarmHelper.EXTRA_ALARM_TIME_MILLIS, 0L);
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra(AlarmHelper.EXTRA_ALARM_TIME_MILLIS, alarmTimeMillis);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(alarmIntent);
    }
}
