package com.example.todoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_button;
    MyDatabaseHelper myDb;
    Button settingsButton;

    ArrayList<String> task_id, title, description, category, execution_date, task_status;
    CustomAdapter customAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);
        settingsButton = findViewById(R.id.settingsButton);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        myDb = new MyDatabaseHelper(MainActivity.this);
        task_id = new ArrayList<>();
        title = new ArrayList<>();
        description = new ArrayList<>();
        category = new ArrayList<>();
        execution_date = new ArrayList<>();
        task_status = new ArrayList<>();

        sharedPreferences = getSharedPreferences("SettingsPreferences", MODE_PRIVATE);
        storeDataInArrays();

        customAdapter = new CustomAdapter(MainActivity.this, MainActivity.this, task_id, title, description, category, execution_date, task_status, myDb);

        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            refreshData();
        }
    }

    void storeDataInArrays() {
        boolean hideTasks = sharedPreferences.getBoolean("hideTasks", false);
        Cursor cursor = hideTasks ? myDb.readUnfinishedTasks() : myDb.readAllData();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Brak danych", Toast.LENGTH_SHORT).show();
        } else {
            int columnIndexID = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID);
            int columnIndexTitle = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TITLE);
            int columnIndexDescription = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DESCRIPTION);
            int columnIndexCategory = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_CATEGORY);
            int columnIndexExecutionDate = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DUE_AT);
            int columnIndexStatus = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_STATUS);

            while (cursor.moveToNext()) {
                task_id.add(cursor.getString(columnIndexID));
                title.add(cursor.getString(columnIndexTitle));
                description.add(cursor.getString(columnIndexDescription));
                category.add(cursor.getString(columnIndexCategory));
                execution_date.add(cursor.getString(columnIndexExecutionDate));
                task_status.add(cursor.getString(columnIndexStatus));
            }
        }
    }

    void refreshData() {
        task_id.clear();
        title.clear();
        description.clear();
        category.clear();
        execution_date.clear();
        task_status.clear();
        storeDataInArrays();
        customAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
}
