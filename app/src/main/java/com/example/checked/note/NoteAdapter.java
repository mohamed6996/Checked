package com.example.checked.note;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checked.Constants;
import com.example.checked.ListItemClickListner;
import com.example.checked.MainActivity;
import com.example.checked.R;
import com.example.checked.data.TaskContract;

import static com.example.checked.MainActivity.roboto;

/**
 * Created by lenovo on 4/24/2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    long color_pos;

    final private ListItemClickListner mOnClickListner;
    private Context mContext;
    private Cursor mCursor;

    public NoteAdapter(Context mContext, ListItemClickListner listner) {
        this.mContext = mContext;
        this.mOnClickListner = listner;

    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.note_row_item, parent, false);

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(TaskContract.NoteEntry._ID);
        int noteTitleIndex = mCursor.getColumnIndex(TaskContract.NoteEntry.COLUMN_TITLE_NOTE);
        int noteIndex = mCursor.getColumnIndex(TaskContract.NoteEntry.COLUMN_DESCRIPTION_note);
        int colorPositionIndex = mCursor.getColumnIndex(TaskContract.NoteEntry.COLUMN_NOTE_COLOR_POSITION);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        int id = mCursor.getInt(idIndex);
        String noteTitle = mCursor.getString(noteTitleIndex);
        String note = mCursor.getString(noteIndex);
        color_pos = mCursor.getLong(colorPositionIndex);
        selectColor(color_pos, holder);

        if (noteTitle.length() == 0) {
            holder.noteTitle.setVisibility(View.GONE);
        } else {
            holder.noteTitle.setVisibility(View.VISIBLE);
        }

        holder.itemView.setTag(id);
        holder.noteDescriptionView.setText(note);
        holder.noteTitle.setText(noteTitle);

        holder.noteTitle.setTypeface(roboto);


    }

    public void selectColor(long position, NoteAdapter.NoteViewHolder holder) {
        int[] color_array = mContext.getResources().getIntArray(R.array.note_color_array);

        //   Toast.makeText(mContext, "" + position, Toast.LENGTH_LONG).show();

        if (position == Constants.ONE) {
            holder.noteBackground.setBackgroundColor(color_array[4]);
        }
        if (position == Constants.TWO) {
            holder.noteBackground.setBackgroundColor(color_array[5]);
        }
        if (position == Constants.THREE) {
            holder.noteBackground.setBackgroundColor(color_array[6]);
        }
        if (position == Constants.FOUR) {
            holder.noteBackground.setBackgroundColor(color_array[7]);
        }
        if (position == Constants.FIVE) {
            holder.noteBackground.setBackgroundColor(color_array[8]);
        }
        if (position == Constants.SIX) {
            holder.noteBackground.setBackgroundColor(color_array[9]);
        }
        if (position == Constants.SEVEN) {
            holder.noteBackground.setBackgroundColor(color_array[10]);
        }
        if (position == Constants.EIGHT) {
            holder.noteBackground.setBackgroundColor(color_array[11]);
        }
        // the new 4 colors
        if (position == Constants.NINE) {
            holder.noteBackground.setBackgroundColor(color_array[0]);
        }
        if (position == Constants.TEN) {
            holder.noteBackground.setBackgroundColor(color_array[1]);
        }
        if (position == Constants.ELEVEN) {
            holder.noteBackground.setBackgroundColor(color_array[2]);
        }
        if (position == Constants.TWELVE) {
            holder.noteBackground.setBackgroundColor(color_array[3]);
        }

        if (position == 0) {
            holder.noteBackground.setBackgroundColor(MainActivity.NOTE_DEFAULT_COLOR);
        }

    }


    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView noteDescriptionView, noteTitle;
        LinearLayout noteBackground;


        public NoteViewHolder(View itemView) {
            super(itemView);

            noteTitle = (TextView) itemView.findViewById(R.id.note_title);
            noteDescriptionView = (TextView) itemView.findViewById(R.id.note_content);
            noteBackground = (LinearLayout) itemView.findViewById(R.id.note_background);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            mOnClickListner.onListItemClick(getAdapterPosition(), (Integer) itemView.getTag());


        }
    }
}
