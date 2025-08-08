package org.example.app

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: NotesDatabaseHelper
    private var noteId: Long = -1L
    private var isEditing: Boolean = false

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_note_detail)

        dbHelper = NotesDatabaseHelper(this)
        titleEditText = findViewById(R.id.editTextTitle)
        contentEditText = findViewById(R.id.editTextContent)
        val btnSave: Button = findViewById(R.id.buttonSave)

        noteId = intent.getLongExtra("NOTE_ID", -1L)
        isEditing = noteId != -1L

        if (isEditing) {
            val note = dbHelper.getNote(noteId)
            note?.let {
                titleEditText.setText(it.title)
                contentEditText.setText(it.content)
            }
            supportActionBar?.title = getString(R.string.edit_note)
        } else {
            supportActionBar?.title = getString(R.string.new_note)
        }

        btnSave.setOnClickListener { saveNote() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        menu?.findItem(R.id.action_delete)?.isVisible = isEditing
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                confirmDelete()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNote() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()
        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, R.string.empty_note_warning, Toast.LENGTH_SHORT).show()
            return
        }
        if (isEditing) {
            dbHelper.updateNote(noteId, title, content)
            Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show()
        } else {
            dbHelper.insertNote(title, content)
            Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_note)
            .setMessage(R.string.delete_note_confirm)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                dbHelper.deleteNote(noteId)
                Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
