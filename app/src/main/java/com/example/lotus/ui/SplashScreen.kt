package com.example.lotus.ui

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.lotus.R
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.home.HomeActivity
import com.example.lotus.ui.login.LoginActivity
import com.example.lotus.ui.register.VerificationCodeFragment
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
       val state = SharedPrefManager.getInstance(this).state
        var manager: FragmentManager? = getSupportFragmentManager()

        if(state == "vercode") {
            val logo = findViewById<ImageView>(R.id.logoSplashScreen)
            manager?.beginTransaction()
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                ?.replace(R.id.wrapperSplashScreen, VerificationCodeFragment())
                ?.addToBackStack("Home")
                ?.commit()
            logo.visibility= View.GONE
        }else if (SharedPrefManager.getInstance(this).isLoggedIn) {
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