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

package com.example.checked;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.checked.archive.Archive;
import com.example.checked.data.TaskContract;
import com.example.checked.note.Notes;
import com.example.checked.settings.Settings;
import com.example.checked.task.AddTaskActivity;
import com.example.checked.task.CustomCursorAdapter;
import com.example.checked.utils.SpaceItemDecoration;
import com.facebook.ads.AdView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ListItemClickListner, SharedPreferences.OnSharedPreferenceChangeListener {

    // Constants for logging and referring to a unique loader
    private static final int TASK_LOADER_ID = 0;

    public static boolean isVibrate;
    public static boolean isCount;
    public static int DEFAULT_COLOR;
    public static int NOTE_DEFAULT_COLOR;


    public static String alert;
    // Member variables for the adapter and RecyclerView
    private CustomCursorAdapter mAdapter;
    //  private RecyclerViewAdapter mAdapter;

    RecyclerView mRecyclerView;
    LinearLayoutManager linearLayoutManager;

    int id;
    public static List pending;
    Toolbar toolbar;

    private FloatingActionMenu menuRed;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton add_note;


    TextView emptyView;
    Cursor cursor;

    TextView count;
    public static Typeface courgette;
    public static Typeface roboto;

    private String[] emptyTexts = {
            "No things is good things.",
            "Everything is done.",
            "Genius only means hard-working all one’s life.",
            "Reading makes a full man, conference a ready man, and writing an exact man.",
            "And those who were seen dancing were thought to be insane by those who could not hear the music.",
            "The only limit to our realization of tomorrow will be our doubts of today.	",
            "There is no doubt that good things will come, and when it comes, it will be a surprise. ",
            "Reality is merely an illusion, albeit a very persistent one.",
            "The first and greatest victory is to conquer yourself, to be conquered by yourself is of all things most " +
                    "shameful and vile.",
            "A pessimist sees the difficulty in every opportunity, an optimist sees the opportunity in every difficulty.",
            "There is nothing noble in being superior to some other man. The true nobility is in being superior to your " +
                    "previous self. ",
            "A man is not old as long as he is seeking something. A man is not old until regrets take the place of dreams.",
            "I was not looking for my dreams to interpret my life, but rather for my life to interpret my dreams. ",
            "Life is about making an impact, not making an income.",
            "Strive not to be a success, but rather to be of value.",
            "You miss 100% of the shots you don’t take.",
            "Definiteness of purpose is the starting point of all achievement.",
            "The most common way people give up their power is by thinking they don’t have any.",
            "Your time is limited, so don’t waste it living someone else’s life.",
            "The two most important days in your life are the day you are born and the day you find out why.",
            "By working faithfully eight hours a day you may eventually get to be boss and work twelve hours a day."

    };

    SharedPreferences sharedPreference;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        courgette = Typeface.createFromAsset(getAssets(), "Courgette-Regular.ttf");
        roboto = Typeface.createFromAsset(getAssets(), "Rationale-Regular.ttf");


        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this);

        MainActivity.isVibrate = sharedPreference.getBoolean("vibrate", true);
        MainActivity.isCount = sharedPreference.getBoolean("count", true);
        MainActivity.DEFAULT_COLOR = sharedPreference.getInt("preference_color", (int) Constants.FIVE);
        MainActivity.NOTE_DEFAULT_COLOR = sharedPreference.getInt("note_color", (int) Constants.FIVE);

        sharedPreference.registerOnSharedPreferenceChangeListener(this);

     /*   LinearLayout adViewContainer = (LinearLayout) findViewById(R.id.adMain);

        adView = new AdView(this, "1312538295448755_1313929028643015", AdSize.BANNER_HEIGHT_50);
        adViewContainer.addView(adView);
        adView.loadAd();*/


        emptyView = (TextView) findViewById(R.id.emptyview);

        menuRed = (FloatingActionMenu) findViewById(R.id.menu);
        fab1 = (FloatingActionButton) findViewById(R.id.menu_item);
        fab2 = (FloatingActionButton) findViewById(R.id.menu_item_2);
        add_note = (FloatingActionButton) findViewById(R.id.note_item);


        menuRed.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    //   mRecyclerView.setVisibility(View.INVISIBLE);
                    // fab:menu_backgroundColor="#86ffffff"  instead
                } else {
                    //   mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
        menuRed.setClosedOnTouchOutside(true);


        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuRed.close(true);
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuRed.close(true);
                Intent addTaskIntent = new Intent(MainActivity.this, Archive.class);
                addTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(addTaskIntent);
            }
        });

        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuRed.close(true);
                Intent addNoteIntent = new Intent(MainActivity.this, Notes.class);
                startActivity(addNoteIntent);
            }
        });

        pending = new ArrayList();

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setTypeface(courgette);

        toolbar.inflateMenu(R.menu.menu_settings);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MainActivity.this, Settings.class));
                return true;
            }
        });


        count = (TextView) findViewById(R.id.count);


        // Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTasks);
        linearLayoutManager = new LinearLayoutManager(this);


        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new CustomCursorAdapter(this, this);

        mRecyclerView.setAdapter(mAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                id = (int) viewHolder.itemView.getTag();


                final CustomCursorAdapter adapter = (CustomCursorAdapter) mRecyclerView.getAdapter();
                //   int position = viewHolder.getAdapterPosition();

                if (!pending.contains(id)) {
                    pending.add(id);
                    adapter.pendingRemoval(id);
                    //  findViewById(R.id.include_regular).setVisibility(View.GONE);

                } else {
                    //  pending.remove(id);

                }

                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);

            }

        }).attachToRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(new SpaceItemDecoration(7));


        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        pending.clear();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sharedPreference.unregisterOnSharedPreferenceChangeListener(this);

    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_TITLE,
                TaskContract.TaskEntry.COLUMN_DESCRIPTION,
                TaskContract.TaskEntry.COLUMN_TIME,
                TaskContract.TaskEntry.COLUMN_COLOR_POSITION

        };

        return new android.support.v4.content.CursorLoader(this, TaskContract.TaskEntry.CONTENT_URI,
                projection,
                null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        this.cursor = data;


        if (isCount) {
            count.setVisibility(View.VISIBLE);
            count.setText("" + data.getCount());
        } else {
            count.setVisibility(View.GONE);
        }

        if (data.getCount() > 0) {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

        } else {
            mRecyclerView.setVisibility(View.GONE);
            String random = emptyTexts[new Random().nextInt(emptyTexts.length)];
            emptyView.setText(random);
            emptyView.setTypeface(courgette);
            emptyView.setVisibility(View.VISIBLE);
        }
        mAdapter.swapCursor(data);

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(int position, int id) {

        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        String stringId = Integer.toString(id);
        Uri uri = TaskContract.TaskEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        intent.setData(uri);
        startActivity(intent);

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("vibrate")) {
            MainActivity.isVibrate = sharedPreferences.getBoolean("vibrate", true);
        }

        if (key.equals("count")) {
            MainActivity.isCount = sharedPreferences.getBoolean("count", true);
        }

        if (key.equals("preference_color")) {
            MainActivity.DEFAULT_COLOR = sharedPreferences.getInt("preference_color", (int) Constants.FIVE);
        }

        if (key.equals("note_color")) {
            MainActivity.NOTE_DEFAULT_COLOR = sharedPreferences.getInt("note_color", (int) Constants.FIVE);
        }


    }


}

