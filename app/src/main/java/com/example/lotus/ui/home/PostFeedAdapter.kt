package com.example.lotus.ui.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.asura.library.posters.Poster
import com.asura.library.posters.RawVideo
import com.asura.library.posters.RemoteImage
import com.asura.library.views.PosterSlider
import com.example.lotus.R
import com.example.lotus.models.Post
import kotlinx.android.synthetic.main.layout_mainfeed_listitem.view.*


class PostFeedAdapter(private val listPost: ArrayList<Post>) : RecyclerView.Adapter<PostFeedAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_mainfeed_listitem, parent, false)
        )
    }

    override fun getItemCount(): Int = listPost?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindFeed(listPost[position])
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        private var posterSlider: PosterSlider? = null
        fun bindFeed(post: Post){
            itemView.apply {
                username.text = post.name
                posterSlider = findViewById(R.id.postSlider) as PosterSlider
                val posters: MutableList<Poster> = ArrayList()
                posters.add(RemoteImage(post.postpic))
                val myUri = Uri.parse("https://www.youtube.com/embed/tgbNymZ7vqY?autoplay=1\"")
                posters.add(RawVideo(R.raw.img_2073))
                posterSlider!!.setPosters(posters)
                loadImageProfilePicturePost(profile_photo, post.propic)
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