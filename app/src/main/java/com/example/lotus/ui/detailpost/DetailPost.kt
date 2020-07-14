package com.example.lotus.ui.detailpost

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
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
import com.example.lotus.ui.CreatePostActivity
import com.example.lotus.utils.setTimePost
import kotlinx.android.synthetic.main.layout_detail_post.view.*
import matrixsystems.nestedexpandablerecyclerview.RowAdapter


class DetailPost : Fragment() {
    private val TAG = "[DetailPost] [Fragment]"

    lateinit var rowAdapter: RowAdapter
    lateinit var rows : MutableList<CommentRowModel>
    lateinit var recyclerView : RecyclerView
    private var posterSlider: PosterSlider? = null

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

        return v
    }

    fun listenCommentIcon(view: View){
        val commentIcon = view.findViewById<ImageView>(R.id.icCommentPost)
        val inputComment = view.findViewById<EditText>(R.id.inputComment)

        commentIcon.setOnClickListener(View.OnClickListener {
            inputComment.requestFocus()
            inputComment.setFocusableInTouchMode(true)
            val imm: InputMethodManager =
                activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.showSoftInput(inputComment, InputMethodManager.SHOW_FORCED)
        })
    }

    fun listenLikeIcon(view: View){
        val likeIcon = view.findViewById<RelativeLayout>(R.id.likeLayoutPost)
        likeIcon.setOnClickListener {
            if(likeStatus.toString() == "1"){
                likeStatus = 0
                likeCount--
                setLike(view, likeStatus, likeCount)
//                Add logic to hit end point like
            }else {
                likeStatus = 1
                likeCount++
                setLike(view, likeStatus, likeCount)
//                Add logic to hit endpont dislike
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
        caption.text = postData?.text

        if (commentCount > 0){
            comment.text = commentCount.toString()
        }

        setMediaPost(view, postData?.media, postData?.text)
        setProfilePicture(ava, postData?.profilePicture)
        setTimePost(time, postData?.date)
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
        if (tags != null) {
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
        rowAdapter = RowAdapter(context, rows)

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        recyclerView.adapter = rowAdapter

        populateData()
    }

    fun sendComment(v: View){
        val btnSend = v.findViewById<View>(R.id.imageSendComment)
        val s = v.inputComment.text
        btnSend.setOnClickListener{
            Log.d("Comment: ", s.toString())
        }
    }

    fun populateData(){ //For sample comment section
        var child1 : MutableList<ChildComment> = mutableListOf()
        child1.add(ChildComment("Hello", "aduh", "hhhhh", "1 second ago", "100"))
        child1.add(ChildComment("gatu", "aduh", "hhhhh", "1 second ago", "23"))
        child1.add(ChildComment("kenapa", "aduh", "hhhhh", "1 second ago", "4000"))
        child1.add(ChildComment("pusing", "aduh", "hhhhh", "1 second ago", "1"))


        rows.add(CommentRowModel(CommentRowModel.PARENT, Comment("Hello world", "aduhduh", "helo", "1 second", "10k", child1)))
        rows.add(CommentRowModel(CommentRowModel.PARENT, Comment("Ih aplikasinya bagus banget suka deh", "rhmdnrhuda", "helo", "1 second", "1", child1)))

        rowAdapter.notifyDataSetChanged()
    }
}