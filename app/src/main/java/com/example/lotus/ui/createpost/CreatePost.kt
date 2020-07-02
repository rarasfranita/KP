package com.example.lotus.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lotus.R
import com.example.lotus.models.MediaPost
import com.example.lotus.ui.home.HomeActivity
import com.github.dhaval2404.imagepicker.ImagePicker

class CreatePost : AppCompatActivity() {
    val TAG = "CreatePost Activity"
    val REQUEST_VIDEO_CAPTURE = 1
    lateinit var alertDialog: AlertDialog

    private val mediaPostDatas: ArrayList<MediaPost> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        toolBarListener()
        getImages()
    }

    private fun getImages() {
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.")
//        var mediaPostData: MediaPost = MediaPost(/storage/emulated/0/DCIM/Camera/IMG_20200702_090929371.jpg, "image")
//        mediaPostDatas.add(mediaPostData)
//
//        mediaPostData  = MediaPost("//media/external/video/media/55", "video")
//        mediaPostDatas.add(mediaPostData)


        initRecyclerView()
    }

    private fun initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview")
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView = findViewById<RecyclerView>(R.id.rvPreviewPostMedia)
        recyclerView.layoutManager = layoutManager
        val adapter = com.example.lotus.ui.createpost.PreviewPostAdapter(mediaPostDatas)
        recyclerView.adapter = adapter
    }

    private fun toolBarListener(){
        val toolbar: Toolbar = findViewById(R.id.tbCreatePost) as Toolbar

        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    fun uploadPhoto(view: View) {
        ImagePicker.with(this)
            .cropSquare()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    fun recordVideo(view: View) {
        dismissDialog(view)
        Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            .putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
            .also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    fun selectVideo(view: View) {
        dismissDialog(view)
         Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
            .also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    fun showDialog(view: View) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_choose_upload, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Choose")
        alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        initRecyclerView()

        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val videoUri: Uri? = intent?.data
            var mediaPostData: MediaPost = MediaPost(videoUri!!, "video")
            mediaPostDatas.add(mediaPostData)
        }else if (resultCode == Activity.RESULT_OK) {
            val uri = intent?.data
            var mediaPostData: MediaPost = MediaPost(uri!!, "image")
            mediaPostDatas.add(mediaPostData)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(intent), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
        Log.d(TAG, "lenght ${mediaPostDatas.size}")
    }

    fun dismissDialog(view: View) {
        alertDialog.dismiss()
    }

    fun sendPost(view: View) {
        val caption = view.findViewById<View>(R.id.eTextCaption)
        if (mediaPostDatas.size <= 0 && caption == null){
            Toast.makeText(this, R.string.alertNoDataPost, Toast.LENGTH_SHORT).show()
        }
    }

}