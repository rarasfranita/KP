package com.example.lotus.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lotus.R
import com.example.lotus.models.MediaData
import com.example.lotus.models.MediaPost
import com.example.lotus.ui.createpost.AddHashtag
import com.example.lotus.ui.home.HomeActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_create_post.*


class CreatePost : AppCompatActivity() {
    val TAG = "CreatePost Activity"
    val REQUEST_VIDEO_CAPTURE = 1
    lateinit var alertDialog: AlertDialog

    private val mediaPostDatas: ArrayList<MediaPost> = ArrayList()
    private var mediaRepostDatas: ArrayList<MediaData> = ArrayList()
    private var tags: ArrayList<String> = ArrayList()
    private var tagsString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        // Hide preview media because data is NULL
        rvPreviewPostMedia.visibility = View.GONE

        // Hide hashtag because data is NULL
        textHashtag.visibility = View.GONE

        toolBarListener()
        initRecyclerView()
        setRepostContent(mediaRepostDatas)
        sendPost()
        editHashtag()
    }

    override fun onResume() {
        super.onResume()

        val extra = intent.getStringExtra("Extra")
        val text = intent.getStringExtra("Text")
        val postId = intent.getStringExtra("PostId")
        val username = intent.getStringExtra("Username")
        val media = intent.getParcelableArrayListExtra<MediaData>("Media")
        val hashtag = intent.getStringArrayListExtra("Tags")
        if (hashtag != null) {
            tags = hashtag
        }

        if (extra != null){
            textCaption.setFilters(arrayOf<InputFilter>(LengthFilter(700 - text!!.length)))
            createPostBottom.visibility = View.GONE

            postingTitle.text = getString(R.string.repost)
            textCaptionRepost.text = "Repost from @$username \n $text"
            if (media?.size!! > 0){
                setRepostContent(media)
            }else{
                rvPreviewRepostMedia.visibility = View.GONE
            }
            if (tags?.size!! > 0 ){
                for (tag in tags){
                    tagsString += "#$tag "
                }
                textHashtagRepost.text = tagsString
            }else{
                textHashtagRepost.visibility = View.GONE
            }
        } else {
            repostBottom.visibility = View.GONE
        }

    }

    fun setRepostContent(data: ArrayList<MediaData>) {
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView = findViewById<RecyclerView>(R.id.rvPreviewRepostMedia)
        recyclerView.layoutManager = layoutManager
        val adapter = com.example.lotus.ui.createpost.PreviewRepostAdapter(data)
        recyclerView.adapter = adapter
    }

    private fun initRecyclerView() {
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
        dialogBuilder.setTitle(getString(R.string.choose))
        alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        initRecyclerView()

        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            // Show Preview Media
            if (rvPreviewPostMedia.visibility == View.GONE){
                rvPreviewPostMedia.visibility = View.VISIBLE
            }

            val videoUri: Uri? = intent?.data
            var mediaPostData: MediaPost = MediaPost(videoUri!!, getString(R.string.video) )
            mediaPostDatas.add(mediaPostData)
        }else if (resultCode == Activity.RESULT_OK) {
            // Show Preview Media
            if (rvPreviewPostMedia.visibility == View.GONE){
                rvPreviewPostMedia.visibility = View.VISIBLE
            }

            val uri = intent?.data
            var mediaPostData: MediaPost = MediaPost(uri!!, getString(R.string.image))
            mediaPostDatas.add(mediaPostData)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(intent), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.task_cancelled), Toast.LENGTH_SHORT).show()
        }
    }

    fun dismissDialog(view: View) {
        alertDialog.dismiss()
    }

    fun editHashtag(){
        addTagButton.setOnClickListener{
            val addHashtag = AddHashtag()
            val manager = getSupportFragmentManager()

            createPost.visibility = View.GONE

            manager?.beginTransaction()
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                ?.replace(R.id.createPost, addHashtag)
                ?.commit()
        }
    }

    fun sendPost() {
        val postButton = findViewById<ImageView>(R.id.icPosting)
        val caption = findViewById<TextView>(R.id.textCaption)
        postButton.setOnClickListener{
            if (mediaPostDatas.size <= 0 && caption.text.length < 1){
                Toast.makeText(this, R.string.alertNoDataPost, Toast.LENGTH_SHORT).show()
            }
        }
    }

}