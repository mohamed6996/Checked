package com.example.checked.note;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checked.ListItemClickListner;
import com.example.checked.R;
import com.example.checked.utils.SpaceDecorationStaggard;
import com.example.checked.data.TaskContract;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import static com.example.checked.MainActivity.courgette;

public class Notes extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ListItemClickListner {
    Toolbar toolbar;
    RecyclerView recyclerViewNote;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    NoteAdapter noteAdapter;
    Cursor mCursor;
    TextView note_emptyView;
    FloatingActionButton add_note;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        note_emptyView = (TextView) findViewById(R.id.note_empty_view);

        LinearLayout adViewContainer = (LinearLayout) findViewById(R.id.adNote);

        adView = new AdView(this, "1312538295448755_1313929028643015", AdSize.BANNER_HEIGHT_50);
        adViewContainer.addView(adView);
        adView.loadAd();

        toolbar = (Toolbar) findViewById(R.id.note_toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_note_title);
        toolbar_title.setTypeface(courgette);


        recyclerViewNote = (RecyclerView) findViewById(R.id.recyclerviewNote);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewNote.setLayoutManager(staggeredGridLayoutManager);
        noteAdapter = new NoteAdapter(this, this);
        recyclerViewNote.setAdapter(noteAdapter);
        recyclerViewNote.addItemDecoration(new SpaceDecorationStaggard(7));

        getSupportLoaderManager().initLoader(2, null, this);

        add_note = (FloatingActionButton) findViewById(R.id.add_note);
        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Notes.this, AddNote.class));
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.NoteEntry.COLUMN_TITLE_NOTE,
                TaskContract.NoteEntry.COLUMN_DESCRIPTION_note,
                TaskContract.NoteEntry.COLUMN_NOTE_COLOR_POSITION
        };
        return new android.support.v4.content.CursorLoader(this, TaskContract.NoteEntry.CONTENT_URI,
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.mCursor = data;
        if (data.getCount() > 0) {
            note_emptyView.setVisibility(View.GONE);
            recyclerViewNote.setVisibility(View.VISIBLE);

        } else {
            note_emptyView.setVisibility(View.VISIBLE);
            recyclerViewNote.setVisibility(View.GONE);
        }
        noteAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        noteAdapter.swapCursor(null);

    }

    @Override
    public void onListItemClick(int position, int id) {
        Intent intent = new Intent(Notes.this, AddNote.class);
        intent.putExtra("FROM_NOTE", true);
        String stringId = Integer.toString(id);
        Uri uri = TaskContract.NoteEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        intent.setData(uri);
        startActivity(intent);
    }
}
