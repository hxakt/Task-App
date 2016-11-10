package com.rakesh.mukherjee.teskapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 100384 on 11/8/2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "taskManager";

    // Contacts table name
    private static final String TABLE_TASKS = "tasks";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_STATE = "state";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_STATE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);

        // Create tables again
        onCreate(db);
    }

    public void addTask(Tasks tasks) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, tasks.getID());
        values.put(KEY_NAME, tasks.getName());
        values.put(KEY_STATE, tasks.getState());

        // Inserting Row
        db.insert(TABLE_TASKS, null, values);
        db.close(); // Closing database connection
    }

    public int getTaskCount(){
        int count;
        String countQuery = "SELECT * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public List<Tasks> getAllTasks() {
        List<Tasks> contactList = new ArrayList<Tasks>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tasks tasks = new Tasks();
                tasks.setID(Integer.parseInt(cursor.getString(0)));
                tasks.setName(cursor.getString(1));
                tasks.setState(Integer.parseInt(cursor.getString(2)));
                // Adding contact to list
                contactList.add(tasks);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public int updateTasks(Tasks tasks) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATE, tasks.getState());

        // updating row
        return db.update(TABLE_TASKS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(tasks.getID()) });
    }

    public void deleteTasks(Tasks tasks) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_ID + " = ?",
                new String[] { String.valueOf(tasks.getID()) });
        db.close();
    }

    public void deleteTableItems(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_TASKS);
        db.close();
    }
}
