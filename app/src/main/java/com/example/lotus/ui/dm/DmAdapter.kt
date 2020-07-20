package com.example.lotus.ui.dm

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.example.lotus.R
import com.example.lotus.models.DM.Channel.Dm
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.home.PostFeedAdapter


// TODO: 20/07/20 add read not read 
class DmAdapter(private var channelDm: ArrayList<Dm>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val token = SharedPrefManager.getInstance(context).token.token

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostFeedAdapter.ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_list_messages, parent, false))
    }

    override fun getItemCount(): Int = channelDm.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val extras = Bundle()
        val message = ItemViewHolder(holder.itemView)
        message.bindMessage(channelDm[position])
        message.ll2.setOnClickListener{
            val intent = Intent(context, GetMessage::class.java)
            extras.putString("name", channelDm[position].receiver!!.name)
            extras.putString("username", channelDm[position].receiver!!.username)
            extras.putString("channelId", channelDm[position]._id)
            extras.putString("userId", channelDm[position].receiver!!._id)
            extras.putString("profilePicture", channelDm[position].receiver!!.profilePicture)
            intent.putExtras(extras)
            context.startActivity(intent)
            Log.d("username", channelDm[position].receiver!!.username)
        }


    }

}

class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val lastMessage: TextView = view.findViewById(R.id.lastMessage)
    val username: TextView = view.findViewById(R.id.username)
    val profileMessage: ImageView = view.findViewById(R.id.profileMessage)
    val ll2: LinearLayout = view.findViewById(R.id.ll2)
//    val read = view.findViewById<ImageView>(R.id.read)

    fun bindMessage(dm: Dm) {
        lastMessage.text = dm.lastMessage!!.message
        username.text = dm.name.toString()
        setProfilePicture(dm.receiver!!.profilePicture)
    }

    fun setProfilePicture(url: String){
        profileMessage.load(url){
            transformations(CircleCropTransformation())
        }
    }


}
