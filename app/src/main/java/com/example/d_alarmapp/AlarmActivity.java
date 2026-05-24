package com.example.d_alarmapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.alarmRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AlarmSoundPlayer.start(this);
        findViewById(R.id.snoozeButton).setOnClickListener(v -> snoozeAlarm());
    }

    private void snoozeAlarm() {
        AlarmSoundPlayer.stop();
        int snoozeMinutes = PreferenceHelper.getSnoozeMinutes(this);
        long alarmTimeMillis = AlarmHelper.scheduleSnooze(this, snoozeMinutes);
        Toast.makeText(this, getString(R.string.alarm_snoozed, AlarmHelper.formatTime(alarmTimeMillis)), Toast.LENGTH_SHORT).show();
        finish();
    }
}
