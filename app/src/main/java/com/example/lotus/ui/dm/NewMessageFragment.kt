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
import kotlinx.android.synthetic.main.layout_list_messages.view.*
import java.util.*
import kotlin.collections.ArrayList

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
        val edSearch: EditText = v.findViewById(R.id.edSearchbar)

        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                //after the change calling the method and passing the search input
                adapter.filter(editable.toString())
//                rv_newMsg.scrollToPosition(0)
//                adapter.notifyDataSetChanged()
            }
        })
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
                                Log.d("dataJson", dataJson.toString())

                            }
                            newMesge = temp
                            Log.d("newMesgeUsername", newMesge.toString())
                            loadNewMsg(newMesge,rv_newMsg)

                        } else {
                            Log.d("Error Code", respon.code.toString())
                        }
                    }

                    override fun onError(error: ANError) {
                        Log.d("onError: Failed ${error.errorCode}", error.toString())
                    }
                })
    }


    private fun loadNewMsg(newMesge: ArrayList<User>, msg: RecyclerView?) {
        adapter = GetNewMsgAdapter(newMesge,this.context!!)
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

    }
}

class GetNewMsgAdapter(var nwMsg: ArrayList<User>, val context: Context) :
    RecyclerView.Adapter<Holder>() {

    val messageDatas = nwMsg

    private var mContext: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.mContext = context

        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.layout_list_messages, parent, false)
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
        message.search_user_container.setOnClickListener {
            val intent = Intent(context, GetMessage::class.java)
            extras.putString("name", nwMsg[position].name)
            extras.putString("username", nwMsg[position].username)
            extras.putString("userId", nwMsg[position]._id)
            extras.putString("profilePicture", nwMsg[position].avatar)
            intent.putExtras(extras)
            context.startActivity(intent) }
        holder.bindFeed(nwMsg[position], context)

    }

    fun filter(text: String) {
        val a = messageDatas
        val filterdNames: ArrayList<User> = ArrayList()

        for (s in a) {
            if (s.username.toString().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                filterdNames.add(s)
            }
            Log.d("filterdNames", filterdNames.toString())

        }
        filterList(filterdNames)
    }

    private fun filterList(filterdNames: ArrayList<User>) {
        this.nwMsg = filterdNames
        Log.d("dsdsd", filterdNames.toString())
        notifyDataSetChanged()
    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view) {

        val uname: TextView = view.findViewById(R.id.username)
        val search_user_container: RelativeLayout = view.findViewById(R.id.rlMessage)
        var mContext: Context? = null
        private var userData: User? = null

        fun bindFeed(search: User, context: Context) {
            itemView.apply {
                userData = search
                mContext = context

                read.visibility = View.GONE

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
    }
}