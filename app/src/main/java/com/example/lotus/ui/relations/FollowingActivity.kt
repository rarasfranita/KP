package com.example.lotus.ui.relations

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.Respon
import com.example.lotus.models.Respons
import com.example.lotus.models.User
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.home.Constant
import com.example.lotus.ui.profile.ProfileActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_following.*
import java.util.*
import kotlin.collections.ArrayList

class FollowingActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null
    var dataSearch = ArrayList<User>()
    private val myUsername = SharedPrefManager.getInstance(this).user.username
    private val token = SharedPrefManager.getInstance(this).token.token
    lateinit var adapter: SearchUserFollowings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_following)

        listenAppToolbar()
        manager = supportFragmentManager
        val edSearch: EditText = findViewById(R.id.edSearchbar)

        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        getSearchFollowings()
    }

    private fun listenAppToolbar() {
        val toolbar: MaterialToolbar = findViewById(R.id.tbSearch)
        toolbar.title = "Following"
        toolbar.setNavigationOnClickListener {
            val intent =
                Intent(this@FollowingActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getSearchFollowings() {
        AndroidNetworking.get(EnvService.ENV_API + "/users/{username}/followings")
            .addHeaders("Authorization", "Bearer $token")
            .addPathParameter("username", myUsername)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respons::class.java,
                object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        val gson = Gson()
                        val temp = ArrayList<User>()
                        if (respon.code.toString() == "200") {
                            for ((i, res) in respon.data.withIndex()) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, User::class.java)
                                temp.add(dataJson)
                                Log.d("temp", temp.toString())
                            }
                            dataSearch = temp
                            loadSearch(dataSearch, rvSearch)
                        }
                    }

                    override fun onError(anError: ANError) {
                    }
                })
    }

    private fun loadSearch(data: ArrayList<User>, listSearchFollowings: RecyclerView) {
        adapter = SearchUserFollowings(data, this)
        adapter.notifyDataSetChanged()

        listSearchFollowings.adapter = adapter
        listSearchFollowings.setHasFixedSize(true)
        listSearchFollowings.layoutManager = LinearLayoutManager(this)
    }
}

// adapter
class SearchUserFollowings(
    private var listSearchFollowings: ArrayList<User>,
    val context: Context) :
    RecyclerView.Adapter<SearchUserFollowings.Holder>() {

    val userFollowingsData = listSearchFollowings
    val token = SharedPrefManager.getInstance(context).token.token
    val userID = SharedPrefManager.getInstance(context).user._id
    private var mContext: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.mContext = context

        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_list_followers, parent, false)
        )
    }

    override fun getItemCount(): Int = listSearchFollowings.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val usernameSrc = SharedPrefManager.getInstance(context).user.username

        val extras = Bundle()
        val search = SearchUserFollowings.Holder(holder.itemView)

        search.rlMessage.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            extras.putString("userID", listSearchFollowings[position]._id)
            intent.putExtras(extras)
            context.startActivity(intent)
        }
        holder.bindFeed(listSearchFollowings[position], context)

        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val item = SearchUserFollowings.Holder(holder.itemView)

            item.setToken(token.toString())
            item.setUserID(userID.toString())
            item.bindFeed(listSearchFollowings[position], context)
            item.follow.visibility = View.GONE
            item.unfollow.visibility = View.GONE
            // TODO: 14/08/20 item.unfollow.visibility = View.VISIBLE and uncomment item.follow and ite,.unfollow

//            item.follow.setOnClickListener {
//                val usernameTrg = listSearchFollowings[position].username
//                Log.d("usernameSrc", "$usernameSrc , usernameTarget, $usernameTrg")
//                AndroidNetworking.get(EnvService.ENV_API + "/users/$usernameSrc/follow/$usernameTrg")
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
//                                    item.setFollowing()
//                                }
//                            }
//
//                            override fun onError(anError: ANError) {
//
//                            }
//                        })
//            }
//            item.unfollow.setOnClickListener {
//                val usernameTrg = listSearchFollowings[position].username
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


    class Holder (val view: View) : RecyclerView.ViewHolder(view) {
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


        fun bindFeed(searchFollowing: User, context: Context) {
            itemView.apply {

                userData = searchFollowing
                mContext = context

                val usernameSearch: TextView =
                    view.findViewById<View>(R.id.username) as TextView
                val profileSearch: ImageView =
                    view.findViewById<View>(R.id.profileMessage) as ImageView
                val bioSearch: TextView = view.findViewById<View>(R.id.lastMessage) as TextView

                usernameSearch.text = searchFollowing.username
                setProfilePicture(profileSearch, searchFollowing.avatar.toString())
                bioSearch.text = searchFollowing.bio
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
        val a = userFollowingsData
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
        this.listSearchFollowings = filterdNames
        notifyDataSetChanged()
    }


}
