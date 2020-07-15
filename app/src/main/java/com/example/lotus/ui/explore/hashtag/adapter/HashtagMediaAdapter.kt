package com.example.lotus.ui.explore.hashtag.adapter


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
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.asura.library.posters.Poster
import com.asura.library.posters.RemoteImage
import com.asura.library.posters.RemoteVideo
import com.asura.library.views.PosterSlider
import com.example.lotus.R
import com.example.lotus.models.MediaData
import com.example.lotus.ui.explore.hashtag.HashtagActivity
import com.example.lotus.ui.explore.hashtag.model.Data
import com.example.lotus.utils.DynamicSquareLayout
import com.example.lotus.utils.setTimePost
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.layout_hashtag_media_item.view.*
import kotlin.collections.ArrayList

class HashtagMediaAdapter(private val listHashtagMedia: ArrayList<Data>, val context: Context) :
    RecyclerView.Adapter<HashtagMediaAdapter.Holder>() {
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
        holder.bindFeed(listHashtagMedia[position], context)
        holder.viewAllCommentHashtag.setOnClickListener {
            if (mContext is HashtagActivity) {
                (mContext as HashtagActivity).detailPost(listHashtagMedia[position])

            }
        }
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val viewAllCommentHashtag = view.findViewById<TextView>(R.id.viewAllCommentHashtag)
        var mContext: Context? = null
        private var mediaHashtag: PosterSlider? = null
        private var postData: Data? = null
        var likeStatus: Boolean? = false
        var likeCount: Int = 0
        var commentCount: Int = 0

        fun bindFeed(post: Data, context: Context) {
            itemView.apply {
                postData = post
                mContext = context

                listenLikeIcon(view)
                listenCommentIcon(view)

                val textUsernameHashtag: TextView =
                    view.findViewById<View>(R.id.textUsernameHashtag) as TextView
                val imageAvatarHashtag: ImageView = view.findViewById<View>(R.id.imageAvatarHashtag) as ImageView
                val textIcCommentHashtag: TextView =
                    view.findViewById<View>(R.id.textIcCommentHashtag) as TextView
                val time: TextView = view.findViewById<View>(R.id.textTimeHashtag) as TextView

                textUsernameHashtag.text = post.name

                if (commentCount > 0) {
                    textIcCommentHashtag.text = commentCount.toString()
                } else {
                    viewAllCommentHashtag.visibility = View.GONE
                }

                setMediaPost(view, post.media, post.text)
                setProfilePicture(imageAvatarHashtag, post.profilePicture.toString())
                setTimePost(time, postData?.postDate)
                setLike(view, postData?.like, likeCount)

            }
        }

        private fun setMediaPost(view: View, medias: ArrayList<MediaData>?, text: String?) {
            // ini punya media
            val textHashtag = view.findViewById<TextView>(R.id.textHashtag)
            val mediaWrap = view.findViewById<DynamicSquareLayout>(R.id.mediaWrap)
            val cardHashtagMedia = view.findViewById<MaterialCardView>(R.id.cardHashtagMedia)
            val mediaHashtag = view.findViewById<PosterSlider>(R.id.mediaHashtag)

            try {
                val posters: MutableList<Poster> = ArrayList()

                if (medias!!.size > 0) {
                    textHashtag.visibility = View.VISIBLE
                    mediaWrap.visibility = View.VISIBLE
                    cardHashtagMedia.visibility = View.VISIBLE
                    mediaHashtag.visibility = View.VISIBLE

                    setCaption(view, text)

                    for (media in medias) {
                        if (media.type == "image") {
                            posters.add(RemoteImage(media.link))
                        } else if (media.type == "video") {
                            val videoURI = Uri.parse(media.link)
                            posters.add(RemoteVideo(videoURI))
                        }
                        postData!!.media[0].link?.let { Log.d("link gambar nya ", it) }
                        Log.d("nama nya ", postData!!.name.toString())

                    }
                    mediaHashtag!!.setPosters(posters)
                }
//                else {
//                    setHashTag(textHashtag, postData?.tag)
//                    val postTextView = view.findViewById<TextView>(R.id.textStatusDetail)
//                    postTextView.text = text
//                }
            } catch (ex: java.lang.IllegalStateException) {

            }

        }

        fun setCaption(view: View, text: String?) {
            val tagView = view.findViewById<TextView>(R.id.textHashtagHashtag)
            val captionView = view.findViewById<TextView>(R.id.textCaption)
            if (text?.length!! > 99) {
                val cutCaption = text.removeRange(99, text.length)
                val caption = "$cutCaption... more"
                val spannableString = SpannableString(caption)
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        if (mContext is HashtagActivity) {
                            postData?.let { (mContext as HashtagActivity).detailPost(it) }
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
//                tagView.visibility = View.GONE
            } else {
                val textHashtag = view.findViewById<TextView>(R.id.textHashtag)
                setHashTag(textHashtag, postData?.tag)
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
                                    postData?.let { (mContext as HashtagActivity).detailPost(it) }
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
            val commentIcon = view.findViewById<ImageView>(R.id.icCommentHashtag)
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

        fun listenLikeIcon(view: View) {
            val likeIcon = view.findViewById<RelativeLayout>(R.id.likeLayoutHashtag)
            likeIcon.setOnClickListener {
                if (likeStatus == true) {
                    likeStatus = false
                    likeCount--
                    setLike(view, likeStatus, likeCount)
//                Add logic to hit end point like
                } else {
                    likeStatus = true
                    likeCount++
                    setLike(view, likeStatus, likeCount)
//                Add logic to hit endpont dislike
                }
            }
        }
    }
}

