package com.example.todoapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput;
    private Spinner spinner;
    private TextView executionInput, attachmentInfo;
    private Button addTaskButton, attachmentButton;
    private Calendar calendar;
    private static final int PICK_FILE_REQUEST_CODE = 1001;
    private Uri attachmentUri;
    private static final String TAG = "checkAttach";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.description_input);
        spinner = findViewById(R.id.spinner);
        executionInput = findViewById(R.id.execution_input);
        attachmentButton = findViewById(R.id.attachment_button);
        attachmentInfo = findViewById(R.id.attchmentInfo);
        addTaskButton = findViewById(R.id.addTask_button);
        calendar = Calendar.getInstance();

        attachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickAttachment();
            }
        });

        executionInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString();
                String description = descriptionInput.getText().toString();
                String category = spinner.getSelectedItem().toString().toUpperCase();
                String executionDateStr = executionInput.getText().toString();
                long executionDateMillis = convertDateStringToMillis(executionDateStr);

                if (!title.isEmpty() && !description.isEmpty() && executionDateMillis != -1) {
                    String attachmentFileName = attachmentUri != null ? getAttachmentFileName(attachmentUri) : null;
                    MyDatabaseHelper dbHelper = new MyDatabaseHelper(AddActivity.this);
                    dbHelper.addTask(title, description, Category.valueOf(category), executionDateMillis, attachmentFileName);
                    Toast.makeText(AddActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddActivity.this, "Please fill in all fields with correct date format", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pickAttachment() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            attachmentUri = data.getData();
            if (attachmentUri != null) {
                String attachmentFileName = getAttachmentFileName(attachmentUri);
                attachmentInfo.setText(attachmentFileName);
                Log.d(TAG, "onActivityResult: Attachment File Name - " + attachmentFileName);
            }
        }
    }


    @SuppressLint("Range")
    private String getAttachmentFileName(Uri uri) {
        String displayName = null;
        Log.d(TAG, "getAttachmentFileName: URI - " + uri.toString());
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Log.d(TAG, "getAttachmentFileName: Display Name - " + displayName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (uri.getScheme().equals("file")) {
            displayName = new File(uri.getPath()).getName();
            Log.d(TAG, "getAttachmentFileName: File Name - " + displayName);
        }
        return displayName;
    }


    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String date = year + "-" + (month < 10 ? "0" + month : month) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
                executionInput.setText(date);
            }
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
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
