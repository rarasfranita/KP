package com.example.lotus.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.lotus.R
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

        //4second splash time
        Handler().postDelayed({
            //start main activity
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            //finish this activity
            finish()
        },4000)

    }
}