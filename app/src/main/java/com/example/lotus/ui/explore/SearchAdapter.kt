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
import com.example.lotus.models.DataSearch
import com.example.lotus.models.Respon
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.dm.DmAdapter
import com.example.lotus.ui.dm.GetMessage
import com.example.lotus.ui.home.Constant
import com.example.lotus.ui.home.HomeActivity
import com.example.lotus.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.layout_list_messages.view.*
import kotlinx.android.synthetic.main.layout_mainfeed_listitem.view.*
import java.util.*
import kotlin.collections.ArrayList

class SearchAdapter(private val listSearchUser: ArrayList<DataSearch>, val context: Context) :
    RecyclerView.Adapter<SearchAdapter.Holder>(), Filterable{
    val token = SharedPrefManager.getInstance(context).token.token
    val userID = SharedPrefManager.getInstance(context).user._id
    var searchUserFilterList = ArrayList<String>()
    var a = listSearchUser[0].username?.toArrayList()
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
            listSearchUser[position].username?.let { it1 -> Log.d("username", it1) }
        }
        holder.bindFeed(listSearchUser[position], context)
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val item = Holder(holder.itemView)

            item.setToken(token.toString())
            item.setUserID(userID.toString())
            item.bindFeed(listSearchUser[position], context)


            item.follow.setOnClickListener {
                val usernameTrg = listSearchUser[position].username
                Log.d("usernameSrc", "${usernameSrc} , usernameTarget, ${usernameTrg}")
                AndroidNetworking.get(EnvService.ENV_API + "/users/$usernameSrc/follow/$usernameTrg")
                    .addPathParameter("usernameSource", usernameSrc)
                    .addPathParameter("usernameTarget", usernameTrg)
                    .addHeaders("Authorization", "Bearer " + token)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsObject(
                        Respon::class.java,
                        object : ParsedRequestListener<Respon> {
                            override fun onResponse(respon: Respon) {
                                if (respon.code.toString() == "200") {
                                    Log.d("RESPON FOLLOWW", respon.data.toString())
                                    item.setFollowing()
                                } else {
                                    Log.e("ERROR!!!", "Following ${respon.code}")
                                }
                            }

                            override fun onError(anError: ANError) {
                                Log.e("ERROR!!!", "While following ${anError.errorCode}")

                            }
                        })
            }
            item.unfollow.setOnClickListener {
                val usernameTrg = listSearchUser[position].username
                Log.d("usernameSrc", "${usernameSrc} , usernameTarget, ${usernameTrg}")
                AndroidNetworking.get(EnvService.ENV_API + "/users/$usernameSrc/unfollow/$usernameTrg")
                    .addPathParameter("usernameSource", usernameSrc)
                    .addPathParameter("usernameTarget", usernameTrg)
                    .addHeaders("Authorization", "Bearer " + token)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsObject(
                        Respon::class.java,
                        object : ParsedRequestListener<Respon> {
                            override fun onResponse(respon: Respon) {
                                if (respon.code.toString() == "200") {
                                    Log.d("RESPON UNFOLLOWW", respon.data.toString())
                                    item.setUnfollow()
                                } else {
                                    Log.e("ERROR!!!", "Unfollowing ${respon.code}")
                                }
                            }

                            override fun onError(anError: ANError) {
                                Log.e("ERROR!!!", "While unfollowing ${anError.errorCode}")

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
        private var userData: DataSearch? = null
        private var token: String = null.toString()
        private var userID: String = null.toString()

        fun setToken(token: String) {
            this.token = token
        }

        fun setUserID(userID: String) {
            this.userID = userID
        }

        fun bindFeed(search: DataSearch, context: Context) {
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

    override fun getFilter():Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    searchUserFilterList = a!!
                } else {
                    val resultList = ArrayList<String>()
                    for (row in a!!) {
                        if (row.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    searchUserFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = searchUserFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                searchUserFilterList = results?.values as ArrayList<String>
                notifyDataSetChanged()
            }

        }
    }
}

fun String.toArrayList(): ArrayList<String> {
    return ArrayList(this.split("").drop(1).dropLast(1))
}
