package org.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// PUBLIC_INTERFACE
class NotesAdapter(
    private var notes: List<Note>,
    private val onNoteClicked: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    fun updateNotes(newNotes: List<Note>) {
        this.notes = newNotes
        notifyDataSetChanged()
    }

    // PUBLIC_INTERFACE
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.noteTitle)
        val contentTextView: TextView = itemView.findViewById(R.id.noteContent)
        val timestampTextView: TextView = itemView.findViewById(R.id.noteTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content
        holder.timestampTextView.text = note.timestamp
        holder.itemView.setOnClickListener { onNoteClicked(note) }
    }

    override fun getItemCount(): Int = notes.size
}
