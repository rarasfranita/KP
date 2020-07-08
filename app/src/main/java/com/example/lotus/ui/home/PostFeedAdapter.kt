package com.example.lotus.ui.home

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.asura.library.posters.Poster
import com.asura.library.posters.RemoteImage
import com.asura.library.posters.RemoteVideo
import com.asura.library.views.PosterSlider
import com.example.lotus.R
import com.example.lotus.models.Post
import kotlinx.android.synthetic.main.layout_mainfeed_listitem.view.*


class PostFeedAdapter(private val listPost: MutableList<Post>, val context: Context) : RecyclerView.Adapter<PostFeedAdapter.Holder>() {
    private var mContext: Context? = null

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
        holder.bindFeed(listPost[position])
        holder.comment.setOnClickListener {
            if (mContext is HomeActivity) {
                (mContext as HomeActivity).detailPost(listPost[position])
            }
        }
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val comment = view.findViewById<TextView>(R.id.image_comments_link)

        private var posterSlider: PosterSlider? = null

        fun bindFeed(post: Post){
            itemView.apply {
                username.text = post.name
                posterSlider = findViewById(R.id.postSlider) as PosterSlider
                val posters: MutableList<Poster> = ArrayList()
                val medias = post.media
                if (medias != null) {
                    for (media in medias) {
                        if (media.type == "image") {
                            posters.add(RemoteImage(media.link))
                        } else if (media.type == "video") {
                            val videoURI = Uri.parse(media.link)
                            posters.add(RemoteVideo(videoURI))
                        }
                    }
                }

                posterSlider!!.setPosters(posters)
                loadImageProfilePicturePost(profile_photo, "http://i.imgur.com/DvpvklR.png")
            }
        }

        fun loadImageProfilePicturePost(profpic: ImageView, url: String){
            profpic.load(url){
                crossfade(true)
                crossfade(300)
                transformations(CircleCropTransformation())
            }
        }
    }

}