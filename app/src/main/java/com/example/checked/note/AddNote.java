package com.example.checked.note;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.checked.R;
import com.example.checked.data.TaskContract;
import com.thebluealliance.spectrum.SpectrumDialog;

import cn.refactor.lib.colordialog.PromptDialog;

public class AddNote extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText edt_note, edt_note_title;
    String noteInput, titleInput;
    FloatingActionButton fab_add_note, fab_delete_note, note_color;
    ContentValues contentValues;
    int color_position;
    int id;
    Uri mCurrentUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note2);
        contentValues = new ContentValues();

        fab_delete_note = (FloatingActionButton) findViewById(R.id.fab_delete_note);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        if (mCurrentUri == null) {
            fab_delete_note.setVisibility(View.GONE);
        } else {
            fab_delete_note.setVisibility(View.VISIBLE);

            getSupportLoaderManager().initLoader(2, null, this);

        }

        edt_note = (EditText) findViewById(R.id.edt_note);
        edt_note_title = (EditText) findViewById(R.id.note_title);

        fab_add_note = (FloatingActionButton) findViewById(R.id.fab_note);
        fab_add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNote();
            }
        });

        note_color = (FloatingActionButton) findViewById(R.id.note_color);
        note_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick_color();
            }
        });

        fab_delete_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNote();
            }
        });

    }

    private void addNote() {
        titleInput = edt_note_title.getText().toString();
        noteInput = edt_note.getText().toString();

        if (titleInput.length() == 0 && noteInput.length() == 0) {
            new PromptDialog(this)
                    .setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                    .setAnimationEnable(true)
                    .setTitleText("Warning")
                    .setContentText("You must enter a title or a note")
                    .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                        @Override
                        public void onClick(PromptDialog dialog) {
                            dialog.dismiss();
                        }
                    }).show();

            return;
        }

        if (mCurrentUri == null) {
            contentValues.put(TaskContract.NoteEntry.COLUMN_TITLE_NOTE, titleInput);
            contentValues.put(TaskContract.NoteEntry.COLUMN_DESCRIPTION_note, noteInput);
            contentValues.put(TaskContract.NoteEntry.COLUMN_NOTE_COLOR_POSITION, color_position);

            Uri uri = getContentResolver().insert(TaskContract.NoteEntry.CONTENT_URI, contentValues);

            if (uri != null) {
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
            }

            finish();
        } else {
            ContentValues values = new ContentValues();
            values.put(TaskContract.NoteEntry.COLUMN_TITLE_NOTE, titleInput);
            values.put(TaskContract.NoteEntry.COLUMN_DESCRIPTION_note, noteInput);
            values.put(TaskContract.NoteEntry.COLUMN_NOTE_COLOR_POSITION, color_position);

            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            }
            finish();

        }
    }

    private void deleteNote() {
        String stringId = Integer.toString(id);
        Uri uri = TaskContract.NoteEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        getContentResolver().delete(uri, null, null);
        finish();
    }

    private void pick_color() {
        new SpectrumDialog.Builder(AddNote.this)
                .setColors(R.array.note_color_array)
                .setDismissOnColorSelected(false)
                .setFixedColumnCount(4)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            color_position = color;

                        } else {
                            Toast.makeText(AddNote.this, "No color selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show(getSupportFragmentManager(), "hi");
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new android.support.v4.content.CursorLoader(this, mCurrentUri,
                null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int idIndex;
        int noteIndex, titleIndex;
        String title, note;

        if (cursor.moveToFirst()) {


            idIndex = cursor.getColumnIndex(TaskContract.NoteEntry._ID);
            titleIndex = cursor.getColumnIndex(TaskContract.NoteEntry.COLUMN_TITLE_NOTE);
            noteIndex = cursor.getColumnIndex(TaskContract.NoteEntry.COLUMN_DESCRIPTION_note);

            id = cursor.getInt(idIndex);
            title = cursor.getString(titleIndex);
            note = cursor.getString(noteIndex);
            edt_note_title.setText(title);
            edt_note.setText(note);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        edt_note_title.setText("");
        edt_note.setText("");

    }
}
