package com.example.todoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
public class UpdateActivity extends AppCompatActivity {

    EditText title_input, description_input;
    Spinner category_spinner;
    Button update_button2, delete_button, backButton;
    String id, title, category, description;
    EditText execution_input;
    long execution_date;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        title_input = findViewById(R.id.title_input2);
        category_spinner = findViewById(R.id.spinner2);
        description_input = findViewById(R.id.description_input2);
        update_button2 = findViewById(R.id.updateButton);
        delete_button = findViewById(R.id.deleteButton);
        execution_input = findViewById(R.id.execution_input);
        calendar = Calendar.getInstance();
        backButton = findViewById(R.id.back);

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Category.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(adapter);

        getAndSetIntentData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        execution_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        update_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                title = title_input.getText().toString().trim();
                description = description_input.getText().toString().trim();
                category = category_spinner.getSelectedItem().toString();
                String attachmentPath = getIntent().getStringExtra("attachment_path");

                try {
                    myDB.updateData(id, title, category, description, calendar.getTimeInMillis(), attachmentPath);

                    // Start NotificationService to update the alarm
                    Intent notificationIntent = new Intent(UpdateActivity.this, NotificationService.class);
                    notificationIntent.putExtra("taskId", id);
                    notificationIntent.putExtra("taskTitle", title);
                    notificationIntent.putExtra("taskDescription", description);
                    notificationIntent.putExtra("taskCategory", category);
                    notificationIntent.putExtra("executionTimeMillis", calendar.getTimeInMillis());
                    notificationIntent.putExtra("attachmentPath", attachmentPath);
                    startService(notificationIntent);

                    finish();
                } catch (Exception e) {
                    Log.e("check", "Error updating task", e);
                    Toast.makeText(UpdateActivity.this, "An error occurred while updating the task", Toast.LENGTH_SHORT).show();
                }
            }
        });


        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("category") && getIntent().hasExtra("description") && getIntent().hasExtra("execution_date")) {
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            category = getIntent().getStringExtra("category");
            description = getIntent().getStringExtra("description");

            // Dodane sprawdzenie czy execution_date nie jest null
            String executionDateString = getIntent().getStringExtra("execution_date");
            if (executionDateString != null) {
                try {
                    execution_date = Long.parseLong(executionDateString);
                    calendar.setTimeInMillis(execution_date);
                } catch (NumberFormatException e) {
                    Log.e("check", "Invalid execution date", e);
                    execution_date = System.currentTimeMillis(); // lub inna wartość domyślna
                }
            } else {
                execution_date = System.currentTimeMillis(); // lub inna wartość domyślna
            }

            title_input.setText(title);
            description_input.setText(description);
            Category selectedCategory = Category.valueOf(category);
            int position = ((ArrayAdapter<Category>) category_spinner.getAdapter()).getPosition(selectedCategory);
            category_spinner.setSelection(position);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            execution_input.setText(sdf.format(calendar.getTime()));
        } else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }



    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + title + " ?");
        builder.setMessage("Are you sure you want to delete " + title + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                myDB.deleteOneRow(id);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    private void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        calendar = Calendar.getInstance();
        new DatePickerDialog(UpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(UpdateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        execution_input.setText(sdf.format(calendar.getTime()));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }
}
