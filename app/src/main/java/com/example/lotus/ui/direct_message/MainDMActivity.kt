package com.example.lotus.ui.direct_message

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.lotus.R
import com.example.lotus.databinding.ActivityMaindmBinding

class MainDMActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        val binding = DataBindingUtil.setContentView<ActivityMaindmBinding>(this, R.layout.activity_maindm)
    }
}