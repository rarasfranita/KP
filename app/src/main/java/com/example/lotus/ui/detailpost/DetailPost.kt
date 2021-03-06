package com.example.lotus.ui.detailpost

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
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
import com.androidnetworking.interfaces.ParsedRequestListener
import com.asura.library.posters.Poster
import com.asura.library.posters.RemoteImage
import com.asura.library.posters.RemoteVideo
import com.asura.library.views.PosterSlider
import com.example.lotus.R
import com.example.lotus.models.*
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.CreatePostActivity
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.login.LoginActivity
import com.example.lotus.ui.notification.NotificationActivity
import com.example.lotus.ui.profile.ProfileActivity
import com.example.lotus.utils.dateToFormatTime
import com.example.lotus.utils.dislikePost
import com.example.lotus.utils.downloadMedia
import com.example.lotus.utils.likePost
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_detail_post.*
import kotlinx.android.synthetic.main.layout_detail_post.view.*
import matrixsystems.nestedexpandablerecyclerview.RowAdapter


class DetailPost : Fragment() {
    lateinit var rowAdapter: RowAdapter
    lateinit var rows: MutableList<CommentRowModel>
    lateinit var recyclerView: RecyclerView

    private val TAG = "[DetailPost] [Fragment]"
    private var posterSlider: PosterSlider? = null
    private var commentID: String? = null

    var token: String? = null
    var userID: String? = null
    var username: String? = null
    var postData: Post? = null
    var likeStatus: Int? = 0
    var likeCount: Int = 0
    var commentCount: Int = 0
    var postID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_detail_post, container, false)

        val textStatus = v.findViewById<MaterialCardView>(R.id.cardPostText)
        textStatus.visibility = View.INVISIBLE

        token = "5f15088739ed965f5b789819"
