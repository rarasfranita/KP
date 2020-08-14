package com.example.lotus.ui.relations

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.lotus.R
import com.example.lotus.models.Respon
import com.example.lotus.models.User
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.home.Constant
import com.example.lotus.ui.profile.ProfileActivity
import java.util.*
import kotlin.collections.ArrayList

class SearchUserFollowers(private var listSearchFollowers: ArrayList<User>, val context: Context) :
    RecyclerView.Adapter<SearchUserFollowers.Holder>() {
    val userFollowersData = listSearchFollowers
    val token = SharedPrefManager.getInstance(context).token.token
    val userID = SharedPrefManager.getInstance(context).user._id
    private var mContext: Context? = null
    private var isFollowing: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.mContext = context;

        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_list_followers, parent, false)
        )
    }

    override fun getItemCount(): Int = listSearchFollowers.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val usernameSrc = SharedPrefManager.getInstance(context).user.username

        val extras = Bundle()
        val search = Holder(holder.itemView)

        search.rlMessage.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            extras.putString("userID", listSearchFollowers[position]._id)
            intent.putExtras(extras)
            context.startActivity(intent)
        }
        holder.bindFeed(listSearchFollowers[position], context)

        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val item = Holder(holder.itemView)

            item.setToken(token.toString())
            item.setUserID(userID.toString())
            item.bindFeed(listSearchFollowers[position], context)
            item.follow.visibility = View.GONE
            item.unfollow.visibility = View.GONE
            // TODO: 14/08/20 item.unfollow.visibility = View.VISIBLE  and uncomment item.unfollow

//            item.unfollow.setOnClickListener {
//                val usernameTrg = listSearchFollowers[position].username
//                Log.d("usernameSrc", "$usernameSrc , usernameTarget, $usernameTrg")
//                AndroidNetworking.get(EnvService.ENV_API + "/users/$usernameSrc/unfollow/$usernameTrg")
//                    .addPathParameter("usernameSource", usernameSrc)
//                    .addPathParameter("usernameTarget", usernameTrg)
//                    .addHeaders("Authorization", "Bearer $token")
//                    .setPriority(Priority.HIGH)
//                    .build()
//                    .getAsObject(
//                        Respon::class.java,
//                        object : ParsedRequestListener<Respon> {
//                            override fun onResponse(respon: Respon) {
//                                if (respon.code.toString() == "200") {
//                                    item.setUnfollow()
//                                }
//                            }
//
//                            override fun onError(anError: ANError) {
//
//                            }
//                        })
//            }
        }
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val rlMessage: RelativeLayout = view.findViewById(R.id.rlMessage)
        val follow: Button = view.findViewById(R.id.btnFollowSearch)
        val unfollow: Button = view.findViewById(R.id.btnUnfollowSearch)

        var mContext: Context? = null
        private var userData: User? = null
        private var token: String = null.toString()
        private var userID: String = null.toString()


        fun setToken(token: String) {
            this.token = token
        }

        fun setUserID(userID: String) {
            this.userID = userID
        }


        fun bindFeed(searchFollowers: User, context: Context) {
            itemView.apply {

                userData = searchFollowers
                mContext = context

                val usernameSearch: TextView =
                    view.findViewById<View>(R.id.username) as TextView
                val profileSearch: ImageView =
                    view.findViewById<View>(R.id.profileMessage) as ImageView
                val bioSearch: TextView = view.findViewById<View>(R.id.lastMessage) as TextView

                usernameSearch.text = searchFollowers.username
                setProfilePicture(profileSearch, searchFollowers.avatar.toString())
                bioSearch.text = searchFollowers.bio
            }
        }

        private fun setProfilePicture(v: ImageView, image: String?) {
            if (image != null) {
                v.load(image) {
                    crossfade(true)
                    crossfade(300)
                    transformations(CircleCropTransformation())
                }
            }
        }
        

        @SuppressLint("ResourceAsColor")
        fun setUnfollow() {
            follow.visibility = View.VISIBLE
            unfollow.visibility = View.GONE
        }

    }

    fun filter(text: String) {
        val a = userFollowersData
        val filterdNames: ArrayList<User> = ArrayList()

        for (s in a) {
            if (s.username.toString().toLowerCase(Locale.ROOT)
                    .contains(text.toLowerCase(Locale.ROOT))
            ) {
                filterdNames.add(s)
            }

        }
        filterList(filterdNames)
    }

    private fun filterList(filterdNames: ArrayList<User>) {
        this.listSearchFollowers = filterdNames
        notifyDataSetChanged()
    }
}
