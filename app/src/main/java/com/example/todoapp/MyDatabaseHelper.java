





package com.example.todoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    // Database Name and Version
    private static final String DATABASE_NAME = "todoApp.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_TASKS = "tasks";

    // Tasks Table Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_DUE_AT = "execution_at";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_NOTIFICATION = "notification";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_ATTACHMENT_PATH = "attachment_path";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tasks table
        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " ("
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
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        // Create tables again
        onCreate(db);
    }
}











