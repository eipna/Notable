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
import com.eipna.notable.models.NoteModel;
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PrettyTime prettyTime = new PrettyTime();
        NoteModel currentNote = notes.get(position);
        holder.titleTextView.setText(currentNote.getTitle());
        holder.contentTextView.setText(currentNote.getContent());
        holder.lastUpdatedTextView.setText(prettyTime.format(new Date(currentNote.getLastUpdated())));

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    listener.launchUpdateActivity(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public interface Listener {
        void launchUpdateActivity(int adapterPos);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadNotes(ArrayList<NoteModel> newNotes) {
        notes = newNotes;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView noteCardView;
        TextView titleTextView;
        TextView contentTextView;
        TextView lastUpdatedTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteCardView = itemView.findViewById(R.id.noteCard);
            titleTextView = itemView.findViewById(R.id.noteTitle);
            contentTextView = itemView.findViewById(R.id.noteContent);
            lastUpdatedTextView = itemView.findViewById(R.id.noteLastUpdated);

            boolean prefsRoundedNotes = new SharedPrefsUtil(itemView.getContext()).getBoolean("prefs_rounded_notes", true);
            noteCardView.setRadius(prefsRoundedNotes ? 32.0f : 0.0f);
        }
    }
}