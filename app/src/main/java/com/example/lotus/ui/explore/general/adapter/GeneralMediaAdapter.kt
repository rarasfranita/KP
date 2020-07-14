package com.example.lotus.ui.explore.general.adapter


import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lotus.R
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.explore.general.model.Data
import com.example.lotus.ui.explore.general.model.Post
import com.example.lotus.utils.DynamicSquareLayout
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
        holder.bindFeed(listExploreMedia[position], context)
        //tambahan
        holder.dynamicSquare1.setOnClickListener {
            if (mContext is GeneralActivity) {
                (mContext as GeneralActivity).detailPostFromExplore(listExploreMedia[position])
            }
        }
        holder.dynamicSquare2.setOnClickListener {
            if (mContext is GeneralActivity) {
                (mContext as GeneralActivity).detailPostFromExplore(listExploreMedia[position])
            }
        }
        holder.dynamicSquare3.setOnClickListener {
            if (mContext is GeneralActivity) {
                (mContext as GeneralActivity).detailPostFromExplore(listExploreMedia[position])
            }
        }
        holder.more.setOnClickListener {
            if (mContext is GeneralActivity) {
                (mContext as GeneralActivity).more(listExploreMedia[position])
            }
        }
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        var mContext: Context? = null
        val more: TextView = view.findViewById(R.id.moreMedia)
        var dynamicSquare1 = view.findViewById(R.id.dynamicSquare1) as DynamicSquareLayout
        var dynamicSquare2 = view.findViewById(R.id.dynamicSquare2) as DynamicSquareLayout
        var dynamicSquare3 = view.findViewById(R.id.dynamicSquare3) as DynamicSquareLayout
        var imageView1: ImageView? = null
        var imageView2: ImageView? = null
        var imageView3: ImageView? = null

        fun bindFeed(data: Data, context: Context) {
            itemView.apply {
                val hashtag: TextView = view.findViewById(R.id.tagar)
                data.hashtag?.let { tagar(hashtag, it) }
                mContext = context

                setMediaPost(view, data.posts)
            }
        }

        private fun setMediaPost(view: View, post: ArrayList<Post>) {
            imageView1 = view.findViewById(R.id.imageMediaExplore1) as ImageView
            imageView2 = view.findViewById(R.id.imageMediaExplore2) as ImageView
            imageView3 = view.findViewById(R.id.imageMediaExplore3) as ImageView



            for ((i, post) in post.withIndex()) {
                when (i) {
                    0 -> {
                        if (post.media[0].type == "image") {
                            Picasso.get().load(post.media[0].link)
                                .into(imageView1)
                            Log.d("ini link gambar post 1", post.media[0].link)
                            Log.d("ini text 1", post.text.toString())
                            Log.d("ini nama 1", post.name.toString())

                        } else if (post.media[0].type == "video") {
                            val videoURI = Uri.parse(post.media[0].link)
                            Log.d("link video ", videoURI.toString())
                            mContext?.let { Glide.with(it).load(videoURI).into(imageView1!!) }
                        }
                    }
                    1 -> {
                        if (post.media[0].type == "image") {
                            Picasso.get().load(post.media[0].link)
                                .into(imageView2)
                            Log.d("ini link gambar post 2", post.media[0].link)
                            Log.d("ini text 2", post.text.toString())
                            Log.d("ini nama 2", post.name.toString())

                        } else if (post.media[0].type == "video") {
                            val videoURI = Uri.parse(post.media[0].link)
                            Log.d("link video ", videoURI.toString())
                            mContext?.let { Glide.with(it).load(videoURI).into(imageView2!!) }
                        }
                    }
                    2 -> {
                        if (post.media[0].type == "image") {
                            Picasso.get().load(post.media[0].link)
                                .into(imageView3)
                            Log.d("ini link gambar post 3", post.media[0].link)
                            Log.d("ini text 3", post.text.toString())
                            Log.d("ini nama 3", post.name.toString())

                        } else if (post.media[0].type == "video") {
                            val videoURI = Uri.parse(post.media[0].link)
                            Log.d("link video ", videoURI.toString())
                            mContext?.let { Glide.with(it).load(videoURI).into(imageView3!!) }
                        }
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
