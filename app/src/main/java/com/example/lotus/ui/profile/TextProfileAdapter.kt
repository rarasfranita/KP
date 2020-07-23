package com.example.lotus.ui.profile

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.example.lotus.R
import com.example.lotus.models.Post
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.CreatePostActivity
import com.example.lotus.ui.home.HomeActivity
import com.example.lotus.utils.dateToFormatTime
import com.example.lotus.utils.dislikePost
import com.example.lotus.utils.likePost

class TextProfileAdapter (val post: ArrayList<Post>, val context: Context) : RecyclerView.Adapter<TextProfileAdapter.Holder>() {
    val token = SharedPrefManager.getInstance(context).token.token
    val userID = SharedPrefManager.getInstance(context).user._id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_list_text_profile,parent,false)
        )
    }

    override fun getItemCount(): Int = post.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setToken(token.toString())
        holder.setUserID(userID.toString())

        holder.bindFeed(post[position], context)

        holder.icSharePost.setOnClickListener {
            val intent = Intent(context, CreatePostActivity::class.java)

            intent.putExtra("Extra", "DetailPost")
            intent.putExtra("Media", post[position].media)
            intent.putExtra("Text", post[position].text)
            intent.putExtra("PostID", post[position].id)
            intent.putExtra("Username", post[position].username)
            intent.putExtra("Tags", post[position].tag)

            if (context is ProfileActivity) {
                (context as ProfileActivity).startActivity(intent)
            }
        }

//        holder.burger.setOnClickListener {
//            if (context is ProfileActivity){
//                (context as ProfileActivity).showDialog(post[position].media!!)
//            }
//        }
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        var mContext: Context? = null
        var postData: Post? = null
        val avatar = view.findViewById<ImageView>(R.id.avatarProfileText)
        val username = view.findViewById<TextView>(R.id.usernameProfileText)
        val status = view.findViewById<TextView>(R.id.textStatusProfileText)
        val hashtagView = view.findViewById<TextView>(R.id.textHashtagProfileText)
        val textLikeCount = view.findViewById<TextView>(R.id.textTotalLikesProfileText)
        val textCommentCount = view.findViewById<TextView>(R.id.textTotalCommentProfileText)
        val likesIcon = view.findViewById<ImageView>(R.id.icLikeProfileTextTrue)
        val dislikeIcon = view.findViewById<ImageView>(R.id.icLikeProfileTextFalse)
        val commentIcon = view.findViewById<ImageView>(R.id.icCommentProfileText)
        val viewAllCommentProfileText = view.findViewById<TextView>(R.id.viewAllCommentProfileText)
        val timePost = view.findViewById<TextView>(R.id.timePostProfileText)
        val icSharePost = view.findViewById<ImageView>(R.id.icSharePostProfileText)
        val burger = view.findViewById<ImageView>(R.id.burgerProfileText)
        var index = 0
        var likeStatus: Int? = 0
        var likeCount: Int = 0
        var commentCount: Int = 0

        private var token: String = null.toString()
        private var userID: String = null.toString()

        fun setToken(token: String){
            this.token = token
        }

        fun setUserID(userID: String){
            this.userID = userID
        }

        fun bindFeed(data: Post, context: Context) {
            mContext = context
            postData = data
            likeCount = data.likesCount!!
            commentCount = data.commentsCount!!
            likeStatus = data.liked

            burger.visibility = View.INVISIBLE // TODO:: Remove when dialog have delete post

            itemView.apply {
                if (data.profilePicture != null){
                    avatar.load(data.profilePicture){
                        transformations(CircleCropTransformation())
                    }
                }
                if (commentCount > 0){
                    textCommentCount.text = commentCount.toString()
                }else{
                    viewAllCommentProfileText.visibility = View.GONE
                }

                dateToFormatTime(timePost, data.date)
                username.text = data.username.toString()

                setStatus(postData!!.text.toString())
                setLike(likeStatus, likeCount)

                listenLikeIcon(view)
                listenViewAllComment()
                listenCommentIcon()
            }
        }

        private fun setStatus(text: String){
            if (text?.length!! > 249) {
                val cutCaption = text?.removeRange(249, text.length)
                val caption = "$cutCaption... more"
                val spannableString = SpannableString(caption)
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        if (mContext is HomeActivity) {
                            postData?.id.toString().let { (mContext as ProfileActivity).gotoDetailPost(it) }
                        }
                    }
                }

                status.setMovementMethod(LinkMovementMethod.getInstance());
                spannableString.setSpan(clickableSpan, caption.length - 4, caption.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                status.text = spannableString
                hashtagView.visibility = View.GONE
            } else {
                setHashTag(postData?.tag)
                status.text = text
            }
        }

        fun setHashTag(tags: ArrayList<String>?){
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

                        hashtagView.setMovementMethod(LinkMovementMethod.getInstance());
                        spannableString.setSpan(clickableSpan, tagMore.length - 4, tagMore.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        hashtagView.text = spannableString
                        anyMore = true
                        break
                    }
                }

                if (!anyMore){
                    hashtagView.text = hashTag
                }
            }else{
                hashtagView.visibility = View.GONE
            }
        }

        fun setLike(likeStatus: Int?, likeCount: Int){

            if (likeStatus.toString() == "1"){
                likesIcon.visibility = View.VISIBLE
                dislikeIcon.visibility = View.GONE
            }else {
                likesIcon.visibility = View.GONE
                dislikeIcon.visibility = View.VISIBLE
            }

            this.textLikeCount.text = likeCount.toString()
        }

        fun listenLikeIcon(view: View){
            val likeIcon = view.findViewById<RelativeLayout>(R.id.likeLayoutProfileText)
            likeIcon.setOnClickListener {
                if(likeStatus.toString() == "1"){
                    dislikePost(postData?.id.toString(), userID.toString(), token)
                    likeStatus = 0
                    likeCount--
                    setLike(likeStatus, likeCount)
                }else {
                    likePost(postData?.id.toString(), userID.toString(), token)
                    likeStatus = 1
                    likeCount++
                    setLike(likeStatus, likeCount)
                }
            }
        }

        fun listenViewAllComment(){
            viewAllCommentProfileText.setOnClickListener {
                if (mContext is ProfileActivity){
                    (mContext as ProfileActivity).gotoDetailPost(postData?.id.toString())
                }
            }
        }

        fun listenCommentIcon(){
            commentIcon.setOnClickListener {
                if (mContext is ProfileActivity){
                    (mContext as ProfileActivity).gotoDetailPost(postData?.id.toString())
                }
            }
        }

    }
}