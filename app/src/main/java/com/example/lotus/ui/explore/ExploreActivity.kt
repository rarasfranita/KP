package com.example.lotus.ui.explore

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.lotus.R




class ExploreActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        inflater.inflate(R.menu.top_app_bar_dm, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }
}