package com.example.d_alarmapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!AlarmHelper.ACTION_ALARM.equals(intent.getAction())) {
            return;
        }

        PowerManager.WakeLock wakeLock = createWakeLock(context);
        try {
            if (wakeLock != null) {
                wakeLock.acquire(10000);
            }

            long alarmTimeMillis = intent.getLongExtra(AlarmHelper.EXTRA_ALARM_TIME_MILLIS, 0L);
            AlarmSoundService.startAlarm(context, alarmTimeMillis);
            NotificationHelper.showAlarmNotification(context, alarmTimeMillis);
            Intent alarmIntent = new Intent(context, AlarmActivity.class);
            alarmIntent.putExtra(AlarmHelper.EXTRA_ALARM_TIME_MILLIS, alarmTimeMillis);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(alarmIntent);
        } finally {
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
    }

    private PowerManager.WakeLock createWakeLock(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager == null) {
            return null;
        }
        return powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "D_AlarmApp:AlarmReceiver");
    }
}
