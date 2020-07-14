package com.example.lotus.ui.explore.general.detailpostExplore

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.asura.library.posters.Poster
import com.asura.library.posters.RemoteImage
import com.asura.library.posters.RemoteVideo
import com.asura.library.views.PosterSlider
import com.example.lotus.R
import com.example.lotus.models.*
import com.example.lotus.ui.explore.general.model.Data
import kotlinx.android.synthetic.main.layout_detail_post_notfollow.view.*
import matrixsystems.nestedexpandablerecyclerview.RowAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DetailPosttGeneral : Fragment() {
    private val TAG = "[DetailPostt] [Fragment]"

    lateinit var rowAdapter: RowAdapter
    lateinit var rows: MutableList<CommentRowModel>
    lateinit var recyclerView: RecyclerView
    private var posterSlider: PosterSlider? = null

    var postData: Data? = null

    var likeStatus: Boolean? = false
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
        val v = inflater.inflate(R.layout.fragment_detail_post_notfollow, container, false)

        val bundle = this.arguments
        if (bundle != null) {
            postData = bundle.getParcelable<Data>("data")
        }

        likeStatus = postData?.posts?.get(0)?.like
        likeCount = postData?.posts?.get(0)?.likesCount!!
        commentCount = postData?.posts?.get(0)?.commentsCount!!

        setView(v)
        initRecyclerView(v)
        sendComment(v)
        listenCommentIcon(v)
        listentLikeIcon(v)

        return v
    }

    fun listenCommentIcon(view: View) {
        val commentIcon = view.findViewById<ImageView>(R.id.icCommentGeneral)
        val inputComment = view.findViewById<EditText>(R.id.inputCommentGeneral)

        commentIcon.setOnClickListener(View.OnClickListener {
            inputComment.requestFocus()
            inputComment.setFocusableInTouchMode(true)
            val imm: InputMethodManager =
                activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.showSoftInput(inputComment, InputMethodManager.SHOW_FORCED)
        })
    }

    fun listentLikeIcon(view: View) {
        val likeIcon = view.findViewById<RelativeLayout>(R.id.likeLayoutGeneral)
        likeIcon.setOnClickListener {
            if (likeStatus == true) {
                likeStatus = false
                likeCount--
                setLike(view, likeStatus, likeCount)
//                Add logic to hit end point like
            } else {
                likeStatus = true
                likeCount++
                setLike(view, likeStatus, likeCount)
//                Add logic to hit endpont dislike
            }
        }
    }

    fun setView(view: View) {
        val username: TextView = view.findViewById<View>(R.id.textUsernameGeneral) as TextView
        val caption: TextView = view.findViewById<View>(R.id.textCaptionGeneral) as TextView
        val ava: ImageView = view.findViewById<View>(R.id.imageAvatarGeneral) as ImageView
        val comment: TextView = view.findViewById<View>(R.id.textIcCommentGeneral) as TextView
        val time: TextView = view.findViewById<View>(R.id.textTimeGeneral) as TextView

        username.text = postData?.posts!![0].name
        caption.text = postData?.posts!![0].text


        if (commentCount > 0) {
            comment.text = commentCount.toString()
        }

        setMediaPost(view, postData?.posts!![0].media, postData?.posts!![0].text)
        setProfilePicture(ava, postData?.posts!![0].profilePicture)
        setTimePost(time, postData?.posts!![0].postDate)
        setLike(view, postData?.posts!![0].like, likeCount)

    }

    private fun setMediaPost(view: View, medias: ArrayList<MediaData>?, text: String?) {
        val postText = view.findViewById<CardView>(R.id.cardTextGeneral)
        val postMedia = view.findViewById<RelativeLayout>(R.id.mediaWrapGeneral)
        val caption = view.findViewById<RelativeLayout>(R.id.relLayout3General)

        posterSlider = view.findViewById(R.id.mediaGeneral)
        val posters: MutableList<Poster> = ArrayList()

        if (medias?.size!! > 0) {
            postText.visibility = View.GONE
            posterSlider?.visibility = View.VISIBLE
            val tagCaption = view.findViewById<TextView>(R.id.textHashtagGeneral)
            setHashTag(tagCaption, postData?.posts!![0].tag)
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
            val tagCaption = view.findViewById<TextView>(R.id.textHashtagGeneral)
            setHashTag(tagCaption, postData?.posts!![0].tag)

            postText.visibility = View.VISIBLE
            postMedia.visibility = View.GONE
            caption.visibility = View.GONE
            posterSlider?.visibility = View.GONE
            val postTextView = view.findViewById<TextView>(R.id.textStatusDetailGeneral)
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

    private fun setTimePost(v: TextView, time: String?) {
        val current = Calendar.getInstance();
        var timePost = Calendar.getInstance()
        val sdf: SimpleDateFormat = SimpleDateFormat(getString(R.string.date_format_full))
        val str2 = time?.removeRange(19, 23)
        timePost.setTime(sdf.parse(str2))
        val diff: Long = current.getTime().time - timePost.getTime().time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        if (seconds < 60) {
            v.text = getString(R.string.now)
        } else if (seconds < 61) {
            v.text = "$minutes minute ago"
        } else if (minutes < 60) {
            v.text = "$minutes minutes ago"
        } else if (minutes < 61) {
            v.text = "$hours hour ago"
        } else if (hours < 24) {
            v.text = "$hours hours ago"
        } else if (hours < 49) {
            v.text = getString(R.string.yesterday)
        } else {
            var format1 = SimpleDateFormat(getString(R.string.date_format))
            val formatted = format1.format(timePost.getTime());

            v.text = formatted
        }
    }

    fun setLike(view: View, likeStatus: Boolean?, likeCount: Int) {
        val iconLikeTrue = view.findViewById<ImageView>(R.id.icLikeTrueGeneral)
        val iconLikeFalse = view.findViewById<ImageView>(R.id.icLikeFalseGeneral)
        val textLikeCount = view.findViewById<TextView>(R.id.textIctLikesGeneral)

        if (likeStatus == true) {
            iconLikeTrue.visibility = View.VISIBLE
            iconLikeFalse.visibility = View.GONE
        } else {
            iconLikeTrue.visibility = View.GONE
            iconLikeFalse.visibility = View.VISIBLE
        }

        textLikeCount.text = likeCount.toString()
    }

    fun setHashTag(view: TextView, tags: ArrayList<String>?) {
        if (tags != null) {
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
        recyclerView = v.findViewById(R.id.recycler_viewGeneral)
        rows = mutableListOf()
        val context: Context = this.requireContext()
        rowAdapter = RowAdapter(context, rows)

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        recyclerView.adapter = rowAdapter

        populateData()
    }

    fun sendComment(v: View) {
        val btnSend = v.findViewById<View>(R.id.imageSendCommentGeneral)
        val s = v.inputCommentGeneral.text
        btnSend.setOnClickListener {
            Log.d("Comment: ", s.toString())
        }
    }

    fun populateData() { //For sample comment section
        var child1: MutableList<ChildComment> = mutableListOf()
        child1.add(ChildComment("Hello", "aduh", "hhhhh", "1 second ago", "100"))
        child1.add(ChildComment("gatu", "aduh", "hhhhh", "1 second ago", "23"))
        child1.add(ChildComment("kenapa", "aduh", "hhhhh", "1 second ago", "4000"))
        child1.add(ChildComment("pusing", "aduh", "hhhhh", "1 second ago", "1"))


        rows.add(
            CommentRowModel(
                CommentRowModel.PARENT,
                Comment("Hello world", "aduhduh", "helo", "1 second", "10k", child1)
            )
        )
        rows.add(
            CommentRowModel(
                CommentRowModel.PARENT,
                Comment(
                    "Ih aplikasinya bagus banget suka deh",
                    "rhmdnrhuda",
                    "helo",
                    "1 second",
                    "1",
                    child1
                )
            )
        )

        rowAdapter.notifyDataSetChanged()
    }
}