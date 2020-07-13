package com.example.lotus.ui.explore.general


import android.content.Context
import android.net.Uri
import android.util.Log
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
import com.example.lotus.models.MediaData
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
        holder.bindFeed(listExploreMedia[position], context)
        //tambahan
        holder.imageView1?.setOnClickListener {
            if (mContext is GeneralActivity) {
                (mContext as GeneralActivity).detailPostFromExplore(listExploreMedia[position])
            }
        }
        holder.imageView2?.setOnClickListener {
            if (mContext is GeneralActivity) {
                (mContext as GeneralActivity).detailPostFromExplore(listExploreMedia[position])
            }
        }
        holder.imageView3?.setOnClickListener {
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
        private var postData: Data? = null
        val more: TextView = view.findViewById(R.id.moreMedia)
        var imageView1: ImageView? = null
        var imageView2: ImageView? = null
        var imageView3: ImageView? = null

        fun bindFeed(data: Data, context: Context) {
            itemView.apply {
                val hashtag: TextView = view.findViewById(R.id.tagar)
                data.hashtag?.let { tagar(hashtag, it) }
                postData = data
                mContext = context
                setMediaPost(view, data.posts[0].media)
            }
        }

        private fun setMediaPost(view: View, medias: ArrayList<MediaData>) {
            imageView1 = view.findViewById(R.id.imageMediaExplore1) as ImageView
            imageView2 = view.findViewById(R.id.imageMediaExplore2) as ImageView
            imageView3 = view.findViewById(R.id.imageMediaExplore3) as ImageView

            var hashtag = postData?.hashtag
            val mediaa: MutableList<Poster> = ArrayList()

            if (postData!!.posts[0].media[0].type == "image") {
                mediaa.add(RemoteImage(medias[0].link))
                Log.d("hashtag ", postData?.hashtag.toString())
                Picasso.get().load(postData?.posts?.get(0)?.media?.get(0)?.link)
                    .into(imageView1)
                Picasso.get().load(postData?.posts?.get(1)?.media?.get(0)?.link)
                    .into(imageView2)
                Picasso.get().load(postData?.posts?.get(2)?.media?.get(0)?.link)
                    .into(imageView3)

                Log.d("ini link gambar post 1", postData?.posts!![0].media[0].link)
                Log.d("ini text 1", postData?.posts!![0].text.toString())
                Log.d("ini nama 1", postData?.posts!![0].name.toString())
                Log.d("ini link gambar post 2", postData?.posts!![1].media[0].link)
                Log.d("ini text 2", postData?.posts!![1].text.toString())
                Log.d("ini nama 2", postData?.posts!![1].name.toString())
                Log.d("ini link gambar post 3", postData?.posts!![2].media[0].link)
                Log.d("ini text 3", postData?.posts!![2].text.toString())
                Log.d("ini nama 3", postData?.posts!![2].name.toString())


            } else if (medias[0].type == "video") {
                val videoURI = Uri.parse(medias[0].link)
                mediaa.add(RemoteVideo(videoURI))
            }

        }


        fun tagar(view: TextView, tags: String) {
            var tagar: String = ""
            tagar += "#$tags"
            view.text = tagar
        }
    }


}
