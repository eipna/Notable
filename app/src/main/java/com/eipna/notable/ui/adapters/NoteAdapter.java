package com.eipna.notable.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.notable.R;
import com.eipna.notable.interfaces.NoteListener;
import com.eipna.notable.models.NoteModel;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private ArrayList<NoteModel> notes;
    private final Context context;
    private final NoteListener listener;
    private final PrettyTime prettyTime;

    public NoteAdapter(Context context, NoteListener listener, ArrayList<NoteModel> notes) {
        this.context = context;
        this.notes = notes;
        this.listener = listener;
        this.prettyTime = new PrettyTime();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchNotes(ArrayList<NoteModel> filteredNotes) {
        notes = filteredNotes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_note, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteModel currentNote = notes.get(position);
        holder.titleView.setText(currentNote.getNoteTitle());
        holder.contentView.setText(currentNote.getNoteContent());
        holder.lastUpdatedView.setText(prettyTime.format(new Date(currentNote.getNoteLastUpdated())));
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

        TextView titleView;
        TextView contentView;
        TextView lastUpdatedView;

        public ViewHolder(@NonNull View itemView, NoteListener listener) {
            super(itemView);
            titleView = itemView.findViewById(R.id.noteTitle);
            contentView = itemView.findViewById(R.id.noteContent);
            lastUpdatedView = itemView.findViewById(R.id.noteLastUpdated);

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onNoteClick(position);
                    }
                }
            });

            itemView.setOnLongClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onNoteLongClick(position);
                    }
                }
                return true;
            });
        }
    }
}