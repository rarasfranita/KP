package com.example.lotus.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.MediaData
import com.example.lotus.models.MediaPost
import com.example.lotus.models.Respon
import com.example.lotus.service.EnvService
import com.example.lotus.ui.createpost.AddHashtag
import com.example.lotus.ui.createpost.CallbackListener
import com.example.lotus.ui.home.HomeActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_create_post.*
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow


class CreatePostActivity : AppCompatActivity(), CallbackListener {
    val TAG = "CreatePost Activity"
    val REQUEST_VIDEO_CAPTURE = 1
    lateinit var alertDialog: AlertDialog
    private val username = "testaccount1"
    private val token = "5f02b3ac10032c371426b525"

    private val mediaPostDatas: ArrayList<MediaPost> = ArrayList()
    private var mediaRepostDatas: ArrayList<MediaData> = ArrayList()
    private var tags: ArrayList<String> = ArrayList()
    private var tagsStringShow: String = ""
    private var tagStringSend: String = ""
    private var repost: Boolean = false
    private var postId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        // Hide preview media because data is NULL
        rvPreviewPostMedia.visibility = View.GONE

        // Hide hashtag because data is NULL
        textHashtag.visibility = View.GONE
        llProgres.visibility = View.GONE

        toolBarListener()
        initRecyclerView()
        setRepostContent(mediaRepostDatas)
        posting()
        editHashtag()
    }

    override fun onResume() {
        super.onResume()

        val extra = intent.getStringExtra("Extra")
        val text = intent.getStringExtra("Text")
        val username = intent.getStringExtra("Username")
        val media = intent.getParcelableArrayListExtra<MediaData>("Media")
        val hashtag = intent.getStringArrayListExtra("Tags")
        postId = intent.getStringExtra("PostId").toString()

        if (hashtag != null) {
            tags = hashtag
        }

        if (extra != null){
            repost = true
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
                for ((i, tag) in tags.withIndex()){
                    tagsStringShow += "#$tag "
                    if (i < tags.size - 1){
                        tagStringSend +="$tag,"
                    }else
                        tagStringSend += tag
                }
                textHashtagRepost.text = tagsStringShow
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

    fun selectPhoto(view: View) {
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
//             .setType("video/*")
//             .putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024)
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

            val data = intent?.data
            val path = getPath(data)!!
            val file = File(path)

            if (file.length() > 104666320) {
                Toast.makeText(this, "Video size is too big!!!", Toast.LENGTH_SHORT).show()
            } else {
                val downloadsPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val desFile = File(downloadsPath, "${System.currentTimeMillis()}_${file.name}")
                if (desFile.exists()) {
                    desFile.delete()
                    try {
                        desFile.createNewFile()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

                var time = 0L

                VideoCompressor.start(
                    path,
                    desFile.path,
                    object : CompressionListener {
                        override fun onProgress(percent: Float) {
                            textProgress.text = "${percent.toLong()}%"
                        }

                        override fun onStart() {
                            llProgres.visibility = View.VISIBLE
                            time = System.currentTimeMillis()
                            Log.d("Original size:", " ${getFileSize(file.length())}")
                        }

                        override fun onSuccess() {
                            llProgres.visibility = View.GONE

                            initRecyclerView()
                            val newSizeValue = desFile.length()

                            Log.d("Size after compression: ", "${getFileSize(newSizeValue)}")

                            time = System.currentTimeMillis() - time
                            Log.d("Duration: ", "${DateUtils.formatElapsedTime(time / 1000)}")

                            var uri = Uri.parse(desFile.toString())

                            var mediaPostData: MediaPost = MediaPost(uri, getString(R.string.video) )
                            mediaPostDatas.add(mediaPostData)
                        }

                        override fun onFailure() {
                            Log.d("Failure", "This video cannot be compressed!")
                        }

                        override fun onCancelled() {
                            Log.wtf("TAG", "compression has been cancelled")
                            // make UI changes, cleanup, etc
                        }
                    }, VideoQuality.MEDIUM, isMinBitRateEnabled = false
                )
            }

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

    fun getPath(uri: Uri?): String? {
        val projection =
            arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri!!, projection, null, null, null)
        return if (cursor != null) {
            val column_index: Int = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else null
    }

    fun dismissDialog(view: View) {
        alertDialog.dismiss()
    }

    private fun showDialog() {
        val dialogFragment = AddHashtag(this)
        val bundle = Bundle()
        bundle.putStringArrayList("Tag", tags)
        dialogFragment.arguments = bundle
        dialogFragment.show(supportFragmentManager, "signature")
    }

    fun getFileSize(size: Long): String {
        if (size <= 0)
            return "0"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()

        return DecimalFormat("#,##0.#").format(
            size / 1024.0.pow(digitGroups.toDouble())
        ) + " " + units[digitGroups]
    }

    override fun onDataReceived(data: String) {
        val tag1 = findViewById<TextView>(R.id.textHashtag)
        val tag2 = findViewById<TextView>(R.id.textHashtagRepost)
        val arrTags = data.split(",").toTypedArray()

        tagStringSend = data
        tags = arrayListOf()

        for (tag in arrTags){
            if (tag != ""){
                tagsStringShow += "#$tag "
                tags.add(tag)
            }
        }

        tag1.visibility = View.VISIBLE
        tag1.text = tagsStringShow
        tag2.text = tagsStringShow
    }

    fun editHashtag(){
        addTagButton.setOnClickListener{
            showDialog()
        }

        addTagButtonRepost.setOnClickListener{
            showDialog()
        }
    }

    fun posting() {
        val postButton = findViewById<ImageView>(R.id.icPosting)
        val caption = findViewById<TextView>(R.id.textCaption)
        postButton.setOnClickListener{
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Loading...")
            progressDialog.show()
            if (mediaPostDatas.size <= 0 && caption.text.length < 1){
                Toast.makeText(this, R.string.alertNoDataPost, Toast.LENGTH_SHORT).show()
            }else{
                if (repost){
                    val uploadRepost = AndroidNetworking.post(EnvService.ENV_API + "/posts/{username}")
                    if (tagStringSend != ""){
                        uploadRepost.addBodyParameter("tag", tagStringSend)
                    }
                    uploadRepost
                        .addPathParameter("username", username)
                        .addBodyParameter("text", caption.text.toString())
                        .addHeaders("Authorization", "Bearer " + token)
                        .setTag(this)
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsObject(
                            Respon::class.java,
                            object : ParsedRequestListener<Respon> {
                                override fun onResponse(res: Respon) {
                                    progressDialog.dismiss()
                                    val gson = Gson()
                                    if (res.code.toString() == "200") {
                                        val intent = Intent(this@CreatePostActivity, HomeActivity::class.java)
                                        startActivity(intent)
                                    }else {
                                        Toast.makeText(applicationContext, "Error ${res.code}", Toast.LENGTH_SHORT)
                                    }
                                }

                                override fun onError(anError: ANError) {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        applicationContext,
                                        "Error ${anError.errorDetail}",
                                        Toast.LENGTH_SHORT
                                    )
                                }
                            })
                }else{
                    val uploadPost =  AndroidNetworking.upload(EnvService.ENV_API + "/posts/{username}")

                    for (media in mediaPostDatas){
                        val auxFile = File(media.mediaPath.path)
                        uploadPost.addMultipartFile("media",  auxFile)
                    }

                    if (tagStringSend != ""){
                        uploadPost.addMultipartParameter("tag", tagStringSend )
                    }

                    uploadPost
                        .addPathParameter("username", username)
                        .addMultipartParameter("text", caption.text.toString())
                        .addHeaders("Authorization", "Bearer " + token)
                        .setTag(this)
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsObject(
                            Respon::class.java,
                            object : ParsedRequestListener<Respon> {
                                override fun onResponse(res: Respon) {
                                    progressDialog.dismiss()
                                    val gson = Gson()
                                    if (res.code.toString() == "200") {
                                        val intent = Intent(this@CreatePostActivity, HomeActivity::class.java)
                                        startActivity(intent)
                                    }else {
                                        Log.d("ERROR!!!", res.code.toString())
                                        Toast.makeText(applicationContext, "Error ${res.code}", Toast.LENGTH_SHORT)
                                    }
                                }

                                override fun onError(anError: ANError) {
                                    progressDialog.dismiss()
                                    Log.d("ERROR code", anError.errorCode.toString())
                                    Log.d("ERROR code", anError.errorDetail.toString())
                                    Toast.makeText(
                                        applicationContext,
                                        "Error ${anError.errorDetail}",
                                        Toast.LENGTH_SHORT
                                    )
                                }
                            })
                }
            }
        }
    }

}