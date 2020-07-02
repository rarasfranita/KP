package com.example.lotus.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.example.lotus.R
import com.example.lotus.ui.CreatePost
import com.example.lotus.ui.detailpost.DetailPost
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeActivity : AppCompatActivity() {
    private val TAG = "HomeActivity"
    private var manager: FragmentManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_main)

        val fabPost = findViewById<View>(R.id.fab_post)

        fabPost.setOnClickListener(View.OnClickListener { fabPostOnClick() })
        navigationMenuLogic()

        manager = getSupportFragmentManager()
    }

    private fun fabPostOnClick() {
        Log.d(TAG, "FabPostOnclick")
        val intent = Intent(this, CreatePost::class.java)
        startActivity(intent)
    }

    private fun navigationMenuLogic(){
        val llBottomSheet =
            findViewById<View>(R.id.bottom_sheet) as LinearLayout

        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(llBottomSheet)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (BottomSheetBehavior.STATE_DRAGGING == newState) {
                    fab_post.hide()
                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    fab_post.show()
                }
            }

            override fun onSlide(
                bottomSheet: View,
                slideOffset: Float
            ) {
            }
        })

        val btmNav =
            findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val btmNav2  =
            findViewById<BottomNavigationView>(R.id.bottom_navigation_2)

        btmNav2.menu.findItem(R.id.navigation_calendar).setCheckable(false)
        val navController = findNavController(R.id.nav_host_fragment)

        btmNav.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            btmNav2.menu.findItem(R.id.navigation_calendar).setCheckable(false)
            setButtonNavChekable(btmNav, true)
            Log.d("item", item.toString())
            when (item.itemId) {
                R.id.navigation_meditation -> {
                    btmNav.menu.findItem(item.itemId).setChecked(true)
                    navController.navigate(item.itemId)
                }
                R.id.navigation_home -> {
                    btmNav.menu.findItem(item.itemId).setChecked(true)
                    navController.navigate(item.itemId)
                }
                R.id.navigation_reflection -> {
                    btmNav.menu.findItem(item.itemId).setChecked(true)
                    navController.navigate(item.itemId)
                }
                R.id.navigation_tipitaka -> {
                    btmNav.menu.findItem(item.itemId).setChecked(true)
                    navController.navigate(item.itemId)
                }
            }
            false
        })

        btmNav2.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_calendar -> {
                    btmNav2.menu.findItem(R.id.navigation_calendar).setCheckable(true)
                    setButtonNavChekable(btmNav, false)
                    navController.navigate(item.itemId)
                }
            }
            false
        })
    }

    private fun setButtonNavChekable(btmNav: BottomNavigationView, active: Boolean) {
        btmNav.getMenu().findItem(R.id.navigation_reflection).setCheckable(active);
        btmNav.getMenu().findItem(R.id.navigation_home).setCheckable(active);
        btmNav.getMenu().findItem(R.id.navigation_tipitaka).setCheckable(active);
        btmNav.getMenu().findItem(R.id.navigation_meditation).setCheckable(active);
        Log.d("meditation",
            btmNav.getMenu().findItem(R.id.navigation_meditation).isCheckable().toString()
        )

        Log.d("home",
            btmNav.getMenu().findItem(R.id.navigation_home).isCheckable().toString()
        )
    }

    fun detailPost(view: View ) {
        appBarLayout?.setVisibility(View.INVISIBLE)
        bottom_sheet?.setVisibility(View.INVISIBLE)
        fab_post?.setVisibility(View.INVISIBLE)
        manager?.beginTransaction()
            ?.replace(R.id.fragmentHome, DetailPost())?.commit()

    }

}