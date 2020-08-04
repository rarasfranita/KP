package com.example.lotus.ui.dm

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.DM.Get.Get
import com.example.lotus.models.DM.MessageType
import com.example.lotus.models.DM.Send.Chat
import com.example.lotus.models.Respon
import com.example.lotus.models.Respons
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_chatting.*
import org.json.JSONObject

class GetMessage : AppCompatActivity(), View.OnClickListener {

    var dmList = ArrayList<Get>()
    var chatlist = ArrayList<Chat>()
    var username = SharedPrefManager.getInstance(this).user.username
    var userId = SharedPrefManager.getInstance(this).user._id
    var token = SharedPrefManager.getInstance(this).token.token

    lateinit var userName: String
    lateinit var mSocket: Socket
    lateinit var adapter: GetMessageAdapter
    val gson: Gson = Gson()
    var chatList: ArrayList<Get> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_chatting)

        val extra = intent.getStringExtra("profilePicture")
        if (extra != null) {
            setProfilePicture(profPictre, extra)
        }


        icSend.setOnClickListener(this)

        listMessages()

        userName = intent.getStringExtra("username")!!

        try {
            mSocket = IO.socket("http://34.101.109.136:3000")
            Log.d("success", mSocket.id())

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fail", "Failed to connect")
        }

        adapter = GetMessageAdapter(chatList, this)
        rv_listMessage.adapter = adapter

        val layoutManager = LinearLayoutManager(this)
        rv_listMessage.layoutManager = layoutManager




        mSocket.connect()
        mSocket.on(userId, onConnect)

        Log.d("Socket Connect", mSocket.connected().toString())
//        mSocket.on("updateChat", onUpdateChat)

    }

    var onConnect = Emitter.Listener { args ->
        this.runOnUiThread(Runnable {
            val data = args[0] as JSONObject

            val gson = Gson()
            val dataJson = gson.fromJson(data.toString(), Get::class.java)
            mSocket.emit("subscribe", dataJson)
            Log.d("Data", dataJson.toString())
            Log.d("DATA CHANNEL di getmessage", dataJson.channelId.toString())
//            adapter.addItemToRecyclerView(dataJson)
            addItemToRecyclerView(dataJson)


            Log.d("Socket on", mSocket.connected().toString())

        })
    }

    fun addItemToRecyclerView(get: Get) {

        runOnUiThread {
            adapter.chatList.add(get)
            rv_listMessage.scrollToPosition(adapter.chatList.size - 1)
            Log.d("chatList.size", adapter.chatList.size.toString())
            adapter.notifyDataSetChanged()

        }

    }


    fun setProfilePicture(profpic: ImageView, url: String) {
        profpic.load(url) {
            transformations(CircleCropTransformation())
        }
    }

    private fun sendMessage() {
        val content = edMessage.text.toString()
        val bundle = intent.extras
        val userid = bundle?.getString("userId")

        // end point send message
        AndroidNetworking.post(EnvService.ENV_API + "/users/{senderId}/dm")
            .addPathParameter("senderId", userId.toString())
            .addBodyParameter("userId", userid.toString())
            .addBodyParameter("message", content)
            .addHeaders("Authorization", "Bearer " + token)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respon::class.java,
                object : ParsedRequestListener<Respon> {
                    override fun onResponse(respon: Respon) {
                        val gson = Gson()
                        val temp = ArrayList<Chat>()
                        if (respon.code.toString() == "200") {
                            val strRes = gson.toJson(respon.data)
                            val dataJson = gson.fromJson(strRes, Chat::class.java)
                            val chat = gson.fromJson(strRes, Get::class.java)
                            Log.d("Chat", chat.toString())
                            temp.add(dataJson)

                            chatlist = temp
                            chatlist.size
                            val message = Get(
                                chat.__v,
                                chat._id,
                                chat.channelId,
                                chat.createdAt,
                                chat.deleted,
                                chat.isRead,
                                chat.receiver,
                                chat.sender,
                                content,
                                chat.updatedAt,
                                MessageType.CHAT_MINE.index
                            )

                            addItemToRecyclerView(chat)
                            edMessage.setText("")

                            Log.d("messagemessage", message.toString())

                            mSocket.emit("newMessage", dataJson)
                        } else {
                            Log.d("Error Code", respon.code.toString())
                        }
                    }

                    override fun onError(error: ANError) {
                        Log.d("onError: Failed ${error.errorCode}", error.toString())
                    }
                })

    }

    private fun listMessages() {
        val bundle = intent.extras
        val channelId = bundle?.getString("channelId")
        val username = bundle?.getString("username")
        val name = bundle?.getString("name")
        nama.text = name
        usernama.text = username

        AndroidNetworking.get(EnvService.ENV_API + "/users/{userid}/dm/{channelId}")
            .addPathParameter("userid", userId.toString())
            .addPathParameter("channelId", channelId.toString())
            .addHeaders("Authorization", "Bearer " + token)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respons::class.java,
                object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        val gson = Gson()
                        val temp = ArrayList<Get>()
                        if (respon.code.toString() == "200") {
                            for ((i, res) in respon.data.withIndex()) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Get::class.java)
//                                temp.reverse()
                                temp.add(dataJson)
                                Log.d("dataJson", dataJson.toString())

                            }
                            temp.reverse()
                            dmList = temp
                            loadDm(dmList, rv_listMessage)

                        } else {
                            Log.d("Error Code", respon.code.toString())
                        }
                    }

                    override fun onError(error: ANError) {
                        Log.d("onError: Failed ${error.errorCode}", error.toString())
                    }
                })
    }


    fun backToDM(view: View) {
        this.onBackPressed()
    }

    fun loadDm(data: ArrayList<Get>, dm: RecyclerView) {
        adapter = GetMessageAdapter(data, this)
        adapter.notifyDataSetChanged()


        dm.adapter = adapter
        dm.setHasFixedSize(true)
        dm.layoutManager = LinearLayoutManager(this)

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.icSend -> sendMessage()
        }
    }
}

