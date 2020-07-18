package com.example.lotus.ui.explore.hashtag.adapter


import android.content.Context
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
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.explore.hashtag.model.Data

class HashtagTextAdapter(private val listHashtagMedia: ArrayList<Data>, val context: Context) :
    RecyclerView.Adapter<HashtagTextAdapter.Holder>() {
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.mContext = context;

        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_hashtag_text_item, parent, false)
        )
    }

    override fun getItemCount(): Int = listHashtagMedia.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindFeed(listHashtagMedia[position], context)
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        var mContext: Context? = null
        private var postData: Data? = null
        var likeStatus: Boolean? = false
        var likeCount: Int = 0
        var commentCount: Int = 0

        fun bindFeed(data: Data, context: Context) {
            itemView.apply {
                mContext = context

                listenLikeIcon(view)
                listenCommentIcon(view)

                val textUsernameHashtag: TextView =
                    view.findViewById<View>(R.id.textUsernameHashtag) as TextView
                val imageAvatarHashtag: ImageView =
                    view.findViewById<View>(R.id.imageAvatarHashtag) as ImageView

                textUsernameHashtag.text = data.name
                listenSendId(view, data)

                setCaption(view, data.text)
                setProfilePicture(imageAvatarHashtag, data.profilePicture.toString())
                setLike(view, postData?.like, likeCount)

            }
        }

        fun listenSendId(view: View, data: Data) {
            val relLayoutHashtag = view.findViewById<RelativeLayout>(R.id.relLayoutHashtag)

            relLayoutHashtag.setOnClickListener {
                val ani = data.id
                val bundle = Bundle()
                bundle.putString("id", ani)
                val dataPost = DetailPost()
                dataPost.arguments = bundle
                if (mContext is GeneralActivity) {
                    (mContext as GeneralActivity).detailPost(ani.toString())
                }
            }
        }

        fun setCaption(view: View, text: String?) {
            val tag = view.findViewById<TextView>(R.id.textHashtagHashtag)
            val postTextView = view.findViewById<TextView>(R.id.textStatusDetail)
//            hashTag(tag, postData?.tag)
            // TODO: 18/07/20 add hashtag
            Log.d("HASHdsdTAG", postData?.tag.toString())

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

        fun setLike(view: View, likeStatus: Boolean?, likeCount: Int) {
            val iconLikeTrue = view.findViewById<ImageView>(R.id.icLikeTrueHashtag)
            val iconLikeFalse = view.findViewById<ImageView>(R.id.icLikeFalseHashtag)
            val textLikeCount = view.findViewById<TextView>(R.id.textIctLikesHashtag)

            if (likeStatus == true) {
                iconLikeTrue.visibility = View.VISIBLE
                iconLikeFalse.visibility = View.GONE
            } else {
                iconLikeTrue.visibility = View.GONE
                iconLikeFalse.visibility = View.VISIBLE
            }

            textLikeCount.text = likeCount.toString()
        }


        fun listenCommentIcon(view: View) {
        }

        fun listenLikeIcon(view: View) {
            val likeIcon = view.findViewById<RelativeLayout>(R.id.likeLayoutHashtag)
            likeIcon.setOnClickListener {
                if (likeStatus == true) {
                    likeStatus = false
                    likeCount--
                    setLike(view, likeStatus, likeCount)
                } else {
                    likeStatus = true
                    likeCount++
                    setLike(view, likeStatus, likeCount)
                }
            }
        }
    }
}

