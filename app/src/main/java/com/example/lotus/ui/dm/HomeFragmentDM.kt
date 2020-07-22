package com.example.lotus.ui.dm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.baoyz.widget.PullRefreshLayout
import com.example.lotus.R
import com.example.lotus.models.DM.Channel.Dm
import com.example.lotus.models.DM.Get.Get
import com.example.lotus.models.Respons
import com.example.lotus.models.User
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.home.HomeActivity
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_maindm.*
import kotlinx.android.synthetic.main.fragment_homedm.*
import kotlinx.android.synthetic.main.layout_chatting.*
import kotlinx.android.synthetic.main.layout_list_messages.view.*
import org.json.JSONObject

// TODO: 21/07/20 socket
class HomeFragmentDM : Fragment() {

    var dmData = ArrayList<Dm>()
    var token: String? = null
    var userID: String? = null
    var username: String? = null


    lateinit var adapter: DmAdapter
    lateinit var mSocket: Socket


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        token = SharedPrefManager.getInstance(requireContext()).token.token
        userID = SharedPrefManager.getInstance(requireContext()).user._id
        username = SharedPrefManager.getInstance(requireContext()).user.username

        try {
            mSocket = IO.socket("http://34.101.109.136:3000")
            Log.d("success", mSocket.id())

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fail", "Failed to connect")
        }

        mSocket.connect()
//        mSocket.on(userID, onConnect)
        val v = inflater.inflate(R.layout.fragment_homedm, container, false)
        val reload :PullRefreshLayout = v.findViewById(R.id.realodDM)
        listenAppToolbar(v)
        getListMessage(null)
        reload.setOnRefreshListener {
            getListMessage(reload)
        }
        return v
    }
//    var onConnect = Emitter.Listener { args ->
//        val data = args[0] as JSONObject
//
//        val gson = Gson()
//        val dataJson = gson.fromJson(data.toString(), Dm::class.java)
//        mSocket.emit("subscribe", dataJson)
//        Log.d("Data", dataJson.toString())
//        adapter.addItemToRecyclerView(dataJson)
//
//
//        Log.d("Socket on", mSocket.connected().toString())
//
//    }


    private fun listenAppToolbar(v: View?) {
        val toolbar: Toolbar = v?.findViewById(R.id.tbMessage) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
        }

        toolbar.setOnMenuItemClickListener(){
            when (it.itemId){
                R.id.fragmentNewDM ->
                    view?.findNavController()?.navigate(R.id.action_homeFragmentDM_to_newMessageFragment)

            }
            true
        }
    }

    private fun getListMessage(v: PullRefreshLayout?) {
        v?.setRefreshing(true)
        AndroidNetworking.get(EnvService.ENV_API + "/users/{userid}/dm")
            .addPathParameter("userid", userID.toString())
            .addHeaders("Authorization", "Bearer " + token)
            .setTag(this)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respons::class.java,
                object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        realodDM.setRefreshing(false)
                        val gson = Gson()
                        val temp = ArrayList<Dm>()
                        if (respon.code.toString() == "200") {
                            for ((i, res) in respon.data.withIndex()) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Dm::class.java)
                                temp.add(dataJson)
                                Log.d("data", dataJson.toString())
                            }
                            dmData = temp

                            if (dmData.size < 1) {
                                dataNull.visibility = View.VISIBLE
                            } else {
                                loadDm(dmData, rv_listChat)
                            }
                        } else {
                        }
                    }

                    override fun onError(anError: ANError) {
                        realodDM.setRefreshing(false)
                        Log.d("Errornya disini kah?", anError.toString())
                    }
                })

    }

    fun loadDm(data: ArrayList<Dm>, notification: RecyclerView) {
        val context: Context = this.requireContext()
        adapter = DmAdapter(data, context)
        adapter.notifyDataSetChanged()

        notification.adapter = adapter
        notification.setHasFixedSize(true)
        notification.layoutManager = LinearLayoutManager(context)
    }

}

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

    fun addItemToRecyclerView(dm: Dm) {

        channelDm.add(dm)
//        rv_listMessage.scrollToPosition(chatList.size - 1) //move focus on last message
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int = channelDm.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = Holder(holder.itemView)
        item.bindMessage(channelDm[position], context)
        val extras = Bundle()
        val message = Holder(holder.itemView)

        message.rlMessage.setOnClickListener {
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
        val username: TextView = view.findViewById(R.id.username)
        val rlMessage: RelativeLayout = view.findViewById(R.id.rlMessage)

        fun bindMessage(dm: Dm, context: Context) {
            itemView.apply {
                btnUnfollowSearch.visibility = View.GONE
                btnFollowSearch.visibility = View.GONE
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
