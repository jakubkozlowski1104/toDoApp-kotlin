package com.example.todoapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput;
    private Spinner spinner;
    private TextView executionInput;
    private Button addTaskButton;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.description_input);
        spinner = findViewById(R.id.spinner);
        executionInput = findViewById(R.id.execution_input);
        addTaskButton = findViewById(R.id.addTask_button);
        calendar = Calendar.getInstance();

        executionInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1; // Miesiące są indeksowane od 0
                        String date = year + "-" + (month < 10 ? "0" + month : month) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
                        executionInput.setText(date);
                    }
                }, year, month, day);

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pobranie wartości z pól wejściowych
                String title = titleInput.getText().toString();
                String description = descriptionInput.getText().toString();
                String category = spinner.getSelectedItem().toString();
                String executionDateStr = executionInput.getText().toString();
                long executionDateMillis = convertDateStringToMillis(executionDateStr);

                if (!title.isEmpty() && !description.isEmpty() && executionDateMillis != -1) {
                    MyDatabaseHelper dbHelper = new MyDatabaseHelper(AddActivity.this);
                    dbHelper.addTask(title, description, Category.valueOf(category.toUpperCase()), executionDateMillis); // Zamiana na wielkie litery
                    Toast.makeText(AddActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Zakończenie aktywności po dodaniu zadania
                } else {
                    Toast.makeText(AddActivity.this, "Please fill in all fields with correct date format", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private long convertDateStringToMillis(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
