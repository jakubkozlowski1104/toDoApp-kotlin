package com.example.todoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
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

    SearchView searchView;
    CheckBox sortByTimeCheckBox;
    ArrayList<String> task_id, title, description, category, execution_date, task_status, created_at;
    CustomAdapter customAdapter;
    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);
        settingsButton = findViewById(R.id.settingsButton);

        sortByTimeCheckBox = findViewById(R.id.sortByTimeCheckBox);

        sortByTimeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                refreshData();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                storeDataInArrays(newText); // Update data based on search results
                customAdapter.filter(newText);
                return false;
            }
        });

        add_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
        });

        myDb = new MyDatabaseHelper(MainActivity.this);
        task_id = new ArrayList<>();
        title = new ArrayList<>();
        description = new ArrayList<>();
        category = new ArrayList<>();
        execution_date = new ArrayList<>();
        task_status = new ArrayList<>();
        created_at = new ArrayList<>(); // Initialize created_at

        sharedPreferences = getSharedPreferences("SettingsPreferences", MODE_PRIVATE);
        storeDataInArrays(""); // Initial data fetch without filtering

        customAdapter = new CustomAdapter(
                MainActivity.this,
                MainActivity.this,
                task_id,
                title,
                description,
                category,
                execution_date,
                task_status,
                created_at,
                myDb
        );

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

    void storeDataInArrays(String searchText) {
        boolean hideTasks = sharedPreferences.getBoolean("hideTasks", false);
        Cursor cursor;
        if (sortByTimeCheckBox.isChecked()) {
            cursor = hideTasks ? myDb.readUnfinishedTasksSortedByTime(searchText) : myDb.readAllDataSortedByTime(searchText);
        } else {
            cursor = hideTasks ? myDb.readUnfinishedTasks(searchText) : myDb.readAllData(searchText);
        }

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Brak danych", Toast.LENGTH_SHORT).show();
        } else {
            int columnIndexID = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID);
            int columnIndexTitle = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TITLE);
            int columnIndexDescription = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DESCRIPTION);
            int columnIndexCategory = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_CATEGORY);
            int columnIndexExecutionDate = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DUE_AT);
            int columnIndexStatus = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_STATUS);
            int columnIndexCreatedAt = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_CREATED_AT);

            task_id.clear();
            title.clear();
            description.clear();
            category.clear();
            execution_date.clear();
            task_status.clear();
            created_at.clear();

            while (cursor.moveToNext()) {
                task_id.add(cursor.getString(columnIndexID));
                title.add(cursor.getString(columnIndexTitle));
                description.add(cursor.getString(columnIndexDescription));
                category.add(cursor.getString(columnIndexCategory));
                execution_date.add(cursor.getString(columnIndexExecutionDate));
                task_status.add(cursor.getString(columnIndexStatus));
                created_at.add(cursor.getString(columnIndexCreatedAt));
            }
        }
    }

    void refreshData() {
        storeDataInArrays(""); // Refresh data without filtering
        customAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
}
