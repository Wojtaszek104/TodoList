package com.example.todolist

import android.database.sqlite.SQLiteDatabase
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_todo.view.cbDone
import kotlinx.android.synthetic.main.item_todo.view.tvTodoTitle

class TodoAdapter (
    private val todos: MutableList<Todo>, val db: SQLiteDatabase
    ) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
       return TodoViewHolder(
           LayoutInflater.from(parent.context).inflate(
               R.layout.item_todo,
               parent,
               false
           )
       )
    }

    init {
        loadTodosFromDatabase()
    }

    private fun loadTodosFromDatabase() {
        val projection = arrayOf("message")
        val cursor = db.query(
            TableInfo.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        todos.clear() // Usunięcie istniejących notatek przed wczytaniem z bazy danych

        with(cursor) {
            while (moveToNext()) {
                val todoTitle = getString(getColumnIndexOrThrow("message"))
                val todo = Todo(todoTitle)
                todos.add(todo)
            }
        }

        notifyDataSetChanged()
    }

    fun addTodo(todo: Todo) {
        todos.add(todo)
        notifyItemInserted(todos.size - 1)
    }

    fun deleteDoneTodos() {

        val checkedTodos = getCheckedTodos()

        for (todo in checkedTodos) {
            db.delete(TableInfo.TABLE_NAME, "message = ?", arrayOf(todo.title))
        }

        todos.removeAll { todo ->
            todo.isChecked
        }
        notifyDataSetChanged()
    }

    fun getCheckedTodos(): List<Todo> {
        val checkedTodos = mutableListOf<Todo>()

        for (todo in todos) {
            if (todo.isChecked) {
                checkedTodos.add(todo)
            }
        }

        return checkedTodos
    }

    private fun toggleStrikeThrough(tvTodoTitle: TextView, isChecked: Boolean) {
        if(isChecked) {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curTodo = todos[position]
        holder.itemView.apply {
            tvTodoTitle.text = curTodo.title
            cbDone.isChecked = curTodo.isChecked
            toggleStrikeThrough(tvTodoTitle, curTodo.isChecked)
            cbDone.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(tvTodoTitle, isChecked)
                curTodo.isChecked = !curTodo.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }
}