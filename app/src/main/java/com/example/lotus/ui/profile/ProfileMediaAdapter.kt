package com.example.lotus.ui.profile

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.bumptech.glide.Glide
import com.example.lotus.R
import com.example.lotus.models.Post

class ProfileMediaAdapter(val post: ArrayList<Post>, val context: Context) : RecyclerView.Adapter<ProfileMediaAdapter.Holder>() {
    var data: ArrayList<Post> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        data = post
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.listmedia,parent,false)
        )
    }

    override fun getItemCount(): Int = post.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindFeed(data[position], context)

    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        var mContext: Context? = null
        val image1 = view.findViewById<ImageView>(R.id.imageMediaProfile1)
        var index = 0

        fun bindFeed(data1: Post, context: Context) {
            mContext = context
            itemView.apply {
                if (data1.media?.get(0)?.type.toString() == "video"){
                    val videoURI = Uri.parse(data1.media?.get(0)?.link.toString())
                    mContext?.let {
                        Glide.with(it).load(videoURI).into(image1!!)
                    }
                }else{
                    image1.load(data1.media?.get(0)?.link)
                }
            }
        }
    }
}