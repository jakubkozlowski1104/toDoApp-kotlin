package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "check";

    private Context context;
    private static final String DATABASE_NAME = "todoApp.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tasks";

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

    @Override
    public void onCreate(SQLiteDatabase db) {
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addTask(String title, String description, Category category, long execution_at, String attachmentFileName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_CATEGORY, category.toString());
        cv.put(COLUMN_DUE_AT, execution_at);
        long currentTimeMillis = System.currentTimeMillis();
        cv.put(COLUMN_CREATED_AT, currentTimeMillis);
        cv.put(COLUMN_STATUS, 0);
        cv.put(COLUMN_NOTIFICATION, 0);
        if (attachmentFileName != null) {
            cv.put(COLUMN_ATTACHMENT_PATH, attachmentFileName);
        }
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Task added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData(String searchText) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " LIKE '%" + searchText + "%'";
        Log.d(TAG, "Executing query: " + query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.rawQuery(query, null) : null;
    }

    Cursor readUnfinishedTasks(String searchText) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0 AND " + COLUMN_TITLE + " LIKE '%" + searchText + "%'";
        Log.d(TAG, "Executing query: " + query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.rawQuery(query, null) : null;
    }

    Cursor readAllDataSortedByTime(String searchText) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " LIKE '%" + searchText + "%' ORDER BY " + COLUMN_DUE_AT + " ASC";
        Log.d(TAG, "Executing query: " + query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.rawQuery(query, null) : null;
    }

    Cursor readUnfinishedTasksSortedByTime(String searchText) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0 AND " + COLUMN_TITLE + " LIKE '%" + searchText + "%' ORDER BY " + COLUMN_DUE_AT + " ASC";
        Log.d(TAG, "Executing query: " + query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.rawQuery(query, null) : null;
    }

    Cursor readAllDataByCategory(String searchText, String category) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CATEGORY + " = '" + category.toUpperCase() + "' AND " + COLUMN_TITLE + " LIKE '%" + searchText + "%'";
        Log.d(TAG, "Executing query: " + query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.rawQuery(query, null) : null;
    }

    Cursor readUnfinishedTasksByCategory(String searchText, String category) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0 AND " + COLUMN_CATEGORY + " = '" + category.toUpperCase() + "' AND " + COLUMN_TITLE + " LIKE '%" + searchText + "%'";
        Log.d(TAG, "Executing query: " + query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.rawQuery(query, null) : null;
    }

    Cursor readAllDataByCategorySortedByTime(String searchText, String category) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CATEGORY + " = '" + category.toUpperCase() + "' AND " + COLUMN_TITLE + " LIKE '%" + searchText + "%' ORDER BY " + COLUMN_DUE_AT + " ASC";
        Log.d(TAG, "Executing query: " + query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.rawQuery(query, null) : null;
    }

    Cursor readUnfinishedTasksByCategorySortedByTime(String searchText, String category) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0 AND " + COLUMN_CATEGORY + " = '" + category.toUpperCase() + "' AND " + COLUMN_TITLE + " LIKE '%" + searchText + "%' ORDER BY " + COLUMN_DUE_AT + " ASC";
        Log.d(TAG, "Executing query: " + query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.rawQuery(query, null) : null;
    }

    void updateData(String row_id, String title, String category, String description, long execution_date, String attachmentPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_DUE_AT, execution_date);
        cv.put(COLUMN_ATTACHMENT_PATH, attachmentPath);
        try {
            long result = db.update(TABLE_NAME, cv, "id=?", new String[]{row_id});
            if(result == -1) {
                Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating data", e);
            Toast.makeText(context, "An error occurred while updating the task", Toast.LENGTH_SHORT).show();
        }
    }



    void updateTaskStatus(String taskId, boolean isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_STATUS, isChecked ? 1 : 0);
        int result = db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{taskId});
        if (result > 0) {
        } else {
            Toast.makeText(context, "Failed to update task status", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    public String getLastInsertedTaskId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        if (cursor != null && cursor.moveToFirst()) {
            String lastId = cursor.getString(0);
            cursor.close();
            return lastId;
        }
        return null;
    }


}
