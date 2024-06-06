package com.example.todoapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch hideTasksSwitch;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        hideTasksSwitch = findViewById(R.id.hideTasksSwitch);
        sharedPreferences = getSharedPreferences("SettingsPreferences", MODE_PRIVATE);

        hideTasksSwitch.setChecked(sharedPreferences.getBoolean("hideTasks", false));

        hideTasksSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("hideTasks", isChecked);
            editor.apply();
        });
    }
}
