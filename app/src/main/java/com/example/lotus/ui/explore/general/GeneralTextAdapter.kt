package com.example.lotus.ui.explore.general


import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.example.lotus.R
import com.example.lotus.ui.explore.general.model.Data
import com.example.lotus.ui.home.HomeActivity

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
        var mContext: Context? = null
        private var postData: Data? = null

        var likeStatus: Boolean? = false
        var likeCount: Int = 0

        fun bindFeed(data: Data, context: Context) {
            itemView.apply {
                postData = data
                mContext = context
                val hashtag : TextView = view.findViewById(R.id.tagarText1)
                data.hashtag?.let { tagar(hashtag, it) }
                val commentCount1: TextView = view.findViewById(R.id.textIcCommentPost) as TextView
                val username1: TextView = view.findViewById<View>(R.id.textUsername1) as TextView
                val ava1: ImageView = view.findViewById<View>(R.id.imageAvatar1) as ImageView
                val comment1: TextView = view.findViewById<View>(R.id.textIcCommentPost) as TextView

                val commentCount2: TextView = view.findViewById(R.id.textIcCommentPost2) as TextView
                val username2: TextView = view.findViewById<View>(R.id.textUsername2) as TextView
                val ava2: ImageView = view.findViewById<View>(R.id.imageAvatar2) as ImageView
                val comment2: TextView = view.findViewById<View>(R.id.textIcCommentPost2) as TextView

                commentCount1.text = data.posts[0].commentsCount.toString()
                username1.text = data?.posts[0].username
                comment1.text = data.posts[0].commentsCount.toString()

                commentCount2.text = data.posts[1].commentsCount.toString()
                username2.text = data?.posts[1].username
                comment2.text = data.posts[1].commentsCount.toString()

                setMediaPost1(view, data?.posts[0].text)
                setProfilePicture1(ava1, data?.posts[0].profilePicture.toString())
                setLike1(view, data?.posts[0].like, likeCount)

                setMediaPost2(view, data?.posts[1].text)
                setProfilePicture2(ava2, data?.posts[1].profilePicture.toString())
                setLike2(view, data?.posts[1].like, likeCount)
            }

        }

        private fun setLike1(view: View, likeStatus: Boolean?, likeCount: Int) {
            val iconLikeTrue = view.findViewById<ImageView>(R.id.icLikeTrue1)
            val iconLikeFalse = view.findViewById<ImageView>(R.id.icLikeFalse1)
            val textLikeCount = view.findViewById<TextView>(R.id.textIctLikes1)

            if (likeStatus == true){
                iconLikeTrue.visibility = View.VISIBLE
                iconLikeFalse.visibility = View.GONE
            }else {
                iconLikeTrue.visibility = View.GONE
                iconLikeFalse.visibility = View.VISIBLE
            }

            textLikeCount.text = likeCount.toString()
        }
        private fun setProfilePicture1(profpic: ImageView, url: String) {
            profpic.load(url){
                transformations(CircleCropTransformation())
            }

        }

        private fun setMediaPost1(view: View, text: String) {
            val postText = view.findViewById<CardView>(R.id.cardPostText)
            val tag = view.findViewById<TextView>(R.id.textHashtagFeed)
            val postTextView = view.findViewById<TextView>(R.id.textStatusDetail)

            postText.visibility = View.VISIBLE

            if (text?.length!! > 249) {
                val cutCaption = text?.removeRange(249, text.length)
                val caption = "$cutCaption... more"
                val spannableString = SpannableString(caption)
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        if (mContext is HomeActivity) {
                            postData?.let { (mContext as HomeActivity).detailPostFromExplore(it) }
                        }
                    }
                }

                postTextView.setMovementMethod(LinkMovementMethod.getInstance());
                spannableString.setSpan(clickableSpan, caption.length - 4, caption.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                postTextView.text = spannableString
                tag.visibility = View.GONE
            } else {
                tagar(tag, postData?.posts?.get(0)?.tag.toString())
                postTextView.text = text
            }

        }

        private fun setLike2(view: View, likeStatus: Boolean?, likeCount: Int) {
            val iconLikeTrue2 = view.findViewById<ImageView>(R.id.icLikeTrue2)
            val iconLikeFalse2 = view.findViewById<ImageView>(R.id.icLikeFalse2)
            val textLikeCount2 = view.findViewById<TextView>(R.id.textIctLikes2)

            if (likeStatus == true){
                iconLikeTrue2.visibility = View.VISIBLE
                iconLikeFalse2.visibility = View.GONE
            }else {
                iconLikeTrue2.visibility = View.GONE
                iconLikeFalse2.visibility = View.VISIBLE
            }

            textLikeCount2.text = likeCount.toString()
        }

        private fun setProfilePicture2(profpic: ImageView, url: String) {
            profpic.load(url){
                transformations(CircleCropTransformation())
            }

        }

        private fun setMediaPost2(view: View, text: String) {
            val postText = view.findViewById<CardView>(R.id.cardPostText2)
            val tag = view.findViewById<TextView>(R.id.textHashtagFeed2)
            val postTextView = view.findViewById<TextView>(R.id.textStatusDetail2)

            postText.visibility = View.VISIBLE

            if (text?.length!! > 249) {
                val cutCaption = text?.removeRange(249, text.length)
                val caption = "$cutCaption... more"
                val spannableString = SpannableString(caption)
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        if (mContext is HomeActivity) {
                            postData?.let { (mContext as HomeActivity).detailPostFromExplore(it) }
                        }
                    }
                }

                postTextView.setMovementMethod(LinkMovementMethod.getInstance());
                spannableString.setSpan(clickableSpan, caption.length - 4, caption.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                postTextView.text = spannableString
                tag.visibility = View.GONE
            } else {
                tagar(tag, postData?.posts?.get(0)?.tag.toString())
                postTextView.text = text
            }

        }

        fun tagar(view: TextView, tags: String) {
            var tagar: String = ""
            tagar += "#$tags"
            view.text = tagar
        }

    }

}

