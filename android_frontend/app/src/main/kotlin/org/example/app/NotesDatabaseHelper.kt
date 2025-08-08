package org.example.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

// PUBLIC_INTERFACE
class NotesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notes.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "notes"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT,
                $COLUMN_CONTENT TEXT,
                $COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // PUBLIC_INTERFACE
    fun insertNote(title: String, content: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_CONTENT, content)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // PUBLIC_INTERFACE
    fun updateNote(id: Long, title: String, content: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_CONTENT, content)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    // PUBLIC_INTERFACE
    fun deleteNote(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    // PUBLIC_INTERFACE
    fun getNote(id: Long): Note? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null, "$COLUMN_ID=?", arrayOf(id.toString()),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val note = getNoteFromCursor(cursor)
            cursor.close()
            note
        } else {
            cursor.close()
            null
        }
    }

    // PUBLIC_INTERFACE
    fun getAllNotes(searchQuery: String = ""): List<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase
        val cursor: Cursor = if (searchQuery.isNotEmpty()) {
            db.query(
                TABLE_NAME,
                null,
                "$COLUMN_TITLE LIKE ? OR $COLUMN_CONTENT LIKE ?",
                arrayOf("%$searchQuery%", "%$searchQuery%"),
                null, null,
                "$COLUMN_TIMESTAMP DESC"
            )
        } else {
            db.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_TIMESTAMP DESC")
        }
        if (cursor.moveToFirst()) {
            do {
                notes.add(getNoteFromCursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return notes
    }

    private fun getNoteFromCursor(cursor: Cursor): Note {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
        return Note(id, title, content, timestamp)
    }
}
