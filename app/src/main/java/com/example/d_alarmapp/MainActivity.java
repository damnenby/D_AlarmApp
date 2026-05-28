package com.example.d_alarmapp;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_NOTIFICATIONS = 3001;

    private TextView activeAlarmTextView;
    private TimePicker alarmTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        activeAlarmTextView = findViewById(R.id.activeAlarmTextView);
        alarmTimePicker = findViewById(R.id.alarmTimePicker);
        alarmTimePicker.setIs24HourView(true);

        findViewById(R.id.setAlarmButton).setOnClickListener(v -> setAlarm());
        findViewById(R.id.cancelAlarmButton).setOnClickListener(v -> cancelAlarm());
        findViewById(R.id.settingsButton).setOnClickListener(v -> openSettings());
        updateActiveAlarmText();
        requestNotificationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateActiveAlarmText();
    }

    private void setAlarm() {
        requestFullScreenAlarmPermission();
        long alarmTimeMillis = AlarmHelper.scheduleAlarm(this, alarmTimePicker.getHour(), alarmTimePicker.getMinute());
        updateActiveAlarmText();
        Toast.makeText(this, getString(R.string.alarm_set, AlarmHelper.formatTime(alarmTimeMillis)), Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm() {
        AlarmHelper.cancelAlarm(this);
        AlarmSoundPlayer.stop();
        NotificationHelper.cancelSnoozeNotification(this);
        updateActiveAlarmText();
        Toast.makeText(this, R.string.alarm_canceled, Toast.LENGTH_SHORT).show();
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATIONS);
        }
    }

    private void requestFullScreenAlarmPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return;
        }

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager == null || notificationManager.canUseFullScreenIntent()) {
            return;
        }

        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        Toast.makeText(this, R.string.full_screen_alarm_permission_needed, Toast.LENGTH_LONG).show();
    }

    private void updateActiveAlarmText() {
        long activeAlarmTimeMillis = AlarmHelper.getActiveAlarmTime(this);
        if (activeAlarmTimeMillis == 0L) {
            activeAlarmTextView.setText(R.string.no_alarm_set);
        } else {
            activeAlarmTextView.setText(getString(R.string.active_alarm, AlarmHelper.formatTime(activeAlarmTimeMillis)));
        }
    }
}
