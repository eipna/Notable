package com.eipna.notable.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.notable.R;
import com.eipna.notable.constants.DateTimePattern;
import com.eipna.notable.constants.NoteSort;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.utils.DateUtil;
import com.eipna.notable.utils.SharedPrefsUtil;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private ArrayList<NoteModel> notes;
    private final Context context;
    private final NoteAdapter.Listener listener;

    public NoteAdapter(Context context, NoteAdapter.Listener listener, ArrayList<NoteModel> notes) {
        this.context = context;
        this.notes = notes;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void search(ArrayList<NoteModel> filteredNotes) {
        notes = filteredNotes;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void sort(NoteSort selectedSort) {
        SharedPrefsUtil prefs = new SharedPrefsUtil(context);
        prefs.setInt("prefs_note_sort", selectedSort.getValue());
        notes.sort(NoteSort.getComparator(context));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Binds each note
        holder.bind(notes.get(position));

        // Handles note click
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    listener.OnItemClick(position);
                }
            }
        });

        // Handles note long click
        holder.itemView.setOnLongClickListener(view -> {
            if (listener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    listener.OnItemLongClick(position);
                }
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadNotes(ArrayList<NoteModel> newNotes) {
        notes = newNotes;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        PrettyTime prettyTime;
        CardView noteCardView;
        TextView titleTextView;
        TextView contentTextView;
        TextView lastUpdatedTextView;
        TextView dateCreatedTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prettyTime = new PrettyTime();
            noteCardView = itemView.findViewById(R.id.noteCard);
            titleTextView = itemView.findViewById(R.id.noteTitle);
            contentTextView = itemView.findViewById(R.id.noteContent);
            lastUpdatedTextView = itemView.findViewById(R.id.noteLastUpdated);
            dateCreatedTextView = itemView.findViewById(R.id.noteDateCreated);

            boolean prefsShowNoteDateCreated = new SharedPrefsUtil(itemView.getContext()).getBoolean("prefs_show_note_date_created", false);
            dateCreatedTextView.setVisibility(prefsShowNoteDateCreated ? View.VISIBLE : View.GONE);

            boolean prefsRoundedNotes = new SharedPrefsUtil(itemView.getContext()).getBoolean("prefs_rounded_notes", true);
            noteCardView.setRadius(prefsRoundedNotes ? 42.0f : 0.0f);

            int prefsNoteTitleMaxLines = new SharedPrefsUtil(itemView.getContext()).getInt("prefs_note_title_max_lines", 1);
            titleTextView.setMaxLines(prefsNoteTitleMaxLines);

            int prefsNoteContentMaxLines = new SharedPrefsUtil(itemView.getContext()).getInt("prefs_note_content_max_lines", 1);
            contentTextView.setMaxLines(prefsNoteContentMaxLines);
        }

        // Binds note data to view components
        public void bind(NoteModel note) {
            String prefsNoteDateFormat = new SharedPrefsUtil(itemView.getContext()).getString("prefs_note_date_format", DateTimePattern.DETAILED_WITHOUT_TIME.toString());

            titleTextView.setText(note.getTitle());
            contentTextView.setText(note.getContent());
            lastUpdatedTextView.setText(prettyTime.format(new Date(note.getLastUpdated())));
            dateCreatedTextView.setText(DateUtil.getDateString(DateTimePattern.fromValue(prefsNoteDateFormat), note.getDateCreated()));
        }
    }

    public interface Listener {
        void OnItemClick(int adapterPos);
        void OnItemLongClick(int adapterPos);
    }
}