package com.eipna.notable.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.notable.R;
import com.eipna.notable.data.interfaces.NoteListener;
import com.eipna.notable.data.model.NoteModel;

import java.util.ArrayList;
import java.util.Date;

import org.ocpsoft.prettytime.*;

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
        View view = LayoutInflater.from(context).inflate(R.layout.layout_note, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteModel currentNote = notes.get(position);
        holder.noteTitle.setText(currentNote.getNoteTitle());
        holder.noteContent.setText(currentNote.getNoteContent());
        holder.noteLastUpdated.setText(prettyTime.format(new Date(currentNote.getNoteLastUpdated())));

        // Load custom fade-in animation
        Animation fadeInAnime = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        holder.itemView.startAnimation(fadeInAnime);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView noteTitle;
        TextView noteContent;
        TextView noteLastUpdated;

        public ViewHolder(@NonNull View itemView, NoteListener listener) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.noteContent);
            noteLastUpdated = itemView.findViewById(R.id.noteLastUpdated);

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