package com.example.todoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateActivity extends AppCompatActivity {

    EditText title_input, description_input;
    Spinner category_spinner;
    Button update_button2;
    String id, title, category, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        // Inicjalizacja pól widoku
        title_input = findViewById(R.id.title_input2);
        category_spinner = findViewById(R.id.spinner2);
        description_input = findViewById(R.id.description_input2);
        update_button2 = findViewById(R.id.updateButton);
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Category.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(adapter);

        getAndSetIntentData();

        update_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);

                title = title_input.getText().toString().trim();
                description = description_input.getText().toString().trim();
                category = category_spinner.getSelectedItem().toString();
                myDB.updateData(id, title, category, description);
                finish();
            }
        });
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("category") && getIntent().hasExtra("description")) {
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            category = getIntent().getStringExtra("category");
            description = getIntent().getStringExtra("description");

            title_input.setText(title);
            description_input.setText(description);
            Category selectedCategory = Category.valueOf(category);
            int position = ((ArrayAdapter<Category>) category_spinner.getAdapter()).getPosition(selectedCategory);
            category_spinner.setSelection(position);
        } else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}
