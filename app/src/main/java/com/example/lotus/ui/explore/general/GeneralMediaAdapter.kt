package com.example.lotus.ui.explore.general


import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asura.library.posters.Poster
import com.asura.library.posters.RemoteImage
import com.asura.library.posters.RemoteVideo
import com.example.lotus.R
import com.example.lotus.ui.explore.general.model.Data
import com.squareup.picasso.Picasso

class GeneralMediaAdapter(private val listExploreMedia: MutableList<Data>, val context: Context) :
    RecyclerView.Adapter<GeneralMediaAdapter.Holder>() {
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.mContext = context

        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_explore_mediaitem, parent, false)
        )
    }

    override fun getItemCount(): Int = listExploreMedia.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindFeed(listExploreMedia[position])
        //tambahan
        holder.more.setOnClickListener {
            if (mContext is GeneralActivity) {
                (mContext as GeneralActivity).more(listExploreMedia[position])
            }
        }
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val more: TextView = view.findViewById(R.id.more)
        private var imageView1: ImageView? = null
        private var imageView2: ImageView? = null
        private var imageView3: ImageView? = null
        fun bindFeed(data: Data) {
            itemView.apply {
                val hashtag : TextView = view.findViewById(R.id.tagar)
                //awal tagar(hashtag, data.hashtag)
                //tambahan, coba intent ke detail post, jadi gini
                data.hashtag?.let { tagar(hashtag, it) }
                imageView1 = findViewById(R.id.imageMediaExplore1) as ImageView
                imageView2 = findViewById(R.id.imageMediaExplore2) as ImageView
                imageView3 = findViewById(R.id.imageMediaExplore3) as ImageView

                val mediaa: MutableList<Poster> = ArrayList()

                val media1 = data.posts[0].media
                for (media in media1) {
                    if (media.type == "image") {
                        mediaa.add(RemoteImage(media.link))
                        Picasso.get().load(media.link).into(imageView1)
                    } else if (media.type == "video") {
                        val videoURI = Uri.parse(media.link)
                        mediaa.add(RemoteVideo(videoURI))
                    }
                }
                val media2 = data.posts[1].media
                for (media in media2) {
                    if (media.type == "image") {
                        mediaa.add(RemoteImage(media.link))
                        Picasso.get().load(media.link).into(imageView2)
                    } else if (media.type == "video") {
                        val videoURI = Uri.parse(media.link)
                        mediaa.add(RemoteVideo(videoURI))
                    }
                }
                val media3 = data.posts[2].media
                for (media in media3) {
                    if (media.type == "image") {
                        mediaa.add(RemoteImage(media.link))
                        Picasso.get().load(media.link).into(imageView3)
                    } else if (media.type == "video") {
                        val videoURI = Uri.parse(media.link)
                        mediaa.add(RemoteVideo(videoURI))
                    }
                }
            }
        }
        fun tagar(view: TextView, tags: String) {
            var tagar: String = ""
            tagar += "#$tags"
            view.text = tagar
        }
    }

}
