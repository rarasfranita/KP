package com.example.lotus.ui.explore


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.lotus.R
import com.example.lotus.models.Respon
import com.example.lotus.models.User
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.home.Constant
import com.example.lotus.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.layout_list_messages.view.*
import java.util.*
import kotlin.collections.ArrayList

class SearchUser(private var listSearchUser: ArrayList<User>, val context: Context) :
    RecyclerView.Adapter<SearchUser.Holder>() {
    val userData = listSearchUser
    val token = SharedPrefManager.getInstance(context).token.token
    val userID = SharedPrefManager.getInstance(context).user._id
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.mContext = context;

        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_list_messages, parent, false)
        )
    }

    override fun getItemCount(): Int = listSearchUser.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val usernameSrc = SharedPrefManager.getInstance(context).user.username

        val extras = Bundle()
        val search = Holder(holder.itemView)

        search.rlMessage.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            extras.putString("userID", listSearchUser[position]._id)
            intent.putExtras(extras)
            context.startActivity(intent)
        }
        holder.bindFeed(listSearchUser[position], context)
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val item = Holder(holder.itemView)

            item.setToken(token.toString())
            item.setUserID(userID.toString())
            item.bindFeed(listSearchUser[position], context)


            item.follow.setOnClickListener {
                val usernameTrg = listSearchUser[position].username
                Log.d("usernameSrc", "$usernameSrc , usernameTarget, $usernameTrg")
                AndroidNetworking.get(EnvService.ENV_API + "/users/$usernameSrc/follow/$usernameTrg")
                    .addPathParameter("usernameSource", usernameSrc)
                    .addPathParameter("usernameTarget", usernameTrg)
                    .addHeaders("Authorization", "Bearer $token")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsObject(
                        Respon::class.java,
                        object : ParsedRequestListener<Respon> {
                            override fun onResponse(respon: Respon) {
                                if (respon.code.toString() == "200") {
                                    item.setFollowing()
                                }
                            }

                            override fun onError(anError: ANError) {

                            }
                        })
            }
            item.unfollow.setOnClickListener {
                val usernameTrg = listSearchUser[position].username
                Log.d("usernameSrc", "$usernameSrc , usernameTarget, $usernameTrg")
                AndroidNetworking.get(EnvService.ENV_API + "/users/$usernameSrc/unfollow/$usernameTrg")
                    .addPathParameter("usernameSource", usernameSrc)
                    .addPathParameter("usernameTarget", usernameTrg)
                    .addHeaders("Authorization", "Bearer $token")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsObject(
                        Respon::class.java,
                        object : ParsedRequestListener<Respon> {
                            override fun onResponse(respon: Respon) {
                                if (respon.code.toString() == "200") {
                                    item.setUnfollow()
                                }
                            }

                            override fun onError(anError: ANError) {

                            }
                        })
            }
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

        fun bindFeed(search: User, context: Context) {
            itemView.apply {
                read.visibility = View.GONE

                userData = search
                mContext = context

                val usernameSearch: TextView =
                    view.findViewById<View>(R.id.username) as TextView
                val profileSearch: ImageView =
                    view.findViewById<View>(R.id.profileMessage) as ImageView
                val bioSearch: TextView = view.findViewById<View>(R.id.lastMessage) as TextView

                usernameSearch.text = search.username
                setProfilePicture(profileSearch, search.avatar.toString())
                bioSearch.text = search.bio
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
        fun setFollowing() {
            unfollow.visibility = View.VISIBLE
            follow.visibility = View.GONE
        }

        @SuppressLint("ResourceAsColor")
        fun setUnfollow() {
            unfollow.visibility = View.GONE
            follow.visibility = View.VISIBLE
        }
    }

    fun filter(text: String) {
        val a = userData
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
        this.listSearchUser = filterdNames
        notifyDataSetChanged()
    }

}