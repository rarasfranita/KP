package com.example.lotus.ui.dm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.Respons
import com.example.lotus.models.User
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.dm.GetNewMsgAdapter.Holder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_new_message.*
import kotlinx.android.synthetic.main.layout_search_user.view.*

class NewMessageFragment : Fragment() {

    var names: ArrayList<String>? = null
    var token: String? = null
    var username: String? = null
    var newMesge = ArrayList<User>()
    lateinit var adapter: GetNewMsgAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_new_message, container, false)
        token = SharedPrefManager.getInstance(requireContext()).token.token
        username = SharedPrefManager.getInstance(requireContext()).user.username
        val edSearch :EditText = v.findViewById(R.id.edSearchbar)

//        edSearch.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(
//                charSequence: CharSequence,
//                i: Int,
//                i1: Int,
//                i2: Int
//            ) {
//            }
//
//            override fun onTextChanged(
//                charSequence: CharSequence,
//                i: Int,
//                i1: Int,
//                i2: Int
//            ) {
//            }
//
//            override fun afterTextChanged(editable: Editable) {
//                //after the change calling the method and passing the search input
//                filter(editable.toString())
//            }
//        })
        getRelation()

        listenAppToolbar(v)

        return v
    }

    private fun getRelation() {
        AndroidNetworking.get(EnvService.ENV_API + "/users/{username}/relation")
            .addPathParameter("username", username.toString())
            .addHeaders("Authorization", "Bearer " + token)
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
                                Log.d("dataJson",dataJson.toString())

                            }
                            newMesge = temp
                            Log.d("newMesge",newMesge.toString())
                            loadNewMsg(newMesge, rv_newMsg)

                        } else {
                            Log.d("Error Code", respon.code.toString())
                        }
                    }

                    override fun onError(error: ANError) {
                        Log.d("onError: Failed ${error.errorCode}", error.toString())
                    }
                })
    }

//    private fun filter(text: String) {
//        //new array list that will hold the filtered data
//        val filterdNames: ArrayList<User> = ArrayList()
//
//        //looping through existing elements
//        for (s in names) {
//            //if the existing elements contains the search input
//            if (s.toLowerCase().contains(text.toLowerCase())) {
//                //adding the element to filtered list
//                filterdNames.add(s)
//            }
//        }
//
//        //calling a method of the adapter class and passing the filtered list
//        adapter.filterList(filterdNames)
//    }


    private fun loadNewMsg(newMesge: ArrayList<User>, msg: RecyclerView?) {
        adapter = GetNewMsgAdapter(newMesge, this.context!!)
        adapter.notifyDataSetChanged()

        msg!!.adapter = adapter
        msg.setHasFixedSize(true)
        msg.layoutManager = LinearLayoutManager(this.context)
    }


    private fun listenAppToolbar(v: View?) {
        val toolbar: Toolbar = v?.findViewById(R.id.tbNewMessage) as Toolbar

        toolbar.setNavigationOnClickListener {
            view?.findNavController()?.navigate(R.id.action_newMessageFragment_to_homeFragmentDM)
        }

        toolbar.setOnMenuItemClickListener() {
            true
        }
    }
}

class GetNewMsgAdapter (private var nwMsg : ArrayList<User>, val context: Context) :
    RecyclerView.Adapter<Holder>(){


    private var mContext: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.mContext = context

        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_search_user,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return nwMsg.size
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.uname.text = nwMsg[position].username
        val extras = Bundle()
        val message = Holder(holder.itemView)
        message.bindFeed(nwMsg[position], context)
        message.search_user_container.setOnClickListener{
            val intent = Intent(context, GetMessage::class.java)
            extras.putString("name", nwMsg[position].name)
            extras.putString("username", nwMsg[position].username)
            extras.putString("userId", nwMsg[position]._id)
            extras.putString("profilePicture", nwMsg[position].avatar)
            intent.putExtras(extras)
            context.startActivity(intent)
            Log.d("usernameSSS", nwMsg[position].username.toString())
        }
        holder.bindFeed(nwMsg[position], context)
    }


    class Holder (val view: View) : RecyclerView.ViewHolder(view){
        val uname : TextView = view.findViewById(R.id.usernameSearch)
        val search_user_container : RelativeLayout = view.findViewById(R.id.search_user_container)
        var mContext: Context? = null
        private var userData: User? = null

        fun bindFeed(search: User, context: Context) {
            itemView.apply {
                userData = search
                mContext = context

                btnFollowSearch.visibility = View.GONE
                btnUnfollowSearch.visibility = View.GONE

                val usernameSearch: TextView =
                    view.findViewById<View>(R.id.usernameSearch) as TextView
                val profileSearch: ImageView =
                    view.findViewById<View>(R.id.profileSearch) as ImageView
                val bioSearch: TextView = view.findViewById<View>(R.id.bioSearch) as TextView

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
    }
//    fun filterList(filterdNames: ArrayList<User>) {
//        this.nwMsg = filterdNames
//        notifyDataSetChanged()
//    }
}