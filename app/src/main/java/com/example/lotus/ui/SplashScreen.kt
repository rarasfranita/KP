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
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket


class SplashActivity : AppCompatActivity() {
    private val mSocket: Socket = IO.socket("http://34.101.109.136:3000")

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
            Handler().postDelayed({
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            },4000)
        } else {
            Handler().postDelayed({
                startActivity(Intent(this@SplashActivity, GeneralActivity::class.java))
                finish()
            },4000)
        }
    }

}