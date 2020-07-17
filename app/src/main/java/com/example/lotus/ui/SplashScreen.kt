package com.example.lotus.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.lotus.R
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.home.HomeActivity
import com.example.lotus.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hiding title bar of this activity
        window.requestFeature(Window.FEATURE_NO_TITLE)
        //making this activity full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.layout_splash_screen)



    }

    override fun onStart() {
        super.onStart()

        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        } else {
            //4second splash time
            Handler().postDelayed({
                //start main activity
                startActivity(Intent(this@SplashActivity, GeneralActivity::class.java))
                //finish this activity
                finish()
            },4000)
        }
    }

}