class GetMessageAdapter(val chatList: ArrayList<Get>, val context: Context) :
    RecyclerView.Adapter<GetMessageAdapter.ViewHolder>() {
    val CHAT_PARTNER = 0
    val CHAT_MINE = 1
    var userId = SharedPrefManager.getInstance(context).user._id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        Log.d("chatlist size", chatList.size.toString())
        var view: View? = null
        when (viewType) {
            0 -> {
                view =
                    LayoutInflater.from(context).inflate(R.layout.row_chat_partner, parent, false)
            }

            1 -> {
                view =
                    LayoutInflater.from(context).inflate(R.layout.row_chat_user, parent, false)
            }
        }
        return ViewHolder(view!!)


    }

//    fun addItemToRecyclerView(get: Get) {
//
//        chatList.add(get)
////        rv_listMessage.scrollToPosition(chatList.size - 1) //move focus on last message
//        notifyDataSetChanged()
//
//    }

    override fun getItemCount(): Int = chatList.size

    override fun getItemViewType(position: Int): Int {
        return chatList[position].mine!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val messageData = chatList[position]
        val content = messageData.text.toString()
        val mine = messageData.mine
        val a = messageData.sender.avatar

        Log.d("messageData", messageData.toString())
        fun setProfilePicture(profpic: ImageView, url: String) {
            profpic.load(url) {
                transformations(CircleCropTransformation())
            }
        }

        when (mine) {
            CHAT_PARTNER -> {
                holder.message.text = content
                setProfilePicture(holder.ava, a)
            }
            CHAT_MINE -> {
                holder.message.text = content
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var mContext: Context? = null
        val message: TextView = view.findViewById(R.id.message)
        val ava: ImageView = view.findViewById(R.id.avatr)
    }

}
