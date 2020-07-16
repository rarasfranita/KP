package com.example.lotus.ui.detailpost

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.AndroidNetworking.get
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.androidnetworking.interfaces.DownloadProgressListener
import com.androidnetworking.interfaces.ParsedRequestListener
import com.asura.library.posters.Poster
import com.asura.library.posters.RemoteImage
import com.asura.library.posters.RemoteVideo
import com.asura.library.views.PosterSlider
import com.example.lotus.R
import com.example.lotus.models.*
import com.example.lotus.service.EnvService
import com.example.lotus.ui.CreatePostActivity
import com.example.lotus.utils.dateToFormatTime
import com.example.lotus.utils.dislikePost
import com.example.lotus.utils.likePost
import com.example.lotus.utils.token
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_detail_post.*
import kotlinx.android.synthetic.main.layout_detail_post.view.*
import matrixsystems.nestedexpandablerecyclerview.RowAdapter


class DetailPost : Fragment() {
    private val TAG = "[DetailPost] [Fragment]"
    val userID = "5f0466e2e524346040f53178"
    lateinit var rowAdapter: RowAdapter
    lateinit var rows : MutableList<CommentRowModel>
    lateinit var recyclerView : RecyclerView
    private var posterSlider: PosterSlider? = null
    private var commentID: String? = null
    var postData: Post? = null
    var likeStatus: Int? = 0
    var likeCount: Int = 0
    var commentCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_detail_post, container, false)

        val bundle = this.arguments
        if (bundle != null) {
            postData = bundle.getParcelable("data")
        }

        likeStatus = postData?.liked
        likeCount = postData?.likesCount!!
        commentCount = postData?.commentsCount!!

        setView(v)
        initRecyclerView(v)
        sendComment(v)
        listenCommentIcon(v)
        listenRepostIcon(v)
        listenLikeIcon(v)
        listenMenuPost(v)

        return v
    }

    fun downloadMedia(medias: ArrayList<MediaData>){
        val downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        for ((i, media) in medias.withIndex()) {
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
                    ) {}
                })
                .startDownload(object : DownloadListener {
                    override fun onDownloadComplete() {
                        if (i.equals(medias.size-1)){
                            Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(error: ANError?) {
                        // handle error
                        Toast.makeText(context, "Error while downloading media, ${error!!.errorDetail}", Toast.LENGTH_SHORT).show()
                        Log.d("Error download", error?.errorCode.toString())
                        Log.d("Error download", error!!.errorDetail)
                    }
                })
        }
    }

    fun showDialog() {
        val medias = postData?.media
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_menu_post)
        val download = dialog.findViewById<LinearLayout>(R.id.downloadMedia)
        download.setOnClickListener {
            if (medias?.size!! < 1){
                Toast.makeText(context, "No media to be downloaded", Toast.LENGTH_SHORT).show()
            }else{
                downloadMedia(medias!!)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun sendComment(v: View){
        val btnSend = v.findViewById<View>(R.id.imageSendComment)
        btnSend.setOnClickListener{
            val s = v.inputComment.text
            closeEditTextComment()

            if (commentID == null){
                AndroidNetworking.post(EnvService.ENV_API + "/posts/{postID}/comments")
                    .addHeaders("Authorization", "Bearer " + token)
                    .addPathParameter("postID", postData?.postId)
                    .addBodyParameter("userId", userID)
                    .addBodyParameter("text", s.toString())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsObject(
                        Respon::class.java,
                        object : ParsedRequestListener<Respon> {
                            override fun onResponse(respon: Respon) {
                                if (respon.code.toString() == "200") {
                                    Log.d("Add Comment: ", "Success")
                                    v.inputComment.setText("")
                                    populateCommentData()
                                }else {
                                    Log.e("ERROR!!!", "Add Comment Data ${respon.code}")
                                }
                            }

                            override fun onError(anError: ANError) {
                                Log.e("ERROR!!!", "Add Comment ${anError.errorCode}")

                            }
                        })
            }else {
                AndroidNetworking.post(EnvService.ENV_API + "/posts/{postID}/comments/{commentID}/replies")
                    .addHeaders("Authorization", "Bearer " + token)
                    .addPathParameter("postID", postData?.postId)
                    .addPathParameter("commentID", commentID)
                    .addBodyParameter("userId", userID)
                    .addBodyParameter("text", s.toString())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsObject(
                        Respon::class.java,
                        object : ParsedRequestListener<Respon> {
                            override fun onResponse(respon: Respon) {
                                if (respon.code.toString() == "200") {
                                    Log.d("Add Comment: ", "Success")
                                    v.inputComment.setText("")
                                    commentID = null
                                    populateCommentData()
                                }else {
                                    Log.e("ERROR!!!", "Add Comment Data ${respon.code}")
                                }
                            }

                            override fun onError(anError: ANError) {
                                Log.e("ERROR!!!", "Add Comment ${anError.errorCode}")

                            }
                        })
            }
        }
    }

    fun listenMenuPost(view: View){
        val menu = view.findViewById<ImageView>(R.id.menuPost)

        menu.setOnClickListener {
            showDialog()
        }
    }

    fun setCommentID(id: String){
        this.commentID = id
    }

    fun listenCommentIcon(view: View){
        val commentIcon = view.findViewById<ImageView>(R.id.icCommentPost)
        commentIcon.setOnClickListener(View.OnClickListener {
            openEditTextComment(view)
        })
    }

    fun closeEditTextComment(){
        val inputManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().getCurrentFocus()?.getWindowToken(),
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    fun openEditTextComment(view: View){
        val inputComment = view.findViewById<EditText>(R.id.inputComment)

        inputComment.requestFocus()
        inputComment.setFocusableInTouchMode(true)
        val imm: InputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.showSoftInput(inputComment, InputMethodManager.SHOW_FORCED)
    }

    fun listenLikeIcon(view: View){
        val likeIcon = view.findViewById<RelativeLayout>(R.id.likeLayoutPost)
        likeIcon.setOnClickListener {
            if(likeStatus.toString() == "1"){
                dislikePost(postData?.postId.toString(), postData?.belongsTo.toString())
                likeStatus = 0
                likeCount--
                setLike(view, likeStatus, likeCount)
            }else {
                likePost(postData?.postId.toString(), postData?.belongsTo.toString())
                likeStatus = 1
                likeCount++
                setLike(view, likeStatus, likeCount)
            }
        }
    }

    fun listenRepostIcon(view: View){
        val repostIcon = view.findViewById<ImageView>(R.id.icSharePost)
        repostIcon.setOnClickListener{
            val intent = Intent(this.activity, CreatePostActivity::class.java)

            intent.putExtra("Extra", "DetailPost")
            intent.putExtra("Media", postData?.media)
            intent.putExtra("Text", postData?.text)
            intent.putExtra("postID", postData?.postId)
            intent.putExtra("Username", postData?.username)
            intent.putExtra("Tags", postData?.tag)
            startActivity(intent)
        }
    }

    fun setView(view: View){
        val username :TextView = view.findViewById<View>(R.id.textUsernamePost) as TextView
        val caption : TextView = view.findViewById<View>(R.id.textCaption) as TextView
        val ava : ImageView = view.findViewById<View>(R.id.imageAvatarPost) as ImageView
        val comment: TextView = view.findViewById<View>(R.id.textIcCommentPost) as TextView
        val time: TextView = view.findViewById<View>(R.id.textTimePost) as TextView

        username.text = postData?.username

        if (postData?.text?.length!! < 1){
            caption.visibility = View.GONE
        }else {
            caption.text = postData?.text
        }

        if (commentCount > 0){
            comment.text = commentCount.toString()
        }

        setMediaPost(view, postData?.media, postData?.text)
        setProfilePicture(ava, postData?.profilePicture)
        dateToFormatTime(time, postData?.date)
        setLike(view, postData?.liked, likeCount)
    }

    private fun setMediaPost(view: View, medias: ArrayList<MediaData>?, text: String?){
        val postText = view.findViewById<CardView>(R.id.cardPostText)
        val postMedia = view.findViewById<RelativeLayout>(R.id.mediaWrap)
        val caption = view.findViewById<RelativeLayout>(R.id.relLayout3)

        posterSlider = view.findViewById(R.id.mediaPost)
        val posters: MutableList<Poster> = ArrayList()

        if (medias?.size!! > 0) {
            postText.visibility = View.GONE
            posterSlider?.visibility = View.VISIBLE
            val tagCaption = view.findViewById<TextView>(R.id.textHashtag)
            setHashTag(tagCaption, postData?.tag)
            for (media in medias) {
                if (media.type == getString(R.string.image)) {
                    posters.add(RemoteImage(media.link))
                } else if (media.type == getString(R.string.video)) {
                    val videoURI = Uri.parse(media.link)
                    posters.add(RemoteVideo(videoURI))
                }
            }
            posterSlider!!.setPosters(posters)
        } else {
            val tagCaption = view.findViewById<TextView>(R.id.textHashtagPost)
            setHashTag(tagCaption, postData?.tag)

            postText.visibility = View.VISIBLE
            postMedia.visibility = View.GONE
            caption.visibility = View.GONE
            val postTextView = view.findViewById<TextView>(R.id.textStatusDetail)
            postTextView.text = text
        }
    }

    private fun setProfilePicture(v: ImageView, image: String?){
        if (image != null) {
            v.load(image){
                crossfade(true)
                crossfade(300)
                transformations(CircleCropTransformation())
            }
        }
    }

    fun setLike(view: View, likeStatus: Int?, likeCount: Int){
        val iconLikeTrue = view.findViewById<ImageView>(R.id.icLikeTrue)
        val iconLikeFalse = view.findViewById<ImageView>(R.id.icLikeFalse)
        val textLikeCount = view.findViewById<TextView>(R.id.textIctLikesPost)

        if (likeStatus.toString() == "1"){
            iconLikeTrue.visibility = View.VISIBLE
            iconLikeFalse.visibility = View.GONE
        }else {
            iconLikeTrue.visibility = View.GONE
            iconLikeFalse.visibility = View.VISIBLE
        }

        textLikeCount.text = likeCount.toString()
    }

    fun setHashTag(view: TextView, tags: ArrayList<String>?){
        if (tags?.size!! > 0) {
            var hashTag: String = ""
            for (tag in tags){
                hashTag += "#$tag "
            }
            view.text = hashTag
        }else{
            view.visibility = View.GONE
        }
    }

    private fun initRecyclerView(v: View){
        recyclerView = v.findViewById(R.id.recycler_view)
        rows = mutableListOf()
        val context: Context = this.requireContext()
        rowAdapter = RowAdapter(context, rows, this)

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        recyclerView.adapter = rowAdapter

        populateCommentData()
    }

    fun populateCommentData(){
        get(EnvService.ENV_API + "/posts/{postID}/comments/all")
            .addHeaders("Authorization", "Bearer " + token)
            .addPathParameter("postID", postData?.postId)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsObject(
                Respon::class.java,
                object : ParsedRequestListener<Respon> {
                    override fun onResponse(respon: Respon) {
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            Log.d("Get Comment", "Success")
                            val strRes = gson.toJson(respon.data)
                            val data = gson.fromJson(strRes, PostWithComment::class.java)
                            val comments = data.comments

                            rows.clear()
                            commentCount = data.commentsCount!!
                            textIcCommentPost.text = commentCount.toString()

                            for (comment in comments){
                                var child1 : MutableList<ChildComment> = mutableListOf()
                                for (reply in comment.replies!!){
                                    child1.add(ChildComment(reply.id, reply.parentId, reply.userId, reply.text, reply.username, reply.profilePicture, reply.createdAt, reply.name))
                                }
                                rows.add((CommentRowModel(
                                    CommentRowModel.PARENT,
                                    Comment(
                                        comment.id,
                                        comment.parentId,
                                        comment.userId,
                                        comment.text,
                                        comment.username,
                                        comment.profilePicture,
                                        comment.createdAt,
                                        comment.name,
                                        child1
                                    )
                                )))

                                rowAdapter.notifyDataSetChanged()
                            }

                        }else {
                            Log.e("ERROR!!!", "Get Comment Data ${respon.code}")
                        }
                    }

                    override fun onError(anError: ANError) {
                        Log.e("ERROR!!!", "Like Post ${anError.errorCode}")

                    }
                })
    }
}