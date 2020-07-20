package com.example.lotus.ui.explore.hashtag.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.lotus.models.Post
import com.example.lotus.models.Respon
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.CreatePostActivity
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.explore.hashtag.HashtagActivity
import com.example.lotus.ui.home.Constant
import com.example.lotus.ui.login.LoginActivity
import com.example.lotus.utils.dislikePost
import com.example.lotus.utils.likePost
import kotlinx.android.synthetic.main.layout_hashtag_text_item.view.*

class HashtagTextAdapter(private val listHashtagText: ArrayList<Post>, val context: Context) :
    RecyclerView.Adapter<HashtagTextAdapter.Holder>() {
    val token = SharedPrefManager.getInstance(context).token.token
    val userID = SharedPrefManager.getInstance(context).user._id

    private var mContext: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.mContext = context;

        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_hashtag_text_item, parent, false)
        )
    }

    override fun getItemCount(): Int = listHashtagText.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val usernameSrc = SharedPrefManager.getInstance(context).user.username
        holder.bindFeed(listHashtagText[position], context)
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val item = Holder(holder.itemView)

            item.setToken(token.toString())
            item.setUserID(userID.toString())
            item.bindFeed(listHashtagText[position],context)


            holder.itemView.icShareHashtagT.setOnClickListener {
                val intent = Intent(context, CreatePostActivity::class.java)

                intent.putExtra("Extra", "DetailPost")
                intent.putExtra("Media", listHashtagText[position].media)
                intent.putExtra("Text", listHashtagText[position].text)
                intent.putExtra("PostID", listHashtagText[position].id)
                intent.putExtra("Username", listHashtagText[position].username)
                intent.putExtra("Tags", listHashtagText[position].tag)

                if (context is HashtagActivity) {
                    context.startActivity(intent)
                }
            }

            holder.itemView.ivEllipsesHashtag.setOnClickListener {
                if (context is HashtagActivity){
                    context.showDialog(listHashtagText[position].media!!)
                }
            }

            item.follow.setOnClickListener {
                val usernameTrg = listHashtagText[position].username
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
                val usernameTrg = listHashtagText[position].username
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

        fun setToken(token: String){
            this.token = token
        }
        fun setUserID(userID: String){
            Log.d("Userid di setuserid", userID)
            this.userID = userID
        }

        fun bindFeed(data: Post, context: Context) {
            itemView.apply {
                postData = data
                likeCount = data.likesCount!!
                mContext = context
                commentCount = data.commentsCount!!

                listenLikeIcon(view)

                val textUsernameHashtag: TextView =
                    view.findViewById<View>(R.id.textUsernameHashtag) as TextView
                val imageAvatarHashtag: ImageView =
                    view.findViewById<View>(R.id.imageAvatarHashtag) as ImageView

                textUsernameHashtag.text = data.username
                listenSendId(view, data)

                setCaption(view, data.text)
                setProfilePicture(imageAvatarHashtag, data.profilePicture.toString())
                setLike(view, data.liked, likeCount)

            }
        }

        fun checkLogin() {
            if (!(this.mContext?.let { SharedPrefManager.getInstance(it).isLoggedIn })!!) {
                mContext!!.startActivity(Intent(mContext, LoginActivity::class.java))
            }
        }

        fun listenSendId(view: View, data: Post) {
            val icCommentHashtag = view.findViewById<ImageView>(R.id.icCommentHashtag)

            icCommentHashtag.setOnClickListener {
                checkLogin()
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

        fun setCaption(view: View, text: String?) {
            val tag = view.findViewById<TextView>(R.id.textHashtagHashtag)
            val postTextView = view.findViewById<TextView>(R.id.textStatusDetail)
            hashTag(tag, postData?.tag)
            Log.d("TAG", postData?.tag.toString())

            postTextView.text = text
        }

        fun hashTag(view: TextView, tags: ArrayList<String>?){
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
                if(likeStatus.toString() == "1"){
                    dislikePost(postData?.id.toString(), userID, token)
                    likeStatus = 0
                    likeCount--
                    setLike(view, likeStatus, likeCount)
                    Log.e("Listen like account!!!", "like Post ${postData?.id}, ${userID}, ${token}")
                }else {
                    likePost(postData?.id.toString(), userID, token)
                    likeStatus = 1
                    likeCount++
                    setLike(view, likeStatus, likeCount)
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

