package com.example.d_alarmapp;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

public class AlarmSoundPlayer {

    private static MediaPlayer mediaPlayer;

    public static synchronized void start(Context context) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return;
        }

        stop();

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        if (alarmUri == null) {
            return;
        }

        MediaPlayer player = new MediaPlayer();
        try {
            player.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            player.setDataSource(context.getApplicationContext(), alarmUri);
            player.setLooping(true);
            player.prepare();
            player.start();
            mediaPlayer = player;
        } catch (Exception e) {
            player.release();
            mediaPlayer = null;
        }
    }

    public static synchronized void stop() {
        if (mediaPlayer == null) {
            return;
        }

        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        } catch (IllegalStateException e) {
            mediaPlayer.reset();
        } finally {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
