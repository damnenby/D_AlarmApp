package com.example.d_alarmapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class AlarmSoundService extends Service {

    private static final String ACTION_START = "com.example.d_alarmapp.START_ALARM_SOUND";
    private static final String ACTION_STOP = "com.example.d_alarmapp.STOP_ALARM_SOUND";

    public static void startAlarm(Context context, long alarmTimeMillis) {
        Intent intent = new Intent(context, AlarmSoundService.class);
        intent.setAction(ACTION_START);
        intent.putExtra(AlarmHelper.EXTRA_ALARM_TIME_MILLIS, alarmTimeMillis);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopAlarm(Context context) {
        Intent intent = new Intent(context, AlarmSoundService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        long alarmTimeMillis = 0L;
        if (intent != null) {
            alarmTimeMillis = intent.getLongExtra(AlarmHelper.EXTRA_ALARM_TIME_MILLIS, 0L);
        }

        startForeground(NotificationHelper.ALARM_NOTIFICATION_ID, NotificationHelper.createAlarmNotification(this, alarmTimeMillis));
        AlarmSoundPlayer.start(this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        AlarmSoundPlayer.stop();
        NotificationHelper.cancelAlarmNotification(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
