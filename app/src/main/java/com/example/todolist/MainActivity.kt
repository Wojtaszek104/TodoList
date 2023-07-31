package com.example.todolist

import android.content.ContentValues
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.btnAddTodo
import kotlinx.android.synthetic.main.activity_main.btnDeleteDoneTodos
import kotlinx.android.synthetic.main.activity_main.etTodoTitle
import kotlinx.android.synthetic.main.activity_main.rvTodoItems

class MainActivity : ComponentActivity() {
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dbHelper = DataBaseHelper(applicationContext)
        val db = dbHelper.writableDatabase
        val save_info_Toast = Toast.makeText(applicationContext, "Notatka zapisana, możesz wyjść", Toast.LENGTH_LONG)
        todoAdapter = TodoAdapter(mutableListOf(), db)
        rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)


        btnAddTodo.setOnClickListener {
            val todoTitle = etTodoTitle.text.toString()
            if(todoTitle.isNotEmpty()) {
                val value = ContentValues()
                value.put("message", todoTitle);
                db.insert(TableInfo.TABLE_NAME, null, value)
              save_info_Toast.show()
                val todo = Todo(todoTitle)
                todoAdapter.addTodo(todo)
                etTodoTitle.text.clear()
            }
        }
        btnDeleteDoneTodos.setOnClickListener {
            todoAdapter.deleteDoneTodos()
        }


    }
}
