package com.project.todoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.project.todoapp.fragments.CallWebserviceFragment
import com.project.todoapp.fragments.NewsFragment
import com.project.todoapp.fragments.TodoFragment


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: NavigationBarView
    public lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Todos"
        setSupportActionBar(toolbar)

        val todoFragment = TodoFragment()
        val newsFragment = NewsFragment()
        val callWebserviceFragment = CallWebserviceFragment()

        setCurrentFragment(todoFragment)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.todo -> setCurrentFragment(todoFragment)
                R.id.news -> setCurrentFragment(newsFragment)
                R.id.webservice -> setCurrentFragment(callWebserviceFragment)

            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, fragment)
                commit()
            }
}