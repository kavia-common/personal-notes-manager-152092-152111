package org.example.app

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var recyclerView: RecyclerView
    private var notesList = listOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        dbHelper = NotesDatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewNotes)
        val fab: FloatingActionButton = findViewById(R.id.fabAddNote)

        recyclerView.layoutManager = LinearLayoutManager(this)
        notesAdapter = NotesAdapter(notesList) { note ->
            val intent = Intent(this, NoteDetailActivity::class.java)
            intent.putExtra("NOTE_ID", note.id)
            startActivity(intent)
        }

        recyclerView.adapter = notesAdapter

        fab.setOnClickListener {
            val intent = Intent(this, NoteDetailActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes(query: String = "") {
        notesList = dbHelper.getAllNotes(query)
        notesAdapter.updateNotes(notesList)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView?.queryHint = getString(R.string.search_hint)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadNotes(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                loadNotes(newText ?: "")
                return true
            }
        })
        return true
    }
}
