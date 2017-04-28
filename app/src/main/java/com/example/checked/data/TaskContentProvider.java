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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.example.checked.data.TaskContract.TaskEntry.TABLE_NAME;


public class TaskContentProvider extends ContentProvider {

    public static final int TASKS = 100;
    public static final int TASK_WITH_ID = 101;
    public static final int ARCHIVE = 200;
    public static final int ARCHIVE_WITH_ID = 201;
    public static final int NOTES = 300;
    public static final int NOTES_WITH_ID = 301;


    private static final UriMatcher sUriMatcher = buildUriMatcher();


    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS + "/#", TASK_WITH_ID);

        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_ARCHIVE, ARCHIVE);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_ARCHIVE + "/#", ARCHIVE_WITH_ID);

        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_NOTE, NOTES);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_NOTE + "/#", NOTES_WITH_ID);


        return uriMatcher;
    }

    // Member variable for a TaskDbHelper that's initialized in the onCreate() method
    private TaskDbHelper mTaskDbHelper;


    @Override
    public boolean onCreate() {

        Context context = getContext();
        mTaskDbHelper = new TaskDbHelper(context);
        return true;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Get access to the task database (to write new data to)
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned
        long id;
        switch (match) {
            case TASKS:
                // Insert new values into the database
                id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            case ARCHIVE:

                id = db.insert(TaskContract.TaskArchiveEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TaskContract.TaskArchiveEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            case NOTES:

                id = db.insert(TaskContract.NoteEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TaskContract.NoteEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        // Query for the tasks directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case ARCHIVE:
                retCursor = db.query(TaskContract.TaskArchiveEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ARCHIVE_WITH_ID:

                selection = TaskContract.TaskArchiveEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Cursor containing that row of the table.
                retCursor = db.query(TaskContract.TaskArchiveEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case TASKS:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TASK_WITH_ID:

                selection = TaskContract.TaskEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Cursor containing that row of the table.
                retCursor = db.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case NOTES:
                retCursor = db.query(TaskContract.NoteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case NOTES_WITH_ID:
                selection = TaskContract.NoteEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Cursor containing that row of the table.
                retCursor = db.query(TaskContract.NoteEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted;

        // Write the code to delete a single row of data
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case TASK_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;

            case NOTES_WITH_ID:
                // Get the task ID from the URI path
                String noteId = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(TaskContract.NoteEntry.TABLE_NAME, "_id=?", new String[]{noteId});
                break;

            case ARCHIVE_WITH_ID:
                // Get the task ID from the URI path
                String rowId = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(TaskContract.TaskArchiveEntry.TABLE_NAME, "_id=?", new String[]{rowId});
                break;

            case ARCHIVE:

                tasksDeleted = db.delete(TaskContract.TaskArchiveEntry.TABLE_NAME, null, null);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return tasksDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASK_WITH_ID:

                selection = TaskContract.TaskEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                SQLiteDatabase database = mTaskDbHelper.getWritableDatabase();

                // Perform the update on the database and get the number of rows affected
                int rowsUpdated = database.update(TABLE_NAME, values, selection, selectionArgs);

                // If 1 or more rows were updated, then notify all listeners that the data at the
                // given URI has changed
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsUpdated;

            case NOTES_WITH_ID:

                selection = TaskContract.NoteEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                SQLiteDatabase noteDataBase = mTaskDbHelper.getWritableDatabase();

                // Perform the update on the database and get the number of rows affected
                int noteUpdatedId = noteDataBase.update(TaskContract.NoteEntry.TABLE_NAME, values, selection, selectionArgs);

                if (noteUpdatedId != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                // Return the number of rows updated
                return noteUpdatedId;

        }
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
