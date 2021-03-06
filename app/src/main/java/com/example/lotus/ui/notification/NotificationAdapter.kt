package com.example.lotus.ui.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.bumptech.glide.Glide
import com.example.lotus.R
import com.example.lotus.R.color.white
import com.example.lotus.models.Notification
import com.example.lotus.models.Respon
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.home.PostFeedAdapter
import com.example.lotus.utils.dateToFormatTime
import com.google.android.material.card.MaterialCardView

class NotificationAdapter(private var notificationsDatas: ArrayList<Notification>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val token = SharedPrefManager.getInstance(context).token.token

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostFeedAdapter.ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_notification, parent, false))
    }

    override fun getItemCount(): Int = notificationsDatas?.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val username = SharedPrefManager.getInstance(context).user.username
        val item = ItemViewHolder(holder.itemView)
        item.bindNotification(notificationsDatas[position], context)
        item.cardNotification.setOnClickListener {
            if (notificationsDatas[position].type == "FOLLOW"){
                if (context is NotificationActivity) {
                    context.gotoProfilePicture(notificationsDatas[position].follower?.id.toString())
                }
            }else {
                val i: Intent = Intent(context, DetailPost::class.java)
                i.putExtra("postID", notificationsDatas[position].postId)

                if (context is NotificationActivity) {
                   context.gotoDetailPost(notificationsDatas[position].postId.toString())
                }
            }
        }

        item.follow.setOnClickListener {
            if (notificationsDatas[position].isFollowing!!.equals(0)){
                AndroidNetworking.get(EnvService.ENV_API + "/users/$username/follow/${notificationsDatas[position].follower?.username}")
                    .addHeaders("Authorization", "Bearer " + token)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsObject(
                        Respon::class.java,
                        object : ParsedRequestListener<Respon> {
                            override fun onResponse(respon: Respon) {
                                if (respon.code.toString() == "200") {
                                    Log.d("Respon Data Follow", respon.data.toString())
                                    item.setFollowing()
                                }else {
                                    Log.e("ERROR!!!", "Following ${respon.code}")
                                }
                            }

                            override fun onError(anError: ANError) {
                                Log.e("ERROR!!!", "While following ${anError.errorCode}")

                            }
                        })
            }
        }

        item.setContext(context)
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val wrapMedia: RelativeLayout = view.findViewById(R.id.mediaWrap)
        val textName: TextView = view.findViewById(R.id.textUsernameNotification)
        val textNotification: TextView = view.findViewById(R.id.textNotification)
        val textTime: TextView = view.findViewById(R.id.textTimeNotification)
        val follow: Button = view.findViewById(R.id.btnFollow)
        val media: ImageView = view.findViewById(R.id.mediaNotification)
        val profilePicture: ImageView = view.findViewById(R.id.profileUserNotification)
        val cardNotification: MaterialCardView = view.findViewById(R.id.cardNotification)
        lateinit private var context: Context

        fun setContext(ctx: Context){
            this.context = ctx
        }

        fun bindNotification(notif: Notification, context: Context){
            if (notif.type == "FOLLOW"){
                wrapMedia.visibility = View.GONE
                Log.d("NOTIFICATION", notif.isFollowing.toString())
                if (notif.isFollowing!!.equals(1)){
                    setFollowing()
                }
                textNotification.text = "Start following you."
                textName.text = notif.follower?.username

                if (notif.follower?.profilePicture != null){
                    setProfilePicture(notif.follower?.profilePicture)
                }
            }else {
                follow.visibility = View.GONE
                textName.text = notif.sender?.username

                if (notif.media!!.size > 0){
                    if (notif.media[0].type == "video"){
                        val videoURI = Uri.parse(notif.media[0].link)
                        context.let {
                            Glide.with(it).load(videoURI).into(media)
                        }
                    }else if (notif.media[0].type == "image"){
                        media.load(notif.media[0].link)
                    }

                }

                if(notif.type == "LIKE"){
                    textNotification.text = "Like your post."
                }else if (notif.type == "COMMENT"){
                    textNotification.text = "Comment on your post."
                }

                if (notif.sender?.profilePicture != null){
                    setProfilePicture(notif.sender?.profilePicture)
                }
            }

            dateToFormatTime(textTime, notif.createdAt)
        }

        @SuppressLint("ResourceAsColor")
        fun setFollowing(){
            follow.setText("Following")
            follow.setBackgroundColor(white)
            follow.setTextColor(R.color.colorPrimary)
        }


        fun setProfilePicture(url: String){
            profilePicture.load(url){
                transformations(CircleCropTransformation())
            }
        }
    }

}

