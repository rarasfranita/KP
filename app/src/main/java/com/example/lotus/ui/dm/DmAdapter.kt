package com.example.lotus.ui.dm

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.provider.ContactsContract
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
class DmAdapter(private var channelDm: ArrayList<Dm>, var context: Context) :
    RecyclerView.Adapter<DmAdapter.Holder>() {
    val token = SharedPrefManager.getInstance(context).token.token

    private var mContext: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.mContext = context
        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_list_messages, parent, false)
        )

    }

    override fun getItemCount(): Int = channelDm.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = Holder(holder.itemView)
        item.bindMessage(channelDm[position], context)
        val extras = Bundle()
        val message = Holder(holder.itemView)

        message.ll2.setOnClickListener {
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

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        var mContext: Context? = null
        val lastMessage: TextView = view.findViewById(R.id.lastMessage)
        val username: TextView = view.findViewById(R.id.username)
        val ll2: LinearLayout = view.findViewById(R.id.ll2)
        val read = view.findViewById<ImageView>(R.id.read)


        fun bindMessage(dm: Dm, context: Context) {
            itemView.apply {
                mContext = context
                val profileMessage: ImageView = view.findViewById(R.id.profileMessage)
                lastMessage.text = dm.lastMessage!!.message
                username.text = dm.name.toString()
                Log.d("nukanya", dm.receiver!!.profilePicture)
                setProfilePicture(profileMessage, dm.receiver.profilePicture)

                if (dm.isRead == 1) {
                    read.visibility = View.GONE
                } else {
                    read.visibility = View.VISIBLE
                }
            }

        }

        fun setProfilePicture(profpic: ImageView, url: String) {
            profpic.load(url) {
                transformations(CircleCropTransformation())
            }
        }


    }

}

