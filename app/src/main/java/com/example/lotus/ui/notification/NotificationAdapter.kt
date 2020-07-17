package com.example.lotus.ui.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.bumptech.glide.Glide
import com.example.lotus.R
import com.example.lotus.models.Notification
import com.example.lotus.models.Respon
import com.example.lotus.service.EnvService
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.home.PostFeedAdapter
import com.example.lotus.utils.dateToFormatTime
import com.example.lotus.utils.token
import com.google.android.material.card.MaterialCardView

class NotificationAdapter(private var notificationsDatas: ArrayList<Notification>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostFeedAdapter.ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_notification, parent, false))
    }

    override fun getItemCount(): Int = notificationsDatas?.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val username = "testaccount4"
        val item = ItemViewHolder(holder.itemView)
        item.bindNotification(notificationsDatas[position], context)
        item.cardNotification.setOnClickListener {
            if (notificationsDatas[position].type == "FOLLOW"){
                Toast.makeText(context, "TO PROFILE ${notificationsDatas[position].follower?.name}", Toast.LENGTH_SHORT).show()
            }else {
                val i: Intent = Intent(context, DetailPost::class.java)
                i.putExtra("postID", notificationsDatas[position].postId)

                if (context is NotificationActivity) {
                   context.detailPost(notificationsDatas[position].postId.toString())
                }
            }
        }

        item.follow.setOnClickListener {
            if (notificationsDatas[position].isFollowing!!.equals(0)){
                Log.d("KESINI GASIH", "")
                AndroidNetworking.get(EnvService.ENV_API + "/users/$username/follow/${notificationsDatas[position].follower?.username}")
                    .addHeaders("Authorization", "Bearer " + token)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsObject(
                        Respon::class.java,
                        object : ParsedRequestListener<Respon> {
                            @SuppressLint("ResourceAsColor")
                            override fun onResponse(respon: Respon) {
                                if (respon.code.toString() == "200") {
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

        fun bindNotification(notif: Notification, context: Context){
            if (notif.type == "FOLLOW"){
                wrapMedia.visibility = View.GONE
                if (notif.isFollowing == 1){
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
            follow.setBackgroundColor(R.color.black)
            follow.setTextColor(R.color.colorPrimary)
        }


        fun setProfilePicture(url: String){
            profilePicture.load(url){
                transformations(CircleCropTransformation())
            }
        }
    }

}

