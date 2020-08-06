package com.example.lotus.ui.explore.general.adapter

import android.annotation.SuppressLint
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
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.Respon
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.CreatePostActivity
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.explore.general.model.Data
import com.example.lotus.ui.explore.general.model.Post
import com.example.lotus.ui.explore.hashtag.HashtagActivity
import com.example.lotus.ui.home.Constant
import com.example.lotus.ui.home.HomeActivity
import com.example.lotus.ui.login.LoginActivity
import com.example.lotus.utils.dislikePost
import com.example.lotus.utils.likePost
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.layout_explore_textitem.view.*
import kotlinx.android.synthetic.main.layout_mainfeed_listitem.view.*

class GeneralTextAdapter(private val listExploreText: MutableList<Data>, val context: Context) :
    RecyclerView.Adapter<GeneralTextAdapter.Holder>() {
    val token = SharedPrefManager.getInstance(context).token.token
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
        // not Login
        holder.itemView.icShareGeneralText.visibility = View.GONE
        holder.itemView.icShare2GeneralText.visibility = View.GONE
        holder.itemView.ivEllipses1GeneralText.visibility = View.GONE
        holder.itemView.ivEllipses2GeneralText.visibility = View.GONE

        if (SharedPrefManager.getInstance(context).isLoggedIn) {
            holder.itemView.icShareGeneralText.visibility = View.VISIBLE
            holder.itemView.icShare2GeneralText.visibility = View.VISIBLE
            holder.itemView.ivEllipses1GeneralText.visibility = View.VISIBLE
            holder.itemView.ivEllipses2GeneralText.visibility = View.VISIBLE
        }

        val usernameSrc = SharedPrefManager.getInstance(context).user.username
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val item = Holder(holder.itemView)

            // not Login
            item.follow.visibility = View.GONE
            item.follow2.visibility = View.GONE

            if (SharedPrefManager.getInstance(context).isLoggedIn) {
                item.follow.visibility = View.VISIBLE
                item.follow2.visibility = View.VISIBLE
            }
            item.setToken(token.toString())
            item.bindFeed(listExploreText[position], context)


            holder.itemView.icShareGeneralText.setOnClickListener {

                val intent = Intent(context, CreatePostActivity::class.java)
                intent.putExtra("Extra", "DetailPost")
                intent.putExtra("Media", listExploreText[position].posts?.get(0)?.media)
                intent.putExtra("Text", listExploreText[position].posts?.get(0)?.text)
                intent.putExtra("PostID", listExploreText[position].posts?.get(0)?.id)
                intent.putExtra("Username", listExploreText[position].posts?.get(0)?.username)
                intent.putExtra("Tags", listExploreText[position].posts?.get(0)?.tag)

                if (context is GeneralActivity) {
                    context.startActivity(intent)
                }
            }
            holder.itemView.icShare2GeneralText.setOnClickListener {
                val intent = Intent(context, CreatePostActivity::class.java)

                intent.putExtra("Extra", "DetailPost")
                intent.putExtra("Media", listExploreText[position].posts?.get(1)?.media)
                intent.putExtra("Text", listExploreText[position].posts?.get(1)?.text)
                intent.putExtra("PostID", listExploreText[position].posts?.get(1)?.id)
                intent.putExtra("Username", listExploreText[position].posts?.get(1)?.username)
                intent.putExtra("Tags", listExploreText[position].posts?.get(1)?.tag)

                if (context is GeneralActivity) {
                    context.startActivity(intent)
                }
            }

            holder.itemView.ivEllipses1GeneralText.setOnClickListener {
                if (context is GeneralActivity) {
                    context.showDialog(listExploreText[position].posts?.get(0)?.media!!)
                }
            }
            holder.itemView.ivEllipses2GeneralText.setOnClickListener {
                if (context is GeneralActivity) {
                    context.showDialog(listExploreText[position].posts?.get(1)?.media!!)
                }
            }
            item.follow.setOnClickListener {
                val usernameTrg = listExploreText[position].posts!![0].username
                Log.d("usernameSrc", "${usernameSrc} , usernameTarget, ${usernameTrg}")
                AndroidNetworking.get(EnvService.ENV_API + "/users/$usernameSrc/follow/$usernameTrg")
                    .addPathParameter("usernameSource", usernameSrc)
                    .addPathParameter("usernameTarget", usernameTrg)
                    .addHeaders("Authorization", "Bearer " + token)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsObject(
                        Respon::class.java,
                        object : ParsedRequestListener<Respon> {
                            override fun onResponse(respon: Respon) {
                                if (respon.code.toString() == "200") {
                                    Log.d("RESPON FOLLOWW", respon.data.toString())
                                    item.setFollowing()
                                } else {
                                    Log.e("ERROR!!!", "Following ${respon.code}")
                                }
                            }

                            override fun onError(anError: ANError) {
                                Log.e("ERROR!!!", "While following ${anError.errorCode}")

                            }
                        })
            }
            item.follow2.setOnClickListener {
                val usernameTrg = listExploreText[position].posts!![1].username
                Log.d("usernameSrc", "${usernameSrc} , usernameTarget, ${usernameTrg}")
                AndroidNetworking.get(EnvService.ENV_API + "/users/$usernameSrc/follow/$usernameTrg")
                    .addPathParameter("usernameSource", usernameSrc)
                    .addPathParameter("usernameTarget", usernameTrg)
                    .addHeaders("Authorization", "Bearer " + token)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsObject(
                        Respon::class.java,
                        object : ParsedRequestListener<Respon> {
                            override fun onResponse(respon: Respon) {
                                if (respon.code.toString() == "200") {
                                    Log.d("RESPON FOLLOWW", respon.data.toString())
                                    item.setFollowing2()
                                } else {
                                    Log.e("ERROR!!!", "Following ${respon.code}")
                                }
                            }

                            override fun onError(anError: ANError) {
                                Log.e("ERROR!!!", "While following ${anError.errorCode}")

                            }
                        })
            }
            item.unfollow1.setOnClickListener {
                val usernameTrg = listExploreText[position].posts!![0].username
                Log.d("usernameSrc", "${usernameSrc} , usernameTarget, ${usernameTrg}")
                AndroidNetworking.get(EnvService.ENV_API + "/users/$usernameSrc/unfollow/$usernameTrg")
                    .addPathParameter("usernameSource", usernameSrc)
                    .addPathParameter("usernameTarget", usernameTrg)
                    .addHeaders("Authorization", "Bearer " + token)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsObject(
                        Respon::class.java,
                        object : ParsedRequestListener<Respon> {
                            override fun onResponse(respon: Respon) {
                                if (respon.code.toString() == "200") {
                                    Log.d("RESPON UNFOLLOWW", respon.data.toString())
                                    item.setUnfollow1()
                                } else {
                                    Log.e("ERROR!!!", "UnFollow ${respon.code}")
                                }
                            }

                            override fun onError(anError: ANError) {
                                Log.e("ERROR!!!", "While UnFollow ${anError.errorCode}")

                            }
                        })
            }
            item.unfollow2.setOnClickListener {
                val usernameTrg = listExploreText[position].posts!![1].username
                Log.d("usernameSrc", "${usernameSrc} , usernameTarget, ${usernameTrg}")
                AndroidNetworking.get(EnvService.ENV_API + "/users/$usernameSrc/unfollow/$usernameTrg")
                    .addPathParameter("usernameSource", usernameSrc)
                    .addPathParameter("usernameTarget", usernameTrg)
                    .addHeaders("Authorization", "Bearer " + token)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsObject(
                        Respon::class.java,
                        object : ParsedRequestListener<Respon> {
                            override fun onResponse(respon: Respon) {
                                if (respon.code.toString() == "200") {
                                    Log.d("RESPON UNFOLLOWW", respon.data.toString())
                                    item.setUnfollow2()
                                } else {
                                    Log.e("ERROR!!!", "UnFollow ${respon.code}")
                                }
                            }

                            override fun onError(anError: ANError) {
                                Log.e("ERROR!!!", "While UnFollow ${anError.errorCode}")

                            }
                        })
            }
        }
    }
    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val follow: Button = view.findViewById(R.id.btnFollow)
        val follow2: Button = view.findViewById(R.id.btnFollow2)
        val unfollow1: Button = view.findViewById(R.id.btnUnfollow)
        val unfollow2: Button = view.findViewById(R.id.btnUnfollow2)

        private var token: String = null.toString()
        var mContext: Context? = null
        private var likeCount: Int = 0
        var likeStatus: Int? = 0
        var commentCount: Int = 0

        fun setToken(token: String) {
            this.token = token
        }

        fun bindFeed(data: Data, context: Context) {
            itemView.apply {
                likeCount = data.posts!!.get(0).likesCount!!
                mContext = context
                val hashtag: TextView = view.findViewById(R.id.tagarGeneralText)
                data.hashtag?.let { tagar(hashtag, it) }
                commentCount = data.posts.get(0).commentsCount!!
                listenSendhashtag(view, data)
                listenSendId(view, data)
                setMediaPost(view, data.posts, data.posts.get(0).text)
            }

        }

        fun checkLogin() {

        }

        fun listenSendhashtag(view: View, data: Data) {
            val more: TextView = view.findViewById(R.id.moreTextGeneralText)
            more.setOnClickListener {
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

        fun listenSendId(view: View, data: Data) {
            val cardPostTextGeneralText = view.findViewById<MaterialCardView>(R.id.cardPostTextGeneralText)
            val cardPostText2GeneralText = view.findViewById<MaterialCardView>(R.id.cardPostText2GeneralText)
            val icCommentGeneralText = view.findViewById<ImageView>(R.id.icCommentGeneralText)
            val icComment2GeneralText = view.findViewById<ImageView>(R.id.icComment2GeneralText)

            cardPostTextGeneralText.setOnClickListener {
                val ani = data.posts?.get(0)?.id
                val bundle = Bundle()
                bundle.putString("id", ani)
                val dataPost = DetailPost()
                dataPost.arguments = bundle

                if (mContext is GeneralActivity) {
                    (mContext as GeneralActivity).detailPost(ani.toString())
                }
            }
            cardPostText2GeneralText.setOnClickListener {
                val ani = data.posts?.get(0)?.id
                val bundle = Bundle()
                bundle.putString("id", ani)
                val dataPost = DetailPost()
                dataPost.arguments = bundle

                if (mContext is GeneralActivity) {
                    (mContext as GeneralActivity).detailPost(ani.toString())
                }
            }
            icCommentGeneralText.setOnClickListener {
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
                                    val share1 =
                                        view.findViewById<ImageView>(R.id.icShareGeneralText)
                                    likeIcon1.setOnClickListener {
                                        if ((this.mContext?.let { SharedPrefManager.getInstance(it).isLoggedIn })!!) {
                                            if (likeStatus.toString() == "1") {
                                                dislikePost(
                                                    post.id.toString(),
                                                    post.userId.toString(), token
                                                )
                                                likeStatus = 0
                                                likeCount--
                                                setLike1(view, likeStatus, likeCount)
                                            } else {
                                                likePost(
                                                    post.id.toString(),
                                                    post.belongsTo.toString(), token.toString()
                                                )
                                                likeStatus = 1
                                                likeCount++
                                                setLike1(view, likeStatus, likeCount)
                                            }
                                        } else {
                                            mContext!!.startActivity(Intent(mContext, LoginActivity::class.java))
                                        }
                                    }
                                    share1.setOnClickListener {
                                        if ((this.mContext?.let { SharedPrefManager.getInstance(it).isLoggedIn })!!) {
                                            val intent =
                                                Intent(mContext, CreatePostActivity::class.java)
                                            intent.putExtra("Extra", "Detailpost")
                                            intent.putExtra("Media", post.media)
                                            intent.putExtra("Text", post.text)
                                            intent.putExtra("postID", post.id)
                                            intent.putExtra("username", post.username)
                                            intent.putExtra("Tags", post.tag)

                                            if (mContext is GeneralActivity) {
                                                (mContext as GeneralActivity).startActivity(intent)
                                            }
                                        } else {
                                            mContext!!.startActivity(Intent(mContext, LoginActivity::class.java))
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
                                }
                                1 -> {
                                    val likeIcon2 =
                                        view.findViewById<RelativeLayout>(R.id.likeLayout2GeneralText)
                                    likeIcon2.setOnClickListener {
                                        if ((this.mContext?.let { SharedPrefManager.getInstance(it).isLoggedIn })!!) {
                                            if (likeStatus.toString() == "1") {
                                                dislikePost(
                                                    post.id.toString(),
                                                    post.userId.toString(), token
                                                )
                                                likeStatus = 0
                                                likeCount--
                                                setLike2(view, likeStatus, likeCount)
                                            } else {
                                                likePost(
                                                    post.id.toString(),
                                                    post.belongsTo.toString(), token.toString()
                                                )
                                                likeStatus = 1
                                                likeCount++
                                                setLike2(view, likeStatus, likeCount)
                                            }
                                        } else {
                                            mContext!!.startActivity(Intent(mContext, LoginActivity::class.java))
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
                                        if ((this.mContext?.let { SharedPrefManager.getInstance(it).isLoggedIn })!!) {
                                            if (likeStatus.toString() == "1") {
                                                dislikePost(
                                                    post.id.toString(),
                                                    post.userId.toString(), token
                                                )
                                                likeStatus = 0
                                                likeCount--
                                                setLike1(view, likeStatus, likeCount)


                                            } else {
                                                likePost(
                                                    post.id.toString(),
                                                    post.belongsTo.toString(), token.toString()
                                                )
                                                likeStatus = 1
                                                likeCount++
                                                setLike1(view, likeStatus, likeCount)
                                            }
                                        }else {
                                            mContext!!.startActivity(Intent(mContext, LoginActivity::class.java))
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

        @SuppressLint("ResourceAsColor")
        fun setFollowing() {
            unfollow1.visibility = View.VISIBLE
            follow.visibility = View.GONE
        }

        @SuppressLint("ResourceAsColor")
        fun setFollowing2() {
            unfollow2.visibility = View.VISIBLE
            follow2.visibility = View.GONE
        }

        @SuppressLint("ResourceAsColor")
        fun setUnfollow1() {
            unfollow1.visibility = View.GONE
            follow.visibility = View.VISIBLE
        }

        @SuppressLint("ResourceAsColor")
        fun setUnfollow2() {
            unfollow2.visibility = View.GONE
            follow2.visibility = View.VISIBLE
        }

    }

}