//            SharedPrefManager.getInstance(requireContext()).token.token
        userID = SharedPrefManager.getInstance(requireContext()).user._id
        username = SharedPrefManager.getInstance(requireContext()).user.username

        val bundle = this.arguments
        if (bundle != null) {
            postData = bundle.getParcelable("data")
            postID = bundle.getString("postId")
        }

        if (postData != null) {
            setView(v)
        } else if (postID != null) {
            populateCommentData(v, postID!!)
        }

        initRecyclerView(v)

        sendComment(v)
        listenCommentIcon(v)
        listenRepostIcon(v)
        listenLikeIcon(v)
        toolBarListener(v)
        setImageProfileAndListen(v)
        listenAvatarAndUsername(v)
        listenMenu(v)

        return v
    }

    private fun listenAvatarAndUsername(v: View) {
        val avatar = v.imageAvatarPost
        val username = v.textUsernamePost

        avatar.setOnClickListener {
            if (SharedPrefManager.getInstance(requireContext()).isLoggedIn) {
                gotoProfile(postData?.belongsTo.toString())
            } else {
                this.activity!!.startActivity(Intent(context, LoginActivity::class.java))
            }
        }

        username.setOnClickListener {
            if (SharedPrefManager.getInstance(requireContext()).isLoggedIn) {
                gotoProfile(postData?.belongsTo.toString())
            } else {
                this.activity!!.startActivity(Intent(context, LoginActivity::class.java))
            }
        }


    }

    fun gotoProfile(userID: String) {
        val intent = Intent(this.activity, ProfileActivity::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
    }

    private fun setImageProfileAndListen(v: View) {
        val imageSend = v.findViewById<ImageView>(R.id.profileAtCommentSend)
        val profilePicture = SharedPrefManager.getInstance(requireContext()).user.avatar

        if (profilePicture != null) {
            imageSend.load(profilePicture) {
                transformations(CircleCropTransformation())
            }
        }
        imageSend.setOnClickListener {
            if (SharedPrefManager.getInstance(requireContext()).isLoggedIn) {
                val intent = Intent(this.activity, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                this.activity!!.startActivity(Intent(context, LoginActivity::class.java))
            }
        }
    }

    fun sendComment(v: View) {
        val btnSend = v.findViewById<View>(R.id.imageSendComment)
        btnSend.setOnClickListener {
            if (SharedPrefManager.getInstance(requireContext()).isLoggedIn) {
                val s = v.inputComment.text
                if (!s.toString().equals("")) {
                    closeEditTextComment()
                    if (commentID == null) {
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
                                            populateCommentData(v, postData?.postId.toString())
                                        } else {
                                            Log.e("ERROR!!!", "Add Comment Data ${respon.code}")
                                            Log.e("ERROR", "Add Comment: ${respon.data}")
                                        }
                                    }

                                    override fun onError(anError: ANError) {
                                        Log.e("ERROR!!!", "Add Comment ${anError.errorCode}")

                                    }
                                })
                    } else {
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
                                            populateCommentData(v, postData?.postId.toString())
                                        } else {
                                            Log.e("ERROR!!!", "Add Comment Data ${respon.code}")
                                        }
                                    }

                                    override fun onError(anError: ANError) {
                                        Log.e("ERROR!!!", "Add Comment ${anError.errorCode}")

                                    }
                                })
                    }
                }

            } else {
                this.activity!!.startActivity(Intent(context, LoginActivity::class.java))
            }
        }
    }

    fun setCommentID(id: String) {
        this.commentID = id
    }

    fun listenMenu(v: View) {
        val menu = v.findViewById<ImageView>(R.id.menuPost)
        menu.setOnClickListener {
            if (SharedPrefManager.getInstance(requireContext()).isLoggedIn) {
                showDialog()
            } else {
                this.activity!!.startActivity(Intent(context, LoginActivity::class.java))
            }
        }
    }

    fun listenCommentIcon(view: View) {
        val commentIcon = view.findViewById<ImageView>(R.id.icCommentPost)
        commentIcon.setOnClickListener(View.OnClickListener {
            if (SharedPrefManager.getInstance(requireContext()).isLoggedIn) {
                openEditTextComment(view)
            } else {
                this.activity!!.startActivity(Intent(context, LoginActivity::class.java))
            }
        })
    }

    fun closeEditTextComment() {
        val inputManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    fun openEditTextComment(view: View) {
        val inputComment = view.findViewById<EditText>(R.id.inputComment)

        inputComment.requestFocus()
        inputComment.isFocusableInTouchMode = true
        val imm: InputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputComment, InputMethodManager.SHOW_FORCED)
    }

    fun listenLikeIcon(view: View) {
        val likeIcon = view.findViewById<RelativeLayout>(R.id.likeLayoutPost)
        likeIcon.setOnClickListener {
            if (SharedPrefManager.getInstance(requireContext()).isLoggedIn) {
                if (likeStatus.toString() == "1") {
                    dislikePost(
                        postData?.postId.toString(), userID.toString(),
                        token.toString()
                    )
                    likeStatus = 0
                    likeCount--
                    setLike(view, likeStatus, likeCount)
                } else {
                    likePost(
                        postData?.postId.toString(), userID.toString(),
                        token.toString()
                    )
                    likeStatus = 1
                    likeCount++
                    setLike(view, likeStatus, likeCount)
                }
            } else {
                this.activity!!.startActivity(Intent(context, LoginActivity::class.java))
            }
        }
    }

    fun listenRepostIcon(view: View) {
        val repostIcon = view.findViewById<ImageView>(R.id.icSharePost)
        repostIcon.setOnClickListener {
            if (SharedPrefManager.getInstance(requireContext()).isLoggedIn) {
                val intent = Intent(this.activity, CreatePostActivity::class.java)

                intent.putExtra("Extra", "DetailPost")
                intent.putExtra("Media", postData?.media)
                intent.putExtra("Text", postData?.text)
                intent.putExtra("PostID", postData?.postId)
                intent.putExtra("Username", postData?.username)
                intent.putExtra("Tags", postData?.tag)
                startActivity(intent)
            } else {
                this.activity!!.startActivity(Intent(context, LoginActivity::class.java))
            }
        }
    }

    fun setView(view: View) {
        likeStatus = postData?.liked
        likeCount = postData?.likesCount!!
        commentCount = postData?.commentsCount!!
        val username: TextView = view.findViewById<View>(R.id.textUsernamePost) as TextView
        val caption: TextView = view.findViewById<View>(R.id.textCaption) as TextView
        val ava: ImageView = view.findViewById<View>(R.id.imageAvatarPost) as ImageView
        val comment: TextView = view.findViewById<View>(R.id.textIcCommentPost) as TextView
        val time: TextView = view.findViewById<View>(R.id.textTimePost) as TextView

        username.text = postData?.username

        if (postData?.text?.length!! < 1) {
            caption.visibility = View.GONE
        } else {
            caption.text = postData?.text
        }

        if (commentCount > 0) {
            comment.text = commentCount.toString()
        }

        setMediaPost(view, postData?.media, postData?.text)
        setProfilePicture(ava, postData?.profilePicture)
        dateToFormatTime(time, postData?.date)
        setLike(view, postData?.liked, likeCount)
    }

    private fun setMediaPost(view: View, medias: ArrayList<MediaData>?, text: String?) {
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

    private fun setProfilePicture(v: ImageView, image: String?) {
        if (image != null) {
            v.load(image) {
                crossfade(true)
                crossfade(300)
                transformations(CircleCropTransformation())
            }
        }
    }

    fun setLike(view: View, likeStatus: Int?, likeCount: Int) {
        val iconLikeTrue = view.findViewById<ImageView>(R.id.icLikeTrue)
        val iconLikeFalse = view.findViewById<ImageView>(R.id.icLikeFalse)
        val textLikeCount = view.findViewById<TextView>(R.id.textIctLikesPost)

        if (likeStatus.toString() == "1") {
            iconLikeTrue.visibility = View.VISIBLE
            iconLikeFalse.visibility = View.GONE
        } else {
            iconLikeTrue.visibility = View.GONE
            iconLikeFalse.visibility = View.VISIBLE
        }

        textLikeCount.text = likeCount.toString()
    }

    fun setHashTag(view: TextView, tags: ArrayList<String>?) {
        if (tags?.size!! > 0) {
            var hashTag: String = ""
            for (tag in tags) {
                hashTag += "#$tag "
            }
            view.text = hashTag
        } else {
            view.visibility = View.GONE
        }
    }

    private fun initRecyclerView(v: View) {
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

        populateCommentData(v, postData?.postId.toString())
    }

    fun populateCommentData(v: View, id: String) {
        if (SharedPrefManager.getInstance(requireContext()).isLoggedIn) {
            get(EnvService.ENV_API + "/posts/{postID}/comments/all?viewer=$userID")
                .addHeaders("Authorization", "Bearer " + token)
                .addPathParameter("postID", id)
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

                                for (comment in comments) {
                                    var child1: MutableList<ChildComment> = mutableListOf()
                                    for (reply in comment.replies!!) {
                                        child1.add(
                                            ChildComment(
                                                reply.id,
                                                reply.parentId,
                                                reply.userId,
                                                reply.text,
                                                reply.username,
                                                reply.profilePicture,
                                                reply.createdAt,
                                                reply.name
                                            )
                                        )
                                    }
                                    rows.add(
                                        (CommentRowModel(
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
                                        ))
                                    )

                                    rowAdapter.notifyDataSetChanged()
                                }

                                if (postID != null) {
                                    postData = Post(
                                        data.id,
                                        data.username,
                                        data.profilePicture,
                                        data.name,
                                        data.likesCount,
                                        data.commentsCount,
                                        data.views,
                                        data.date,
                                        data.text,
                                        data.liked,
                                        data.postId,
                                        data.belongsTo,
                                        data.tag,
                                        data.media
                                    )
                                    setView(v)
                                }

                            } else {
                                Log.e("ERROR!!!", "Get Comment Data ${respon.code}")
                            }
                        }

                        override fun onError(anError: ANError) {
                            Log.e("ERROR!!!", "Get Comment ${anError.errorCode}")

                        }
                    })
        } else {
            // TODO: 06/08/20 wait for no AUTH from backend
            get(EnvService.ENV_API + "/posts/{postID}/comments/all")
                .addHeaders("Authorization", "Bearer " + token)
                .addPathParameter("postID", id)
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

                                for (comment in comments) {
                                    var child1: MutableList<ChildComment> = mutableListOf()
                                    for (reply in comment.replies!!) {
                                        child1.add(
                                            ChildComment(
                                                reply.id,
                                                reply.parentId,
                                                reply.userId,
                                                reply.text,
                                                reply.username,
                                                reply.profilePicture,
                                                reply.createdAt,
                                                reply.name
                                            )
                                        )
                                    }
                                    rows.add(
                                        (CommentRowModel(
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
                                        ))
                                    )

                                    rowAdapter.notifyDataSetChanged()
                                }

                                if (postID != null) {
                                    postData = Post(
                                        data.id,
                                        data.username,
                                        data.profilePicture,
                                        data.name,
                                        data.likesCount,
                                        data.commentsCount,
                                        data.views,
                                        data.date,
                                        data.text,
                                        data.liked,
                                        data.postId,
                                        data.belongsTo,
                                        data.tag,
                                        data.media
                                    )
                                    setView(v)
                                }

                            } else {
                                Log.e("ERROR!!!", "Get Comment Data ${respon.code}")
                            }
                        }

                        override fun onError(anError: ANError) {
                            Log.e("ERROR!!!", "Get Comment ${anError.errorCode}")

                        }
                    })
        }
    }

    private fun toolBarListener(view: View) {
        val toolbar: Toolbar = view.findViewById(R.id.tbDetailPost) as Toolbar

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

    }

    fun showDialog() {
        val dialog = Dialog(this.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_menu_post)
        val download = dialog.findViewById<LinearLayout>(R.id.downloadMedia)
        val share = dialog.findViewById<LinearLayout>(R.id.sharePost)
        download.setOnClickListener {
            if (SharedPrefManager.getInstance(requireContext()).isLoggedIn) {
                downloadMedia(postData?.media!!, requireContext())
                dialog.dismiss()
            } else {
                this.activity!!.startActivity(Intent(context, LoginActivity::class.java))
            }
        }

        share.setOnClickListener {
            if (SharedPrefManager.getInstance(requireContext()).isLoggedIn) {
                if (postData?.media!!.size < 1) {
                    Toast.makeText(context, "No media to be downloaded", Toast.LENGTH_SHORT).show()
                } else {
                    shareMediaToOtherApp(postData?.media!!)
                    dialog.dismiss()
                }
            } else {
                this.activity!!.startActivity(Intent(context, LoginActivity::class.java))
            }
        }

        dialog.show()
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

}