package com.example.lotus.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Handler
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
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.CreatePostActivity
import com.example.lotus.utils.dateToFormatTime
import com.example.lotus.utils.dislikePost
import com.example.lotus.utils.likePost
import kotlinx.android.synthetic.main.layout_mainfeed_listitem.view.*
import kotlinx.android.synthetic.main.progress_loading.view.*


class PostFeedAdapter(private var listPost: ArrayList<Post>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val token = SharedPrefManager.getInstance(context).token.token
    val userID = SharedPrefManager.getInstance(context).user._id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constant.VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_mainfeed_listitem, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.progress_loading, parent, false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                view.progressbar.indeterminateDrawable.colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
            } else {
                view.progressbar.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
            }
            LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int = listPost?.size

    fun addData(dataViews: ArrayList<Post>) {
        this.listPost.addAll(dataViews)
        notifyDataSetChanged()
    }

    fun addLoadingView() {
        Handler().post {
            var post: Post = listPost[0]
            listPost.add(post)
            notifyItemInserted(listPost.size - 1)
        }
    }

    fun removeLoadingView() {
        //Remove loading item
        if (listPost.size != 0) {
            listPost.removeAt(listPost.size - 1)
            notifyItemRemoved(listPost.size)
        }
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val comment = view.findViewById<RelativeLayout>(R.id.rlCommentFeed)
        val menu = view.findViewById<ImageView>(R.id.menuFeed)
        var mContext: Context? = null
        private var posterSlider: PosterSlider? = null
        private var postData: Post? = null
        private var token: String = null.toString()
        private var userID: String = null.toString()

        var likeStatus: Int? = 0
        var likeCount: Int = 0
        var commentCount: Int = 0

        fun setToken(token: String){
            this.token = token
        }

        fun setUserID(userID: String){
            Log.d("Userid di setuserid", userID)
            this.userID = userID
        }

        fun bindFeed(post: Post, context: Context){
            itemView.apply {
                postData = post
                mContext = context

                listenLikeIcon(view)

                val username :TextView = view.findViewById<View>(R.id.textUsernameFeed) as TextView
                val caption : TextView = view.findViewById<View>(R.id.textCaptionFeed) as TextView
                val ava : ImageView = view.findViewById<View>(R.id.imageAvatarFeed) as ImageView
                val comment: TextView = view.findViewById<View>(R.id.textIcCommentFeed) as TextView
                val time: TextView = view.findViewById<View>(R.id.textTimeFeed) as TextView

                username.text = post?.username
                likeCount = post?.likesCount!!
                likeStatus = post?.liked
                commentCount = post?.commentsCount!!

                if (commentCount > 0){
                    comment.text = commentCount.toString()
                }else{
                    viewAllComment.visibility = View.GONE
                }

                setMediaPost(view, post?.media, post?.text)
                setProfilePicture(ava, post?.profilePicture.toString())
                dateToFormatTime(time, post?.date)
                setLike(view, post?.liked, likeCount)
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
                    val cutCaption = text?.removeRange(249, text.length)
                    val caption = "$cutCaption... more"
                    val spannableString = SpannableString(caption)
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(p0: View) {
                            if (mContext is HomeActivity) {
                                postData?.let { (mContext as HomeActivity).gotoDetailPost(it) }
                            }
                        }
                    }

                    postTextView.setMovementMethod(LinkMovementMethod.getInstance());
                    spannableString.setSpan(clickableSpan, caption.length - 4, caption.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    postTextView.text = spannableString
                    tag.visibility = View.GONE
                } else {
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
                            postData?.let { (mContext as HomeActivity).gotoDetailPost(it) }
                        }
                    }
                }

                captionView.setMovementMethod(LinkMovementMethod.getInstance());
                spannableString.setSpan(clickableSpan, caption.length - 4, caption.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                captionView.text = spannableString
                tagView.visibility = View.GONE
            }else{
                if (text?.length!! < 1){
                    captionView.visibility = View.GONE
                }
                val tagCaption = view.findViewById<TextView>(R.id.textHashtag2)
                setHashTag(tagCaption, postData?.tag)
                captionView.text = text
            }
        }

        fun setHashTag(view: TextView, tags: ArrayList<String>?){
            if (tags?.size!! > 0) {
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
                                    postData?.let { (mContext as HomeActivity).gotoDetailPost(it) }
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
                view.visibility = View.GONE
            }
        }

        fun setProfilePicture(profpic: ImageView, url: String){
            profpic.load(url){
                transformations(CircleCropTransformation())
            }
        }

        fun setLike(view: View, likeStatus: Int?, likeCount: Int){
            val iconLikeTrue = view.findViewById<ImageView>(R.id.icLikeTrueFeed)
            val iconLikeFalse = view.findViewById<ImageView>(R.id.icLikeFalseFeed)
            val textLikeCount = view.findViewById<TextView>(R.id.textIctLikesFeed)

            if (likeStatus.toString() == "1"){
                iconLikeTrue.visibility = View.VISIBLE
                iconLikeFalse.visibility = View.GONE
            }else {
                iconLikeTrue.visibility = View.GONE
                iconLikeFalse.visibility = View.VISIBLE
            }

            textLikeCount.text = likeCount.toString()
        }

        fun listenLikeIcon(view: View){
            val likeIcon = view.findViewById<RelativeLayout>(R.id.likeLayoutFeed)
            likeIcon.setOnClickListener {
                if(likeStatus.toString() == "1"){
                    dislikePost(postData?.postId.toString(), userID.toString(), token)
                    likeStatus = 0
                    likeCount--
                    setLike(view, likeStatus, likeCount)
                }else {
                    likePost(postData?.postId.toString(), userID.toString(), token)
                    likeStatus = 1
                    likeCount++
                    setLike(view, likeStatus, likeCount)
                }
            }
        }
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val item = ItemViewHolder(holder.itemView)

            item.setToken(token.toString())
            item.setUserID(userID.toString())
            item.bindFeed(listPost[position], context)


            holder.itemView.viewAllComment.setOnClickListener {
                if (context is HomeActivity) {
                    (context as HomeActivity).gotoDetailPost(listPost[position])
                }
            }

            holder.itemView.rlCommentFeed.setOnClickListener {
                if (context is HomeActivity) {
                    (context as HomeActivity).gotoDetailPost(listPost[position])
                }
            }

            holder.itemView.icSharePost.setOnClickListener {
                val intent = Intent(context, CreatePostActivity::class.java)

                intent.putExtra("Extra", "DetailPost")
                intent.putExtra("Media", listPost[position].media)
                intent.putExtra("Text", listPost[position].text)
                intent.putExtra("PostID", listPost[position].postId)
                intent.putExtra("Username", listPost[position].username)
                intent.putExtra("Tags", listPost[position].tag)

                if (context is HomeActivity) {
                    (context as HomeActivity).startActivity(intent)
                }
            }

            holder.itemView.menuFeed.setOnClickListener {
                if (context is HomeActivity){
                    (context as HomeActivity).showDialog(listPost[position].media!!)
                }
            }

        }
    }
}
