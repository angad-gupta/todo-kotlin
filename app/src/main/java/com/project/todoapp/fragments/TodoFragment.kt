package com.project.todoapp.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.project.todoapp.MainActivity
import com.project.todoapp.R
import com.project.todoapp.adapter.TodoAdapter
import com.project.todoapp.helpers.DBHelper
import com.project.todoapp.models.Todo
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [TodoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TodoFragment : Fragment(), TodoAdapter.OnClickListener {
    private lateinit var recyclerView: RecyclerView
    private var todoAdapter: TodoAdapter? = null
    private var dbHelper: DBHelper? = null
    private var todoList = ArrayList<Todo>()

    private lateinit var fcontext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View = inflater.inflate(R.layout.fragment_todo, container, false);

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { v ->
            showNoteDialog(false, null, -1)
        }

        (activity as MainActivity?)!!.toolbar.title = "Todos"

        recyclerView = view.findViewById(R.id.recyclerView)
        fcontext = container!!.context
        dbHelper = DBHelper(fcontext)
        getTodoList()

        // Inflate the layout for this fragment
        return view
    }

    override fun onItemDelete(todo: Todo) {
        deleteConfirmation(todo)
    }

    override fun onItemClick(todo: Todo, position: Int) {
        showNoteDialog(true, todo, position)
    }

    override fun onResume() {
        super.onResume()
        if (todoAdapter != null) {
            getTodoList()
            todoAdapter!!.notifyDataSetChanged()
        }
    }

    private fun getTodoList() {
        todoList = dbHelper!!.allNotes
        recyclerView.layoutManager = LinearLayoutManager(
            fcontext,
            LinearLayoutManager.VERTICAL,
            false
        )
        todoAdapter = TodoAdapter(todoList)
        todoAdapter!!.setListener(this)

        recyclerView.adapter = todoAdapter
    }

    private fun deleteConfirmation(todo: Todo) {
        val alertDialog = AlertDialog.Builder(fcontext)
        alertDialog.setTitle("Confirm Delete")
        alertDialog.setMessage("Are you sure you want to delete this?")
        alertDialog.setIcon(android.R.drawable.ic_delete)
        alertDialog.setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
            dbHelper!!.deleteTodo(todo)
            getTodoList()  // refreshing the list
        })

        alertDialog.setNegativeButton("NO", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel() //Cancel the dialog
        })
        alertDialog.show()
    }

    /**
     * Shows alert dialog with EditText options to enter / edit  a note.
     * when shouldUpdate=true, it automatically displays old note and changes the  button text to UPDATE
     */
    private fun showNoteDialog(shouldUpdate: Boolean, todo: Todo?, position: Int) {
        val view = LayoutInflater.from(fcontext).inflate(R.layout.add_todo, null)

        val alertDialogView = AlertDialog.Builder(fcontext).create()
        alertDialogView.setView(view)

        val tvHeader = view.findViewById<TextView>(R.id.tvHeader)
        val edTitle = view.findViewById<EditText>(R.id.edTitle)
        val edDesc = view.findViewById<EditText>(R.id.edDesc)
        val btAddUpdate = view.findViewById<Button>(R.id.btAddUpdate)
        val btCancel = view.findViewById<Button>(R.id.btCancel)
        if (shouldUpdate) btAddUpdate.text = "Update" else btAddUpdate.text = "Save"

        if (shouldUpdate && todo != null) {
            edTitle.setText(todo.title)
            edDesc.setText(todo.desc)
        }

        btAddUpdate.setOnClickListener(View.OnClickListener {
            val tName = edTitle.text.toString()
            val descName = edDesc.text.toString()

            if (TextUtils.isEmpty(tName)) {
                Snackbar.make(view, "Enter Title!", Snackbar.LENGTH_SHORT).show()
                return@OnClickListener
            } else if (TextUtils.isEmpty(descName)) {
                Snackbar.make(view, "Enter Task Details!", Snackbar.LENGTH_SHORT).show()
                return@OnClickListener
            }
            // check if user updating Todos
            if (shouldUpdate && todo != null) {
                updateNote(Todo(tName, descName), position)      // update note by it's id
            } else {
                createNote(Todo(tName, descName))   // create new note
            }
            alertDialogView.dismiss()
        })

        btCancel.setOnClickListener(View.OnClickListener {
            alertDialogView.dismiss()
        })
        tvHeader.text = if (!shouldUpdate) getString(R.string.lbl_new_todo_title) else getString(R.string.lbl_edit_todo_title)

        alertDialogView.setCancelable(false)
        alertDialogView.show()
    }

    /**
     * Inserting new note in db and refreshing the list
     */
    private fun createNote(todo: Todo) {
        val id = dbHelper!!.insertTodo(todo)    // inserting note in db and getting newly inserted note id
        val new = dbHelper!!.getTodo(id)

        // get the newly inserted note from db
        if (new != null) {
            Log.v("data", new.title!!)
            todoList.add(0, new)    // adding new note to array list at 0 position
            todoAdapter!!.notifyDataSetChanged()  // refreshing the list
        }
    }

    /**
     * Updating note in db and updating item in the list by its position
     */
    private fun updateNote(t: Todo, position: Int) {
        val todo = todoList[position]
        todo.title = t.title    // updating title
        todo.desc = t.desc  // updating description
        dbHelper!!.updateTodo(todo) // updating note in db
        todoList[position] = todo  // refreshing the list
        todoAdapter!!.notifyItemChanged(position)
    }
}