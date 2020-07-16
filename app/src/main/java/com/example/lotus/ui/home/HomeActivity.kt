package com.example.lotus.ui.home

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.androidnetworking.interfaces.DownloadProgressListener
import com.example.lotus.R
import com.example.lotus.models.MediaData
import com.example.lotus.models.Post
import com.example.lotus.ui.CreatePostActivity
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
//    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_main)
//        Dialog(this)
        val fabPost = findViewById<View>(R.id.fab_post)

        fabPost.setOnClickListener(View.OnClickListener { fabPostOnClick() })
        navigationMenuLogic()

        manager = getSupportFragmentManager()

        AndroidNetworking.initialize(getApplicationContext());
    }

    private fun fabPostOnClick() {
        val intent = Intent(this, CreatePostActivity::class.java)
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
      }

    fun detailPost(item: Post) {
        val bundle = Bundle()
        bundle.putParcelable("data", item)
        val dataPost = DetailPost()
        dataPost.arguments = bundle
        appBarLayout?.setVisibility(View.INVISIBLE)
        bottom_sheet?.setVisibility(View.INVISIBLE)
        fab_post?.setVisibility(View.INVISIBLE)
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.fragmentHome, dataPost)
            ?.commit()
    }

    fun backToHome(view: View) {
        appBarLayout?.setVisibility(View.VISIBLE)
        bottom_sheet?.setVisibility(View.VISIBLE)
        fab_post?.setVisibility(View.VISIBLE)
        manager?.beginTransaction()
            ?.replace(R.id.fragmentHome, HomeFragment())?.commit()
    }

    fun downloadMedia(medias: ArrayList<MediaData>){
        val downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        Log.d("DOWNLOAD PATH", downloadsPath.toString())
        for (media in medias) {
            val fileName = media.link?.removeRange(0, media.link.length-10)

            AndroidNetworking.download(media.link, downloadsPath.toString(), fileName)
                .setTag("downloadTest")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(object : DownloadProgressListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onProgress(
                        bytesDownloaded: Long,
                        totalBytes: Long
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val channel1 = NotificationChannel(
                                "channelId",
                                "Progress Notification",
                                //IMPORTANCE_HIGH = shows a notification as peek notification.
                                //IMPORTANCE_LOW = shows the notification in the status bar.
                                NotificationManager.IMPORTANCE_HIGH
                            )
                            channel1.description = "Progress Notification Channel"
                            val manager = getSystemService(
                                NotificationManager::class.java
                            )
                            manager.createNotificationChannel(channel1)
                        }
//                        Log.d("Progress", "downloaded: $bytesDownloaded from total $totalBytes")
//                        val progress = findViewById<TextView>(R.id.progressDownload)
//                        progress.text = "$bytesDownloaded/$totalBytes"
                    }
                })
                .startDownload(object : DownloadListener {
                    override fun onDownloadComplete() {
                        Log.d("Complete", "TEEE")
                        Toast.makeText(this@HomeActivity, "Download Complete", Toast.LENGTH_SHORT).show()
//                        dialog?.dismiss()
                        // do anything after completion
                    }

                    override fun onError(error: ANError?) {
                        // handle error
                        Log.d("Error download", error?.errorCode.toString())
                        Log.d("Error download", error!!.errorDetail)
                    }
                })
        }
    }

    fun showDialog(medias: ArrayList<MediaData>) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_menu_post)
        val download = dialog.findViewById<LinearLayout>(R.id.downloadMedia)
        download.setOnClickListener {
            downloadMedia(medias)
            dialog.dismiss()
        }
//        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }

}