package com.example.lotus.ui.explore.hashtag.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.asura.library.posters.Poster
import com.asura.library.posters.RemoteImage
import com.asura.library.posters.RemoteVideo
import com.asura.library.views.PosterSlider
import com.example.lotus.R
import com.example.lotus.models.MediaData
import com.example.lotus.models.Post
import com.example.lotus.models.Respon
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.CreatePostActivity
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.explore.hashtag.HashtagActivity
import com.example.lotus.ui.home.Constant
import com.example.lotus.ui.login.LoginActivity
import com.example.lotus.utils.dateToFormatTime
import com.example.lotus.utils.dislikePost
import com.example.lotus.utils.likePost
import kotlinx.android.synthetic.main.layout_hashtag_media_item.view.*

class HashtagMediaAdapter(private val listHashtagMedia: ArrayList<Post>, val context: Context) :
    RecyclerView.Adapter<HashtagMediaAdapter.Holder>() {
    val token = SharedPrefManager.getInstance(context).token.token
    val userID = SharedPrefManager.getInstance(context).user._id

    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.mContext = context;

        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_hashtag_media_item, parent, false)
        )
    }

    override fun getItemCount(): Int = listHashtagMedia.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val usernameSrc = SharedPrefManager.getInstance(context).user.username

        holder.bindFeed(listHashtagMedia[position], context)
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val item = Holder(holder.itemView)

            item.setToken(token.toString())
            item.setUserID(userID.toString())
            item.bindFeed(listHashtagMedia[position], context)


            holder.itemView.icShareHashtagM.setOnClickListener {
                val intent = Intent(context, CreatePostActivity::class.java)

                intent.putExtra("Extra", "DetailPost")
                intent.putExtra("Media", listHashtagMedia[position].media)
                intent.putExtra("Text", listHashtagMedia[position].text)
                intent.putExtra("PostID", listHashtagMedia[position].id)
                intent.putExtra("Username", listHashtagMedia[position].username)
                intent.putExtra("Tags", listHashtagMedia[position].tag)

                if (context is HashtagActivity) {
                    context.startActivity(intent)
                }
            }

            holder.itemView.ivEllipsesHashtag.setOnClickListener {
                if (context is HashtagActivity) {
                    context.showDialog(listHashtagMedia[position].media!!)
                }
            }

            item.follow.setOnClickListener {
                val usernameTrg = listHashtagMedia[position].username
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
            item.unfollow.setOnClickListener {
                val usernameTrg = listHashtagMedia[position].username
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
                                    item.setUnfollow()
                                } else {
                                    Log.e("ERROR!!!", "Unfollowing ${respon.code}")
                                }
                            }

                            override fun onError(anError: ANError) {
                                Log.e("ERROR!!!", "While unfollowing ${anError.errorCode}")

                            }
                        })
            }
        }
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val follow: Button = view.findViewById(R.id.btnFollow)
        val unfollow: Button = view.findViewById(R.id.btnUnfollow)

        var mContext: Context? = null
        private var likeCount: Int = 0
        private var postData: Post? = null
        private var token: String = null.toString()
        private var userID: String = null.toString()
        var likeStatus: Int? = 0
        var commentCount: Int = 0
        private var posterSlider: PosterSlider? = null

        fun setToken(token: String) {
            this.token = token
        }

        fun setUserID(userID: String) {
            Log.d("Userid di setuserid", userID)
            this.userID = userID
        }

        fun checkLogin() {
            if (!(this.mContext?.let { SharedPrefManager.getInstance(it).isLoggedIn })!!) {
                mContext!!.startActivity(Intent(mContext, LoginActivity::class.java))
            }
        }

        fun bindFeed(post: Post, context: Context) {
            itemView.apply {
                postData = post
                likeCount = post.likesCount!!
                mContext = context
                commentCount = post.commentsCount!!

                val textUsernameHashtag: TextView =
                    view.findViewById<View>(R.id.textUsernameHashtag) as TextView
                val imageAvatarHashtag: ImageView =
                    view.findViewById<View>(R.id.imageAvatarHashtag) as ImageView
                val time: TextView = view.findViewById<View>(R.id.textTimeHashtag) as TextView
                val comment: TextView =
                    view.findViewById<View>(R.id.textIcCommentHashtag) as TextView


                if (commentCount > 0) {
                    comment.text = commentCount.toString()
                } else {
                    viewAllCommentHashtag.visibility = View.GONE
                }
                listenLikeIcon(view)
                listenSendId(view, post)
                textUsernameHashtag.text = post.username
                setMediaPost(view, post.media, post.text)
                setProfilePicture(imageAvatarHashtag, post.profilePicture.toString())
                dateToFormatTime(time, post.date)
                setLike(view, post.liked, likeCount)

            }
        }

        private fun setMediaPost(view: View, medias: ArrayList<MediaData>?, text: String?) {
            posterSlider = view.findViewById(R.id.mediaHashtag)

            val posters: MutableList<Poster> = ArrayList()
            if (medias!!.size > 0) {
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
            }
        }

        fun setCaption(view: View, text: String?) {
            val captionView = view.findViewById<TextView>(R.id.textCaption)
            if (text?.length!! > 99) {
                val cutCaption = text?.removeRange(99, text.length)
                val caption = "$cutCaption... more"
                val spannableString = SpannableString(caption)
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        checkLogin()
                        if (mContext is HashtagActivity) {
                            postData?.let { (mContext as HashtagActivity).gotoDetailPost(it) }
                        }
                    }
                }

                captionView.setMovementMethod(LinkMovementMethod.getInstance());
                spannableString.setSpan(
                    clickableSpan,
                    caption.length - 4,
                    caption.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                captionView.text = spannableString
            } else {
                if (text.length < 1) {
                    captionView.visibility = View.GONE
                }
                val tagCaption = view.findViewById<TextView>(R.id.textHashtag2)
                if (tagCaption == null) {
                    tagCaption?.visibility = View.GONE
                } else {
                    setHashTag(tagCaption, postData?.tag)
                }
                captionView.text = text
            }
        }

        fun setHashTag(view: TextView, tags: ArrayList<String>?) {

            if (tags?.size!! > 0) {
                var hashTag: String = ""
                var anyMore = false

                for ((i, tag) in tags.withIndex()) {
                    hashTag += "#$tag "
                    if (i == 5) {
                        val tagMore = "$hashTag... more"
                        val spannableString = SpannableString(tagMore)
                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                if (mContext is HashtagActivity) {
                                    postData?.let { (mContext as HashtagActivity).gotoDetailPost(it) }
                                }
                            }
                        }

                        view.setMovementMethod(LinkMovementMethod.getInstance());
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
            val iconLikeTrue = view.findViewById<ImageView>(R.id.icLikeTrueHashtag)
            val iconLikeFalse = view.findViewById<ImageView>(R.id.icLikeFalseHashtag)
            val textLikeCount = view.findViewById<TextView>(R.id.textIctLikesHashtag)

            if (likeStatus.toString() == "1") {
                iconLikeTrue.visibility = View.VISIBLE
                iconLikeFalse.visibility = View.GONE
            } else {
                iconLikeTrue.visibility = View.GONE
                iconLikeFalse.visibility = View.VISIBLE
            }

            textLikeCount.text = likeCount.toString()
        }

        fun listenLikeIcon(view: View) {
            val likeIcon = view.findViewById<RelativeLayout>(R.id.likeLayoutHashtag)
            likeIcon.setOnClickListener {
                checkLogin()
                if (likeStatus.toString() == "1") {
                    dislikePost(postData?.id.toString(), userID, token)
                    likeStatus = 0
                    likeCount--
                    setLike(view, likeStatus, likeCount)
                    Log.e(
                        "Listen like account!!!",
                        "like Post ${postData?.id}, ${userID}, ${token}"
                    )
                } else {
                    likePost(postData?.id.toString(), userID, token)
                    likeStatus = 1
                    likeCount++
                    setLike(view, likeStatus, likeCount)
                }
            }
        }
        fun listenSendId(view: View, data: Post) {
            val icCommentHashtag = view.findViewById<ImageView>(R.id.icCommentHashtag)

            icCommentHashtag.setOnClickListener {
                val ani = data.id
                val bundle = Bundle()
                bundle.putString("id", ani)
                val dataPost = DetailPost()
                dataPost.arguments = bundle
                if (mContext is HashtagActivity) {
                    (mContext as HashtagActivity).detailPost(ani.toString())
                }
            }
        }
        @SuppressLint("ResourceAsColor")
        fun setFollowing() {
            unfollow.visibility = View.VISIBLE
            follow.visibility = View.GONE
        }
        @SuppressLint("ResourceAsColor")
        fun setUnfollow() {
            unfollow.visibility = View.GONE
            follow.visibility = View.VISIBLE
        }
    }
}

