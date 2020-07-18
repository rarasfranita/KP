package com.example.lotus.ui.explore.general.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.example.lotus.R
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.explore.general.model.Data
import com.example.lotus.ui.explore.general.model.Post
import com.example.lotus.ui.explore.hashtag.HashtagActivity
import com.example.lotus.ui.login.LoginActivity
import com.example.lotus.utils.dislikePost
import com.example.lotus.utils.likePost

class GeneralTextAdapter(private val listExploreText: MutableList<Data>, val context: Context) :
    RecyclerView.Adapter<GeneralTextAdapter.Holder>() {
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.mContext = context

        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_explore_textitem, parent, false)
        )
    }

    override fun getItemCount(): Int = listExploreText.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindFeed(listExploreText[position], context)
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val more: TextView = view.findViewById(R.id.moreTextGeneralText)
        var mContext: Context? = null
        private var likeCount: Int = 0
        var likeStatus: Int? = 0
        var postData: Data? = null

        fun bindFeed(data: Data, context: Context) {
            itemView.apply {
//                listenLikeIcon(view)
                Log.d("DATaaaA", data.toString())
                data.posts!![0].belongsTo?.let { Log.d("DATadaaA", it) }
                mContext = context
                val hashtag: TextView = view.findViewById(R.id.tagarGeneralText)
                data.hashtag?.let { tagar(hashtag, it) }
                listenSendhashtag(view, data)
                listenSendId(view, data)
                setMediaPost(view, data.posts, data.posts[0].text)
//                setLike1(view, data.posts[0].liked, likeCount)
//                setLike2(view, data.posts[1].liked, likeCount)

                Log.d("listenlikeicon", data.toString())

            }

        }

        fun checkLogin() {
            if (!(this.mContext?.let { SharedPrefManager.getInstance(it).isLoggedIn })!!) {
                mContext!!.startActivity(Intent(mContext, LoginActivity::class.java))
            }
        }

        fun listenSendhashtag(view: View, data: Data) {
            val more: TextView = view.findViewById(R.id.moreTextGeneralText)
            more.setOnClickListener {
//                checkLogin()
                val more = Intent(mContext, HashtagActivity::class.java)
                val ani = data.hashtag
                val bundle = Bundle()
                bundle.putString("hashtag", ani)
                val dataPost = DetailPost()
                dataPost.arguments = bundle
                more.putExtras(bundle)
                mContext!!.startActivity(more)
            }
        }

        // TODO: 18/07/20 change intent like to logic
        fun listenSendId(view: View, data: Data) {
            val icCommentGeneralText = view.findViewById<ImageView>(R.id.icCommentGeneralText)
            val icComment2GeneralText = view.findViewById<ImageView>(R.id.icComment2GeneralText)
            icCommentGeneralText.setOnClickListener {
                checkLogin()
                val ani = data.posts?.get(0)?.id
                val bundle = Bundle()
                bundle.putString("id", ani)
                val dataPost = DetailPost()
                dataPost.arguments = bundle

                if (mContext is GeneralActivity) {
                    (mContext as GeneralActivity).detailPost(ani.toString())
                }
            }
            icComment2GeneralText.setOnClickListener {
                checkLogin()
                val ani = data.posts?.get(1)?.id
                val bundle = Bundle()
                bundle.putString("id", ani)
                val dataPost = DetailPost()
                dataPost.arguments = bundle
                if (mContext is GeneralActivity) {
                    (mContext as GeneralActivity).detailPost(ani.toString())
                }
            }
        }

        private fun setMediaPost(view: View, post: ArrayList<Post>?, text: String?) {
            val tag = view.findViewById<TextView>(R.id.textHashtagGeneralText)
            val postTextView1 = view.findViewById<TextView>(R.id.textStatusDetailGeneralText)
            val postTextView2 = view.findViewById<TextView>(R.id.textStatusDetail2GeneralText)
            val RL2 = view.findViewById<RelativeLayout>(R.id.RL2)
            var commentCount1: TextView =
                view.findViewById(R.id.textIcCommentGeneralText) as TextView
            val username1: TextView =
                view.findViewById<View>(R.id.textUsername1GeneralText) as TextView
            val ava1: ImageView =
                view.findViewById<View>(R.id.imageAvatar1GeneralText) as ImageView
            val comment1: TextView =
                view.findViewById<View>(R.id.textIcCommentGeneralText) as TextView

            val commentCount2: TextView =
                view.findViewById(R.id.textIcComment2GeneralText) as TextView
            val username2: TextView =
                view.findViewById<View>(R.id.textUsername2GeneralText) as TextView
            val ava2: ImageView =
                view.findViewById<View>(R.id.imageAvatar2GeneralText) as ImageView
            val comment2: TextView =
                view.findViewById<View>(R.id.textIcComment2GeneralText) as TextView

            if (post != null) {
                when (post.size) {
                    // TODO: 18/07/20 change when with for
                    2, 3, 4, 5, 6, 7, 8, 9, 10 -> {
                        for ((i, post) in post.withIndex()) {
                            when (i) {
                                0 -> {
                                    val likeIcon1 =
                                        view.findViewById<RelativeLayout>(R.id.likeLayoutGeneralText)
                                    likeIcon1.setOnClickListener {
                                        checkLogin()
                                        if (likeStatus.toString() == "1") {
                                            dislikePost(
                                                post.id.toString(),
                                                post.belongsTo.toString()
                                            )
                                            likeStatus = 0
                                            likeCount--
                                            setLike1(view, likeStatus, likeCount)


                                        } else {
                                            likePost(
                                                post.id.toString(),
                                                post.belongsTo.toString()
                                            )
                                            likeStatus = 1
                                            likeCount++
                                            setLike1(view, likeStatus, likeCount)
                                        }
                                    }
                                    if (post.text?.length!! > 249) {
                                        val cutCaption =
                                            post.text.removeRange(249, post.text.length)
                                        val caption = "$cutCaption... more"
                                        val spannableString = SpannableString(caption)
                                        val clickableSpan = object : ClickableSpan() {
                                            override fun onClick(p0: View) {
                                                val ani = post.id
                                                val bundle = Bundle()
                                                bundle.putString("id", ani)
                                                val dataPost = DetailPost()
                                                dataPost.arguments = bundle
                                                if (mContext is GeneralActivity) {
                                                    (mContext as GeneralActivity).detailPost(ani.toString())
                                                }
                                            }
                                        }
                                        postTextView1.movementMethod =
                                            LinkMovementMethod.getInstance()
                                        spannableString.setSpan(
                                            clickableSpan,
                                            caption.length - 4,
                                            caption.length,
                                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                        )
                                        postTextView1.text = spannableString
                                        tag.visibility = View.GONE
                                    } else {
                                        hashTag(
                                            tag,
                                            post.tag
                                        )
                                        postTextView1.text = text
                                    }
                                    commentCount1.text = post.commentsCount.toString()
                                    username1.text = post.username.toString()
                                    comment1.text = post.commentsCount.toString()
                                    setProfilePicture(ava1, post.profilePicture.toString())
                                    setLike1(view, post.liked, likeCount)
                                    Log.d("text_2_0", post.text)
                                }
                                1 -> {
                                    val likeIcon2 =
                                        view.findViewById<RelativeLayout>(R.id.likeLayout2GeneralText)
                                    likeIcon2.setOnClickListener {
                                        checkLogin()
                                        if (likeStatus.toString() == "1") {
                                            dislikePost(
                                                post.id.toString(),
                                                post.belongsTo.toString()
                                            )
                                            likeStatus = 0
                                            likeCount--
                                            setLike2(view, likeStatus, likeCount)
                                        } else {
                                            likePost(
                                                post.id.toString(),
                                                post.belongsTo.toString()
                                            )
                                            likeStatus = 1
                                            likeCount++
                                            setLike2(view, likeStatus, likeCount)
                                        }


                                    }
                                    if (post.text?.length!! > 249) {
                                        val cutCaption =
                                            post.text.removeRange(249, post.text.length)
                                        val caption = "$cutCaption... more"
                                        val spannableString = SpannableString(caption)
                                        val clickableSpan = object : ClickableSpan() {
                                            override fun onClick(p0: View) {
                                                val ani = post.id
                                                val bundle = Bundle()
                                                bundle.putString("id", ani)
                                                val dataPost = DetailPost()
                                                dataPost.arguments = bundle
                                                if (mContext is GeneralActivity) {
                                                    (mContext as GeneralActivity).detailPost(ani.toString())
                                                }
                                            }
                                        }
                                        postTextView2.movementMethod =
                                            LinkMovementMethod.getInstance()
                                        spannableString.setSpan(
                                            clickableSpan,
                                            caption.length - 4,
                                            caption.length,
                                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                        )
                                        postTextView2.text = spannableString
                                        tag.visibility = View.GONE
                                    } else {
                                        hashTag(
                                            tag,
                                            post.tag
                                        )
                                        postTextView2.text = text
                                    }
                                    commentCount2.text = post.commentsCount.toString()
                                    username2.text = post.username
                                    comment2.text = post.commentsCount.toString()
                                    setProfilePicture(ava2, post.profilePicture.toString())
                                    setLike2(view, post.liked, likeCount)
                                    Log.d("text_2_1", post.text)
                                }
                            }
                        }
                    }
                    1 -> {
                        RL2.visibility = View.GONE
                        for ((i, post) in post.withIndex()) {
                            when (i) {
                                0 -> {
                                    val likeIcon1 =
                                        view.findViewById<RelativeLayout>(R.id.likeLayoutGeneralText)
                                    likeIcon1.setOnClickListener {
                                        checkLogin()
                                        if (likeStatus.toString() == "1") {
                                            dislikePost(
                                                post.id.toString(),
                                                post.belongsTo.toString()
                                            )
                                            likeStatus = 0
                                            likeCount--
                                            setLike1(view, likeStatus, likeCount)


                                        } else {
                                            likePost(
                                                post.id.toString(),
                                                post.belongsTo.toString()
                                            )
                                            likeStatus = 1
                                            likeCount++
                                            setLike1(view, likeStatus, likeCount)
                                        }
                                    }
                                    if (post.text?.length!! > 249) {
                                        val cutCaption =
                                            post.text.removeRange(249, post.text.length)
                                        val caption = "$cutCaption... more"
                                        val spannableString = SpannableString(caption)
                                        val clickableSpan = object : ClickableSpan() {
                                            override fun onClick(p0: View) {
                                                val ani = post.id
                                                val bundle = Bundle()
                                                bundle.putString("id", ani)
                                                val dataPost = DetailPost()
                                                dataPost.arguments = bundle
                                                if (mContext is GeneralActivity) {
                                                    (mContext as GeneralActivity).detailPost(ani.toString())
                                                }
                                            }
                                        }
                                        postTextView1.movementMethod =
                                            LinkMovementMethod.getInstance()
                                        spannableString.setSpan(
                                            clickableSpan,
                                            caption.length - 4,
                                            caption.length,
                                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                        )
                                        postTextView1.text = spannableString
                                        tag.visibility = View.GONE
                                    } else {
                                        hashTag(
                                            tag,
                                            post.tag
                                        )
                                        postTextView1.text = text
                                    }
                                    commentCount1.text = post.commentsCount.toString()
                                    username1.text = post.username.toString()
                                    comment1.text = post.commentsCount.toString()
                                    setProfilePicture(ava1, post.profilePicture.toString())
                                    setLike1(view, post.liked, likeCount)
                                    Log.d("text_1_0", post.text)

                                }
                            }
                        }
                    }
                }


            }
        }


        private fun setLike1(view: View, likeStatus: Int?, likeCount: Int) {
            val iconLikeTrue = view.findViewById<ImageView>(R.id.icLikeTrue1GeneralText)
            val iconLikeFalse = view.findViewById<ImageView>(R.id.icLikeFalse1GeneralText)
            val textLikeCount = view.findViewById<TextView>(R.id.textIctLikes1GeneralText)

            if (likeStatus.toString() == "1") {
                iconLikeTrue.visibility = View.VISIBLE
                iconLikeFalse.visibility = View.GONE
            } else {
                iconLikeTrue.visibility = View.GONE
                iconLikeFalse.visibility = View.VISIBLE
            }

            textLikeCount.text = likeCount.toString()
        }
        private fun setLike2(view: View, likeStatus: Int?, likeCount: Int) {
            val iconLikeTrue = view.findViewById<ImageView>(R.id.icLikeTrue2GeneralText)
            val iconLikeFalse = view.findViewById<ImageView>(R.id.icLikeFalse2GeneralText)
            val textLikeCount = view.findViewById<TextView>(R.id.textIctLikes2GeneralText)

            if (likeStatus.toString() == "1") {
                iconLikeTrue.visibility = View.VISIBLE
                iconLikeFalse.visibility = View.GONE
            } else {
                iconLikeTrue.visibility = View.GONE
                iconLikeFalse.visibility = View.VISIBLE
            }

            textLikeCount.text = likeCount.toString()
        }

        private fun setProfilePicture(profpic: ImageView, url: String) {
            profpic.load(url) {
                transformations(CircleCropTransformation())
            }

        }


        private fun tagar(view: TextView, tags: String) {
            var tagar = ""
            tagar += "#$tags"
            view.text = tagar
        }

        private fun hashTag(view: TextView, tags: ArrayList<String>?) {

            if (tags?.size!! > 0) {
                var hashTag = ""
                var anyMore = false

                for ((i, tag) in tags.withIndex()) {
                    hashTag += "#$tag "
                    if (i == 5) {
                        val tagMore = "$hashTag... more"
                        val spannableString = SpannableString(tagMore)
                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(p0: View) {
//                                if (mContext is GeneralActivity) {
//                                    postData?.let {
//                                        (mContext as GeneralActivity).detailPostFromExplore(it)
//                                    }
//                                }
                            }
                        }

                        view.movementMethod = LinkMovementMethod.getInstance()
                        spannableString.setSpan(
                            clickableSpan,
                            tagMore.length - 4,
                            tagMore.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        view.text = spannableString
                        anyMore = true
                        break
                    }
                }

                if (!anyMore) {
                    view.text = hashTag
                }
            } else {
                view.visibility = View.GONE
            }
        }

    }

}
