package com.example.lotus.ui.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lotus.R
import com.example.lotus.models.Post
import com.example.lotus.ui.home.PostFeedAdapter

class NotificationAdapter(private var notificationsData: ArrayList<Post>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        this.mContext = context;
        return PostFeedAdapter.ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_mainfeed_listitem, parent, false))
    }

    override fun getItemCount(): Int = notificationsData?.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}

