/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.checked.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TaskDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "tasksDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 3;


    // Constructor
    TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
                TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY, " +
                TaskContract.TaskEntry.COLUMN_TITLE + " TEXT, " +
                TaskContract.TaskEntry.COLUMN_DESCRIPTION + " TEXT, " +
                TaskContract.TaskEntry.COLUMN_TIME + " INTEGER, " +
                TaskContract.TaskEntry.COLUMN_COLOR_POSITION + " INTEGER ); ";

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE_ARCHIVE = "CREATE TABLE " + TaskContract.TaskArchiveEntry.TABLE_NAME + " (" +
                TaskContract.TaskArchiveEntry._ID + " INTEGER PRIMARY KEY, " +
                TaskContract.TaskArchiveEntry.COLUMN_TITLE_ARCHIVE + " TEXT, " +
                TaskContract.TaskArchiveEntry.COLUMN_DESCRIPTION_ARCHIVE + " TEXT );";

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE_NOTE = "CREATE TABLE " + TaskContract.NoteEntry.TABLE_NAME + " (" +
                TaskContract.NoteEntry._ID + " INTEGER PRIMARY KEY, " +
                TaskContract.NoteEntry.COLUMN_TITLE_NOTE + " TEXT, " +
                TaskContract.NoteEntry.COLUMN_DESCRIPTION_note + " TEXT, " +
                TaskContract.NoteEntry.COLUMN_NOTE_COLOR_POSITION + " INTEGER ); ";


        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_ARCHIVE);
        db.execSQL(CREATE_TABLE_NOTE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskArchiveEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.NoteEntry.TABLE_NAME);

        onCreate(db);
    }
}
