package com.example.todoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput;
    private Spinner spinner;
    private Button addTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.description_input);
        spinner = findViewById(R.id.spinner);
        addTaskButton = findViewById(R.id.addTask_button);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pobranie wartości z pól wejściowych
                String title = titleInput.getText().toString();
                String description = descriptionInput.getText().toString();
                String category = spinner.getSelectedItem().toString();

                if (!title.isEmpty() && !description.isEmpty()) {
                    MyDatabaseHelper dbHelper = new MyDatabaseHelper(AddActivity.this);
                    dbHelper.addTask(title, description, Category.valueOf(category.toUpperCase())); // Zamiana na wielkie litery
                    Toast.makeText(AddActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Zakończenie aktywności po dodaniu zadania
                } else {
                    Toast.makeText(AddActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
