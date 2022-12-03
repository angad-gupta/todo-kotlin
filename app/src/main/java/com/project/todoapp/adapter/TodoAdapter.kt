package com.project.todoapp.adapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.todoapp.R
import com.project.todoapp.models.Todo

class TodoAdapter(private val todoList: ArrayList<Todo>) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    private var listener: OnClickListener? = null

    fun setListener(clickListener: OnClickListener) {
        this.listener = clickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo: Todo = todoList[position]
        holder.bindItems(todo)

        holder.itemView.setOnClickListener(View.OnClickListener {
            if (listener != null) {
                listener!!.onItemClick(todo, position)
            }
        })

        val btDelete = holder.itemView.findViewById<ImageButton>(R.id.btDelete)
        btDelete.setOnClickListener(View.OnClickListener {
            if (listener != null) {
                listener!!.onItemDelete(todo)
            }
        })
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_todo, parent, false))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(todo: Todo) {
            val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
            val tvDesc = itemView.findViewById<TextView>(R.id.tvDesc)
            val tvTimestamp = itemView.findViewById<TextView>(R.id.tvTimestamp)
            tvTitle.text = todo.title
            tvDesc.text = todo.desc
            tvTimestamp.text = todo.timestamp
        }
    }

    interface OnClickListener {
        fun onItemClick(todo: Todo, position: Int)
        fun onItemDelete(todo: Todo)
    }
}