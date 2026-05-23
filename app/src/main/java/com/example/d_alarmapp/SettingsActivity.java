package com.example.d_alarmapp;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup snoozeRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        snoozeRadioGroup = findViewById(R.id.snoozeRadioGroup);
        selectCurrentSnoozeDuration();

        snoozeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int minutes = getMinutesForCheckedId(checkedId);
            PreferenceHelper.saveSnoozeMinutes(this, minutes);
            Toast.makeText(this, getString(R.string.snooze_saved, minutes), Toast.LENGTH_SHORT).show();
        });
    }

    private void selectCurrentSnoozeDuration() {
        int minutes = PreferenceHelper.getSnoozeMinutes(this);
        snoozeRadioGroup.check(getCheckedIdForMinutes(minutes));
    }

    private int getCheckedIdForMinutes(int minutes) {
        if (minutes == 1) {
            return R.id.snoozeOneMinuteRadioButton;
        }
        if (minutes == 10) {
            return R.id.snoozeTenMinutesRadioButton;
        }
        if (minutes == 15) {
            return R.id.snoozeFifteenMinutesRadioButton;
        }
        return R.id.snoozeFiveMinutesRadioButton;
    }

    private int getMinutesForCheckedId(int checkedId) {
        if (checkedId == R.id.snoozeOneMinuteRadioButton) {
            return 1;
        }
        if (checkedId == R.id.snoozeTenMinutesRadioButton) {
            return 10;
        }
        if (checkedId == R.id.snoozeFifteenMinutesRadioButton) {
            return 15;
        }
        return PreferenceHelper.DEFAULT_SNOOZE_MINUTES;
    }
}
