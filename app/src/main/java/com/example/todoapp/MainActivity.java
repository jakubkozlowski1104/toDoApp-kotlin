package com.example.todoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Spinner;
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
    Spinner categorySpinner;
    ArrayList<String> task_id, title, description, category, execution_date, task_status, created_at, attachment_path;
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
        categorySpinner = findViewById(R.id.spinner3);

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                storeDataInArrays(newText); // Update data based on search results
                customAdapter.filter(newText, categorySpinner.getSelectedItem().toString());
                return false;
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setSelection(adapter.getPosition("All"));

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
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
        created_at = new ArrayList<>();
        attachment_path = new ArrayList<>(); // Initialize attachment_path

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
                attachment_path, // Add this parameter
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
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        if (selectedCategory.equals("All")) {
            if (sortByTimeCheckBox.isChecked()) {
                cursor = hideTasks ? myDb.readUnfinishedTasksSortedByTime(searchText) : myDb.readAllDataSortedByTime(searchText);
            } else {
                cursor = hideTasks ? myDb.readUnfinishedTasks(searchText) : myDb.readAllData(searchText);
            }
        } else {
            if (sortByTimeCheckBox.isChecked()) {
                cursor = hideTasks ? myDb.readUnfinishedTasksByCategorySortedByTime(searchText, selectedCategory) : myDb.readAllDataByCategorySortedByTime(searchText, selectedCategory);
            } else {
                cursor = hideTasks ? myDb.readUnfinishedTasksByCategory(searchText, selectedCategory) : myDb.readAllDataByCategory(searchText, selectedCategory);
            }
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
            int columnIndexAttachment = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ATTACHMENT_PATH);

            task_id.clear();
            title.clear();
            description.clear();
            category.clear();
            execution_date.clear();
            task_status.clear();
            created_at.clear();
            attachment_path.clear(); // Clear attachment_path

            while (cursor.moveToNext()) {
                task_id.add(cursor.getString(columnIndexID));
                title.add(cursor.getString(columnIndexTitle));
                description.add(cursor.getString(columnIndexDescription));
                category.add(cursor.getString(columnIndexCategory));
                execution_date.add(cursor.getString(columnIndexExecutionDate));
                task_status.add(cursor.getString(columnIndexStatus));
                created_at.add(cursor.getString(columnIndexCreatedAt));
                attachment_path.add(cursor.getString(columnIndexAttachment)); // Add attachment_path
            }
        }
    }

    void refreshData() {
        storeDataInArrays(searchView.getQuery().toString()); // Refresh data with current search text
        customAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
}
