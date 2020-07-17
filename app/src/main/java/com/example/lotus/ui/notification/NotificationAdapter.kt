package com.example.lotus.ui.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.example.lotus.R
import com.example.lotus.models.Notification
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.home.PostFeedAdapter
import com.example.lotus.utils.dateToFormatTime
import com.google.android.material.card.MaterialCardView

class NotificationAdapter(private var notificationsDatas: ArrayList<Notification>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostFeedAdapter.ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_notification, parent, false))
    }

    override fun getItemCount(): Int = notificationsDatas?.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
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

        @SuppressLint("ResourceAsColor")
        fun bindNotification(notif: Notification, context: Context){
            if (notif.type == "FOLLOW"){
                wrapMedia.visibility = View.GONE
                if (notif.isFollowing == 1){
                    follow.setText("Following")
                    follow.setBackgroundColor(R.color.colorAccent)
                    follow.setTextColor(R.color.colorPrimary)
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
                    media.load(notif.media[0].link)
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

        fun setProfilePicture(url: String){
            profilePicture.load(url){
                transformations(CircleCropTransformation())
            }
        }
    }

}
