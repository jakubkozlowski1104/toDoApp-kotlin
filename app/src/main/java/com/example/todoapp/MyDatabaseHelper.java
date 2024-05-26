





package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    // Database Name and Version

    private Context context;
    private static final String DATABASE_NAME = "todoApp.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_NAME = "tasks";

    // Tasks Table Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_DUE_AT = "execution_at";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_NOTIFICATION = "notification";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_ATTACHMENT_PATH = "attachment_path";

    MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static String getColumnId() {
        return COLUMN_ID;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tasks table
        String createTasksTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_CREATED_AT + " INTEGER, "
                + COLUMN_DUE_AT + " INTEGER, "
                + COLUMN_STATUS + " INTEGER, "
                + COLUMN_NOTIFICATION + " INTEGER, "
                + COLUMN_CATEGORY + " TEXT, "
                + COLUMN_ATTACHMENT_PATH + " TEXT)";
        db.execSQL(createTasksTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Usunięcie istniejącej tabeli
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Utworzenie nowej tabeli
        onCreate(db);
    }



    void addTask(String title, String description, Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Wstawienie wartości dla kolumny tytułu, opisu i kategorii
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_CATEGORY, category.toString());

        // Wstawienie aktualnej daty dla czasu utworzenia zadania
        long currentTimeMillis = System.currentTimeMillis();
        cv.put(COLUMN_CREATED_AT, currentTimeMillis);

        // Ustawienie statusu na 0 (zadanie niezakończone)
        cv.put(COLUMN_STATUS, 0);

        cv.put(COLUMN_NOTIFICATION, 0);

        // Wstawienie nowego zadania do bazy danych
        long result = db.insert(TABLE_NAME, null, cv);
        // Zamknięcie połączenia z bazą danych

        if (result == -1) {
            Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added successfully!", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }


    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
            if(db != null) {
                cursor = db.rawQuery(query, null);
            }
        return cursor;
    }


    void updateData(String row_id, String title, String category, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_DESCRIPTION, description);
        long result = db.update(TABLE_NAME, cv, "id=?", new String[] {row_id});
        if(result == -1) {
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully updated!", Toast.LENGTH_SHORT).show();
        }
    }
}











