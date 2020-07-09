package com.example.lotus.ui.home

import android.content.Context
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.asura.library.posters.Poster
import com.asura.library.posters.RemoteImage
import com.asura.library.posters.RemoteVideo
import com.asura.library.views.PosterSlider
import com.example.lotus.R
import com.example.lotus.models.MediaData
import com.example.lotus.models.Post
import kotlinx.android.synthetic.main.layout_mainfeed_listitem.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PostFeedAdapter(private val listPost: ArrayList<Post>, val context: Context) : RecyclerView.Adapter<PostFeedAdapter.Holder>() {
    var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.mContext = context;

        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_mainfeed_listitem, parent, false)
        )
    }

    override fun getItemCount(): Int = listPost?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindFeed(listPost[position], context)
        holder.viewALlComment.setOnClickListener {
            if (mContext is HomeActivity) {
                (mContext as HomeActivity).detailPost(listPost[position])
            }
        }

    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val viewALlComment = view.findViewById<TextView>(R.id.viewAllComment)
        var mContext: Context? = null
        private var posterSlider: PosterSlider? = null
        private var postData: Post? = null

        var likeStatus: Boolean? = false
        var likeCount: Int = 0
        var commentCount: Int = 0

        fun bindFeed(post: Post, context: Context){
            itemView.apply {
                postData = post
                mContext = context

                listenLikeIcon(view)
                listenCommentIcon(view)

                val username :TextView = view.findViewById<View>(R.id.textUsernameFeed) as TextView
                val caption : TextView = view.findViewById<View>(R.id.textCaptionFeed) as TextView
                val ava : ImageView = view.findViewById<View>(R.id.imageAvatarFeed) as ImageView
                val comment: TextView = view.findViewById<View>(R.id.textIcCommentFeed) as TextView
                val time: TextView = view.findViewById<View>(R.id.textTimeFeed) as TextView

                username.text = post?.username
//                caption.text = post?.text

                if (commentCount > 0){
                    comment.text = commentCount.toString()
                }else{
                    viewAllComment.visibility = View.GONE
                }

                setMediaPost(view, post?.media, post?.text)
                setProfilePicture(ava, post?.profilePicture.toString())
                setTimePost(time, post?.date)
                setLike(view, post?.like, likeCount)
            }
        }

        private fun setMediaPost(view: View, medias: ArrayList<MediaData>?, text: String?){
            val postText = view.findViewById<CardView>(R.id.cardPostText)
            val postMedia = view.findViewById<RelativeLayout>(R.id.mediaWrap)
            val caption = view.findViewById<RelativeLayout>(R.id.relLayout3)

            posterSlider = view.findViewById(R.id.postSlider)
            val posters: MutableList<Poster> = ArrayList()

            if (medias?.size!! > 0) {
                postText.visibility = View.GONE
                posterSlider?.visibility = View.VISIBLE

                setCaption(view, text)

                for (media in medias) {
                    if (media.type == "image") {
                        posters.add(RemoteImage(media.link))
                    } else if (media.type == "video") {
                        val videoURI = Uri.parse(media.link)
                        posters.add(RemoteVideo(videoURI))
                    }
                }
                posterSlider!!.setPosters(posters)
            } else {
                val tag = view.findViewById<TextView>(R.id.textHashtagFeed)
                val postTextView = view.findViewById<TextView>(R.id.textStatusDetail)

                postText.visibility = View.VISIBLE
                postMedia.visibility = View.GONE
                caption.visibility = View.GONE

                if (text?.length!! > 249) {
                    Log.d("With More", postData?.tag.toString())
                    val cutCaption = text?.removeRange(249, text.length)
                    val caption = "$cutCaption... more"
                    val spannableString = SpannableString(caption)
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(p0: View) {
                            if (mContext is HomeActivity) {
                                postData?.let { (mContext as HomeActivity).detailPost(it) }
                            }
                        }
                    }

                    postTextView.setMovementMethod(LinkMovementMethod.getInstance());
                    spannableString.setSpan(clickableSpan, caption.length - 4, caption.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    postTextView.text = spannableString
                    tag.visibility = View.GONE
                } else {
                    Log.d("No More $text", postData?.tag.toString())
                    setHashTag(tag, postData?.tag)
                    postTextView.text = text
                }

            }
        }

        fun setCaption (view: View, text: String?){
            val tagView = view.findViewById<TextView>(R.id.textHashtagFeed)
            val captionView = view.findViewById<TextView>(R.id.textCaptionFeed)

            if (text?.length!! > 99) {
                val cutCaption = text?.removeRange(99, text.length)
                val caption = "$cutCaption... more"
                val spannableString = SpannableString(caption)
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        if (mContext is HomeActivity) {
                            postData?.let { (mContext as HomeActivity).detailPost(it) }
                        }
                    }
                }

                captionView.setMovementMethod(LinkMovementMethod.getInstance());
                spannableString.setSpan(clickableSpan, caption.length - 4, caption.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                captionView.text = spannableString
                tagView.visibility = View.GONE
            }else{
                setHashTag(tagView, postData?.tag)
                captionView.text = text
            }
        }

        fun setHashTag(view: TextView, tags: ArrayList<String>?){
            if (tags?.size!! > 0 ) {
                var hashTag: String = ""
                var anyMore = false

                for ((i, tag) in tags.withIndex()){
                    hashTag += "#$tag "
                    if (i == 5){
                        val tagMore = "$hashTag... more"
                        val spannableString = SpannableString(tagMore)
                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                if (mContext is HomeActivity) {
                                    postData?.let { (mContext as HomeActivity).detailPost(it) }
                                }
                            }
                        }

                        view.setMovementMethod(LinkMovementMethod.getInstance());
                        spannableString.setSpan(clickableSpan, tagMore.length - 4, tagMore.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        view.text = spannableString
                        anyMore = true
                        break
                    }
                }
                if (!anyMore){
                    view.text = hashTag
                }
            }else{
                Log.d("$view", "GOne sih harusnya")
                view.visibility = View.GONE
            }
        }

        fun setProfilePicture(profpic: ImageView, url: String){
            profpic.load(url){
                transformations(CircleCropTransformation())
            }
        }

        fun setLike(view: View, likeStatus: Boolean?, likeCount: Int){
            val iconLikeTrue = view.findViewById<ImageView>(R.id.icLikeTrueFeed)
            val iconLikeFalse = view.findViewById<ImageView>(R.id.icLikeFalseFeed)
            val textLikeCount = view.findViewById<TextView>(R.id.textIctLikesFeed)

            if (likeStatus == true){
                iconLikeTrue.visibility = View.VISIBLE
                iconLikeFalse.visibility = View.GONE
            }else {
                iconLikeTrue.visibility = View.GONE
                iconLikeFalse.visibility = View.VISIBLE
            }

            textLikeCount.text = likeCount.toString()
        }

        private  fun setTimePost(v: TextView, time: String?){
            val current = Calendar.getInstance();
            var timePost = Calendar.getInstance()
            val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
            val timeRemove = time?.removeRange(19, 23)

            timePost.setTime(sdf.parse(timeRemove))
            val diff: Long = current.getTime().time - timePost.getTime().time

            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            if (seconds < 60 ){
                v.text = "Now"
            }else if(seconds < 61){
                v.text = "$minutes minute ago"
            }else if(minutes < 60){
                v.text = "$minutes minutes ago"
            }else if(minutes < 61){
                v.text = "$hours hour ago"
            }else if(hours < 24){
                v.text = "$hours hours ago"
            }else if(hours < 49){
                v.text = "Yesterday"
            }else {
                var format1 = "dd MMMM yyyy"
                val formatted = format1.format(timePost.getTime());

                v.text = formatted
            }
        }

        fun listenCommentIcon(view: View){
            val commentIcon = view.findViewById<ImageView>(R.id.icCommentFeed)
//            val inputComment = view.findViewById<EditText>(R.id.inputComment)
//
//            commentIcon.setOnClickListener(View.OnClickListener {
//                inputComment.requestFocus()
//                inputComment.setFocusableInTouchMode(true)
//                val imm: InputMethodManager =
//                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm?.showSoftInput(inputComment, InputMethodManager.SHOW_FORCED)
//            })
        }

        fun listenLikeIcon(view: View){
            val likeIcon = view.findViewById<RelativeLayout>(R.id.likeLayoutFeed)
            likeIcon.setOnClickListener {
                if(likeStatus == true){
                    likeStatus = false
                    likeCount--
                    setLike(view, likeStatus, likeCount)
//                Add logic to hit end point like
                }else {
                    likeStatus = true
                    likeCount++
                    setLike(view, likeStatus, likeCount)
//                Add logic to hit endpont dislike
                }
            }
        }
    }

}