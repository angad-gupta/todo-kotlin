package com.project.todoapp.helpers

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.project.todoapp.models.Todo
import java.util.*

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1  // Database Version
        private const val DATABASE_NAME = "todos_db" // Database Name
    }

    // Database Table Name
    private val TABLE_NAME:String = "todos"
    // Parameters for Todos Table
    private val ID:String = "id"
    private val TITLE:String = "title"
    private val DESCRIPTION = "description"
    private val TIMESTAMP = "timestamp"

    // 1) Select All Query, 2)looping through all rows and adding to list 3) close db connection, 4) return todos
    val allNotes: ArrayList<Todo>
        @SuppressLint("Range")
        get() {
            val notes = ArrayList<Todo>()
            val selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + TIMESTAMP + " DESC"

            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val todo = Todo(cursor!!.getInt(cursor.getColumnIndex(ID)), cursor.getString(cursor.getColumnIndex(TITLE)),
                        cursor.getString(cursor.getColumnIndex(DESCRIPTION)), cursor.getString(cursor.getColumnIndex(TIMESTAMP)))
                    todo.id = cursor.getInt(cursor.getColumnIndex(ID))
                    todo.title = cursor.getString(cursor.getColumnIndex(TITLE))
                    todo.desc = cursor.getString(cursor.getColumnIndex(DESCRIPTION))
                    todo.timestamp = cursor.getString(cursor.getColumnIndex(TIMESTAMP))
                    notes.add(todo)
                } while (cursor.moveToNext())
            }
            db.close()
            return notes
        }

    // Creating Tables
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL((
                "CREATE TABLE " + TABLE_NAME + "("
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + TITLE + " TEXT,"
                        + DESCRIPTION + " TEXT,"
                        + TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ")"))  // create notes table
    }

    // Upgrading database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun insertTodo(todo: Todo): Long {
        val db = this.writableDatabase   // get writable database as we want to write data
        val values = ContentValues()

        values.put(TITLE, todo.title)
        values.put(DESCRIPTION, todo.desc)
        val id = db.insert(TABLE_NAME, null, values)  // insert row
        db.close()

        return id
    }

    @SuppressLint("Range")
    fun getTodo(id: Long): Todo {
        val db = this.readableDatabase    // get readable database as we are not inserting anything
        val cursor = db.query(TABLE_NAME,
            arrayOf(ID, TITLE, DESCRIPTION, TIMESTAMP), ID + "=?",
            arrayOf(id.toString()), null, null, null, null)

        cursor?.moveToFirst()

        val todo = Todo(
            cursor!!.getInt(cursor.getColumnIndex(ID)),
            cursor.getString(cursor.getColumnIndex(TITLE)),
            cursor.getString(cursor.getColumnIndex(DESCRIPTION)),
            cursor.getString(cursor.getColumnIndex(TIMESTAMP)))

        cursor.close()
        return todo
    }

    fun updateTodo(todo: Todo): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TITLE, todo.title)
        values.put(DESCRIPTION, todo.desc)
        return db.update(TABLE_NAME, values, ID + " = ?", arrayOf(todo.id.toString()))   // updating row
    }

    fun deleteTodo(todo: Todo): Boolean {
        val db = writableDatabase   // Gets the data repository in write mode
        db.delete(TABLE_NAME, ID + " LIKE ?", arrayOf(todo.id.toString())) // Issue SQL statement.
        return true
    }
}