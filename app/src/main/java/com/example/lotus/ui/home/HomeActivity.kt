package com.example.lotus.ui.home

import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.androidnetworking.AndroidNetworking
import com.example.lotus.R
import com.example.lotus.models.MediaData
import com.example.lotus.models.Post
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.CreatePostActivity
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.notification.NotificationActivity
import com.example.lotus.utils.downloadMedia
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import org.json.JSONException
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {
    private val TAG = "HomeActivity"
    private val mSocket: Socket = IO.socket("http://34.101.109.136:3000")
    private var manager: FragmentManager? = null
    var userID = SharedPrefManager.getInstance(this).user._id
    var token = SharedPrefManager.getInstance(this).token.token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_main)

        bottom_sheet.visibility = View.GONE // For temporary
        // navigationMenuLogic()
        val fabPost = findViewById<View>(R.id.fab_post)

        fabPost.setOnClickListener(View.OnClickListener { fabPostOnClick() })

        mSocket.on(userID.toString(), onNewMessage)
        mSocket.connect()
        Log.d("SOCKET", "${mSocket.connected()},  ${mSocket.connect()}")

        manager = getSupportFragmentManager()

        AndroidNetworking.initialize(getApplicationContext());
    }

    private fun fabPostOnClick() {
        val intent = Intent(this, CreatePostActivity::class.java)
        startActivity(intent)
    }

    private fun navigationMenuLogic() {
        val llBottomSheet =
            findViewById<View>(R.id.bottom_sheet) as LinearLayout
        bottom_sheet.visibility = View.GONE

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

        val btmNav2 =
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

    fun gotoDetailPost(item: Post) {
        val bundle = Bundle()
        bundle.putParcelable("data", item)
        val dataPost = DetailPost()
        dataPost.arguments = bundle
        fab_post?.setVisibility(View.INVISIBLE)
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.fragmentHome, dataPost)
            ?.addToBackStack("Home")
            ?.commit()
    }

    fun setfabPostVisible(){
        fab_post?.visibility = View.VISIBLE
    }

    fun showDialog(medias: ArrayList<MediaData>) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_menu_post)
        val download = dialog.findViewById<LinearLayout>(R.id.downloadMedia)
        val share = dialog.findViewById<LinearLayout>(R.id.sharePost)
        download.setOnClickListener {
            downloadMedia(medias, this)
            dialog.dismiss()
        }

        share.setOnClickListener {
            if (medias.size < 1){
                Toast.makeText(this@HomeActivity, "No media to be downloaded", Toast.LENGTH_SHORT).show()
            }else {
                shareMediaToOtherApp(medias)
                dialog.dismiss()
            }
        }

        dialog.show()

    }

    fun shareMediaToOtherApp(medias: ArrayList<MediaData>){
        for (media in medias){
            val uri: Uri = Uri.parse(media.link)
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, media.link)
                type = "*"
            }
            startActivity(Intent.createChooser(shareIntent, "Share To"))
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        this.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val name: String
            val type: String
            var content: String = ""
            try {
                name = data.getString("name")
                type = data.getString("type")
            } catch (e: JSONException) {
                return@Runnable
            }

            if (type == "LIKE"){
                content = "Like yout post."
            }else if (type == "COMMENT"){
                content = "Comment yout post."
            }else if (type == "FOLLOW"){
                content = "Following you."
            }

            Log.d("Socket on", mSocket.connected().toString())
            createNotification(name, content)

        })
    }

    private fun createNotification(title: String, content: String) {
        val id = 1
        val fullScreenIntent = Intent(this, NotificationActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        var builder = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setSmallIcon(R.drawable.logo_lotus)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setContentText(content)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setColor(getResources().getColor(R.color.colorPrimary))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(id, builder)
        }

    }

}
