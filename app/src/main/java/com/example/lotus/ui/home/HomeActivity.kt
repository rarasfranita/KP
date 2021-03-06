package com.example.lotus.ui.home

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.DM.Get.Get
import com.example.lotus.models.MediaData
import com.example.lotus.models.Post
import com.example.lotus.models.Respon
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.dm.MainActivityDM
import com.example.lotus.ui.notification.NotificationActivity
import com.example.lotus.ui.profile.ProfileActivity
import com.example.lotus.utils.downloadMedia
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
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

        mSocket.on(userID.toString(), onNewMessage)
        mSocket.connect()
        Log.d("SOCKET", "${mSocket.connected()},  ${mSocket.connect()}")

        manager = supportFragmentManager

        AndroidNetworking.initialize(applicationContext)
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
//                    fab_post.hide()
                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
//                    fab_post.show()
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

        btmNav2.menu.findItem(R.id.navigation_calendar).isCheckable = false
        val navController = findNavController(R.id.nav_host_fragment)

        btmNav.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            btmNav2.menu.findItem(R.id.navigation_calendar).isCheckable = false
            setButtonNavChekable(btmNav, true)

            when (item.itemId) {
                R.id.navigation_meditation -> {
                    btmNav.menu.findItem(item.itemId).isChecked = true
                    navController.navigate(item.itemId)
                }
                R.id.navigation_home -> {
                    btmNav.menu.findItem(item.itemId).isChecked = true
                    navController.navigate(item.itemId)
                }
                R.id.navigation_reflection -> {
                    btmNav.menu.findItem(item.itemId).isChecked = true
                    navController.navigate(item.itemId)
                }
                R.id.navigation_tipitaka -> {
                    btmNav.menu.findItem(item.itemId).isChecked = true
                    navController.navigate(item.itemId)
                }
            }
            false
        })

        btmNav2.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_calendar -> {
                    btmNav2.menu.findItem(R.id.navigation_calendar).isCheckable = true
                    setButtonNavChekable(btmNav, false)
                    navController.navigate(item.itemId)
                }
            }
            false
        })
    }

    private fun setButtonNavChekable(btmNav: BottomNavigationView, active: Boolean) {
        btmNav.menu.findItem(R.id.navigation_reflection).isCheckable = active
        btmNav.menu.findItem(R.id.navigation_home).isCheckable = active
        btmNav.menu.findItem(R.id.navigation_tipitaka).isCheckable = active
        btmNav.menu.findItem(R.id.navigation_meditation).isCheckable = active
    }

    fun gotoDetailPost(item: Post) {
        val bundle = Bundle()
        bundle.putParcelable("data", item)
        val dataPost = DetailPost()
        dataPost.arguments = bundle
//        fab_post?.setVisibility(View.INVISIBLE)
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.fragmentHome, dataPost)
            ?.addToBackStack("Home")
            ?.commit()
    }

    fun gotoProfilePicture(UID: String) {
        val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
        intent.putExtra("userID", UID)
        startActivity(intent)
    }

    fun showDialog(medias: ArrayList<MediaData>, PostID : String) {
        Log.d("PostID", PostID)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_menu_post)
        val download = dialog.findViewById<LinearLayout>(R.id.downloadMedia)
        val share = dialog.findViewById<LinearLayout>(R.id.sharePost)
        val report = dialog.findViewById<LinearLayout>(R.id.reportPost)
        download.setOnClickListener {
            downloadMedia(medias, this)
            dialog.dismiss()
        }

        share.setOnClickListener {
            if (medias.size < 1) {
                Toast.makeText(this@HomeActivity, "No media to be downloaded", Toast.LENGTH_SHORT)
                    .show()
            } else {
                shareMediaToOtherApp(medias)
                dialog.dismiss()
            }
        }

        report.setOnClickListener {
            Log.d("PostIDshowDialog", PostID)
            dialogReport(PostID)
//                postId = ""
        }

        dialog.show()

    }

    fun dialogReport(postID: String) {
        Log.d("PostIDshowdialogReport", postID)
//        postId: String
        val dialogReport = Dialog(this)
        dialogReport.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogReport.setContentView(R.layout.report_layout)

        val cancel = dialogReport.findViewById<MaterialButton>(R.id.btnCancel)
        val report = dialogReport.findViewById<MaterialButton>(R.id.btnReport)

        cancel.setOnClickListener {
            dialogReport.dismiss()
        }
        report.setOnClickListener {
            val radioGroup = dialogReport.findViewById<RadioGroup>(R.id.radBtn)
            val selectedId: Int = radioGroup.checkedRadioButtonId
            val radioButton = dialogReport.findViewById(selectedId) as RadioButton
            Log.d("radioButton", radioButton.text.toString())
            val text = radioButton.text.toString()
            reportPost(text, postID)
            dialogReport.dismiss()
        }
        dialogReport.show()
    }

    private fun reportPost(text:String, postID: String) {
        Log.d("postIDreportPost", postID)
        Log.d("userID", userID.toString())
        Log.d("text", text)


        AndroidNetworking.post(EnvService.ENV_API + "/posts/{postId}/report")
//            .addHeaders("Authorization", "Bearer $token")
            .addPathParameter("postId", postID)
            .addBodyParameter("userId", userID.toString())
            .addBodyParameter("text", text)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respon::class.java,
                object : ParsedRequestListener<Respon> {
                    override fun onResponse(respon: Respon) {
                        if (respon.code.toString() == "200") {
                            Log.d("Report Post", "Success")
                            if (respon.data.toString() == "you have been reported this post"){
                                Toast.makeText(
                                    this@HomeActivity,
                                    "${respon.data}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d("DATANYA", respon.data.toString())
                            } else {
                                Toast.makeText(
                                    this@HomeActivity,
                                    "Report Succes",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@HomeActivity,
                                "Error while Report Post, code: ${respon.code} \n${respon.data}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("ERROR!!!", "Report Post ${respon.code}, ${respon.data}")
                        }
                    }

                    override fun onError(anError: ANError) {
                        Toast.makeText(
                            this@HomeActivity,
                            "Error while Report Post, code: ${anError.errorDetail}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("ERROR!!!", "Report Post ${anError.errorCode}")

                    }
                })
    }

    fun shareMediaToOtherApp(medias: ArrayList<MediaData>) {
        for (media in medias) {
            val uri: Uri = Uri.parse(media.link)
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, media.link)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share To"))
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        this.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            var name: String = ""
            var type: String = ""
            var content: String = ""
            var channelID: String = ""
            var message: Get? = null

            try {
                name = data.getString("name")
                type = data.getString("type")
            } catch (e: JSONException) {
                name = ""
            }

            if (type == "LIKE") {
                content = "Like your post."
                channelID = "Notif"
            } else if (type == "COMMENT") {
                content = "Comment your post."
                channelID = "Notif"
            } else if (type == "FOLLOW") {
                content = "Following you."
                channelID = "Notif"
            } else {
                val gson = Gson()
                message = gson.fromJson(data.toString(), Get::class.java)
                name = message.sender.name.toString()
                content = "Send you message."
                channelID = "DM"
            }

            createNotification(name, content, channelID)
        })
    }

    private fun createNotification(title: String, content: String, channelID: String) {
        var id = 1
        lateinit var fullScreenIntent: Any

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = title
            val descriptionText = content
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        if (channelID == "DM") {
            id = 2
            fullScreenIntent = Intent(this, MainActivityDM::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            fullScreenIntent = Intent(this, NotificationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        var builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.logo_lotus)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setColor(resources.getColor(R.color.colorPrimary))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(id, builder)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        finish()
        finishAndRemoveTask()
    }
}
