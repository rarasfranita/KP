package com.example.lotus.ui.dm

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.lotus.R


class MainActivityDM : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maindm)
        val datanull = findViewById<LinearLayout>(R.id.dataNull)
        datanull.visibility = View.GONE

//        getListMessage()
    }


}
