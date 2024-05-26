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

    MyDatabaseHelper myDb;

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

        // Wypełnienie Spinnera kategoriami
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Category.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(adapter);

        update_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kod do aktualizacji danych w bazie danych
            }
        });
        getAndSetIntentData();
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("category") && getIntent().hasExtra("description")) {
            // Pobranie danych z Intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            category = getIntent().getStringExtra("category");
            description = getIntent().getStringExtra("description");

            // Ustawienie danych w widoku
            title_input.setText(title);
            description_input.setText(description);

            // Ustawienie wybranej kategorii w Spinnerze
            Category selectedCategory = Category.valueOf(category);
            int position = ((ArrayAdapter<Category>) category_spinner.getAdapter()).getPosition(selectedCategory);
            category_spinner.setSelection(position);
        } else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}
