package com.example.todoapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch hideTasksSwitch;
    private SharedPreferences sharedPreferences;
    private Spinner pickTimeNotificationsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        hideTasksSwitch = findViewById(R.id.hideTasksSwitch);
        pickTimeNotificationsSpinner = findViewById(R.id.pickTimeNotificationsSpinner);
        sharedPreferences = getSharedPreferences("SettingsPreferences", MODE_PRIVATE);

        hideTasksSwitch.setChecked(sharedPreferences.getBoolean("hideTasks", false));
        pickTimeNotificationsSpinner.setSelection(sharedPreferences.getInt("notificationTime", 1));


        hideTasksSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("hideTasks", isChecked);
                editor.apply();
            }

        });

        pickTimeNotificationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("notificationTime", position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
