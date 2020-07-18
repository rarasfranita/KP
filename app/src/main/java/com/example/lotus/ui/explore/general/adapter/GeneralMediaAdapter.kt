package com.example.lotus.ui.explore.general.adapter


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lotus.R
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.explore.general.model.Data
import com.example.lotus.ui.explore.general.model.Post
import com.example.lotus.ui.explore.hashtag.HashtagActivity
import com.example.lotus.ui.login.LoginActivity
import com.example.lotus.utils.DynamicSquareLayout
import com.squareup.picasso.Picasso


class GeneralMediaAdapter(
    private val listExploreMedia: MutableList<Data>, var context: Context
) : RecyclerView.Adapter<GeneralMediaAdapter.Holder>() {

    var data: Post? = null
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
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        var mContext: Context? = null
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
                data.posts?.let { setMediaPost(view, it) }
                listenSendhashtag(view, data)
                listenSendId(view, data)
                Log.d("hashtagnya", data.hashtag.toString())
            }
        }

        fun checkLogin() {
            if (!(this.mContext?.let { SharedPrefManager.getInstance(it).isLoggedIn })!!){
                mContext!!.startActivity(Intent(mContext, LoginActivity::class.java))
            }
        }

        fun listenSendhashtag(view: View, data: Data) {
            val more: TextView = view.findViewById(R.id.moreMedia)
            more.setOnClickListener {
                checkLogin()
                val more = Intent(mContext, HashtagActivity::class.java)
                val ani = data.hashtag
                val bundle = Bundle()
                bundle.putString("hashtag", ani)
                val dataPost = DetailPost()
                dataPost.arguments = bundle
                more.putExtras(bundle)
                mContext!!.startActivity(more)
            }
        }

        fun listenSendId(view: View, data: Data) {
            val dynamicSquare1: DynamicSquareLayout = view.findViewById(R.id.dynamicSquare1)
            val dynamicSquare2: DynamicSquareLayout = view.findViewById(R.id.dynamicSquare2)
            val dynamicSquare3: DynamicSquareLayout = view.findViewById(R.id.dynamicSquare3)
            dynamicSquare1.setOnClickListener {
                val ani = data.posts?.get(0)?.id
                val bundle = Bundle()
                bundle.putString("id", ani)
                val dataPost = DetailPost()
                dataPost.arguments = bundle
                if (mContext is GeneralActivity) {
                    checkLogin()
                    (mContext as GeneralActivity).detailPost(ani.toString())
                }
            }
            dynamicSquare2.setOnClickListener {
                val ani = data.posts?.get(1)?.id
                val bundle = Bundle()
                bundle.putString("id", ani)
                val dataPost = DetailPost()
                dataPost.arguments = bundle
                if (mContext is GeneralActivity) {
                    checkLogin()
                    (mContext as GeneralActivity).detailPost(ani.toString())
                }
            }
            dynamicSquare3.setOnClickListener {
                val ani = data.posts?.get(2)?.id
                val bundle = Bundle()
                bundle.putString("id", ani)
                val dataPost = DetailPost()
                dataPost.arguments = bundle
                if (mContext is GeneralActivity) {
                    checkLogin()
                    (mContext as GeneralActivity).detailPost(ani.toString())
                }
            }
        }

        private fun setMediaPost(view: View, post: ArrayList<Post>) {
            imageView1 = view.findViewById(R.id.imageMediaExplore1) as ImageView
            imageView2 = view.findViewById(R.id.imageMediaExplore2) as ImageView
            imageView3 = view.findViewById(R.id.imageMediaExplore3) as ImageView

//            try {
                when (post.size) {
                    3 -> {
                        for ((i, post) in post.withIndex()) {
                            when (i) {
                                0 -> {
                                    if (post.media!![0].type == "image") {
                                        Picasso.get().load(post.media[0].link)
                                            .into(imageView1)
                                    } else if (post.media[0].type == "video") {
                                        val videoURI = Uri.parse(post.media[0].link)
                                        mContext?.let {
                                            Glide.with(it).load(videoURI).into(imageView1!!)
                                        }
                                    }
                                }
                                1 -> {
                                    if (post.media!![0].type == "image") {
                                        Picasso.get().load(post.media[0].link)
                                            .into(imageView2)
                                    } else if (post.media[0].type == "video") {
                                        val videoURI = Uri.parse(post.media[0].link)
                                        mContext?.let {
                                            Glide.with(it).load(videoURI).into(imageView2!!)
                                        }
                                    }
                                }
                                2 -> {
                                    if (post.media!![0].type == "image") {
                                        Picasso.get().load(post.media[0].link)
                                            .into(imageView3)
                                    } else if (post.media[0].type == "video") {
                                        val videoURI = Uri.parse(post.media[0].link)
                                        mContext?.let {
                                            Glide.with(it).load(videoURI).into(imageView3!!)
                                        }
                                    }
                                }
                            }
                        }

                    }
                    2 -> {
                        dynamicSquare3.visibility = View.INVISIBLE
                        for ((i, post) in post.withIndex()) {
                            when (i) {
                                0 -> {
                                    if (post.media!![0].type == "image") {
                                        Picasso.get().load(post.media[0].link)
                                            .into(imageView1)
                                    } else if (post.media[0].type == "video") {
                                        val videoURI = Uri.parse(post.media[0].link)
                                        mContext?.let {
                                            Glide.with(it).load(videoURI).into(imageView1!!)
                                        }
                                    }
                                }
                                1 -> {
                                    if (post.media!![0].type == "image") {
                                        Picasso.get().load(post.media[0].link)
                                            .into(imageView2)
                                    } else if (post.media[0].type == "video") {
                                        val videoURI = Uri.parse(post.media[0].link)
                                        mContext?.let {
                                            Glide.with(it).load(videoURI).into(imageView2!!)
                                        }
                                    }
                                }
                            }
                        }

                    }
                    1 -> {
                        dynamicSquare2.visibility = View.INVISIBLE
                        dynamicSquare3.visibility = View.INVISIBLE
                        for ((i, post) in post.withIndex()) {
                            when (i) {
                                0 -> {
                                    if (post.media!![0].type == "image") {
                                        Picasso.get().load(post.media[0].link)
                                            .into(imageView1)
                                    } else if (post.media[0].type == "video") {
                                        val videoURI = Uri.parse(post.media[0].link)
                                        mContext?.let {
                                            Glide.with(it).load(videoURI).into(imageView1!!)
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
//            } catch (ex: IndexOutOfBoundsException) {
//            }
        }


        private fun tagar(view: TextView, tags: String) {
            var tagar = ""
            tagar += "#$tags"
            view.text = tagar
        }
    }


}

