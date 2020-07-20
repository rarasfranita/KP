package com.example.lotus.ui.dm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.DM.Channel.Dm
import com.example.lotus.models.DM.Get.Get
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
    var getChannel = ArrayList<Dm>()
    var chatlist = ArrayList<Chat>()
    var username = SharedPrefManager.getInstance(this).user.username
    var userId = SharedPrefManager.getInstance(this).user._id
    var token = SharedPrefManager.getInstance(this).token.token

    lateinit var mSocket: Socket;
    lateinit var adapter: GetMessageAdapter
    val gson: Gson = Gson()
    val chatList: ArrayList<Get> = arrayListOf();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_chatting)

        icSend.setOnClickListener(this)

        listMessages()

        try {
            mSocket = IO.socket("http://34.101.109.136:3000")
            Log.d("success", mSocket.id())

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fail", "Failed to connect")
        }

        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT, onConnect)

    }

    var onConnect = Emitter.Listener {

//        val data = args[0] as JSONObject
//        val gson = Gson()
//        val dataJson = gson.fromJson(data.toString(), Chat::class.java)
//        Log.d("Data", dataJson.toString())
//        Log.d("DATA CHANNEL di getmessage", dataJson.channelId.toString())
//        Log.d("Socket on", mSocket.connected().toString())

    }


    private fun sendMessage() {
        val message = edMessage.text
        val bundle = getIntent().getExtras()
        val userid = bundle?.getString("userId")
        Log.d("userId", userid.toString())

        AndroidNetworking.post(EnvService.ENV_API + "/users/{senderId}/dm")
            .addPathParameter("senderId", userId.toString())
            .addBodyParameter("userId", userid.toString())
            .addBodyParameter("message", message.toString())
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
                            temp.add(dataJson)
                            Log.e("Responchatlist", temp.toString())
                            chatlist = temp
//                            loadDm(chatlist, rv_listMessage)
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
        val bundle = getIntent().getExtras()
        val channelId = bundle?.getString("channelId")
        val username = bundle?.getString("username")
        val name = bundle?.getString("name")
        Log.d("channelId", channelId.toString())
        Log.d("userId", userId.toString())
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
                                temp.add(dataJson)
                                Log.d("dmList", temp.toString())
                            }
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

    fun loadDm(data: ArrayList<Get>, notification: RecyclerView) {
        adapter = GetMessageAdapter(data, this)
        adapter.notifyDataSetChanged()

        notification.adapter = adapter
        notification.setHasFixedSize(true)
        notification.layoutManager = LinearLayoutManager(this)
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
                Log.d("user inflating", "viewType : ${viewType}")
            }

            1 -> {
                view =
                    LayoutInflater.from(context).inflate(R.layout.row_chat_user, parent, false)
                Log.d("partner inflating", "viewType : ${viewType}")
            }
        }
        return ViewHolder(view!!)
    }

    override fun getItemCount(): Int = chatList.size

    override fun getItemViewType(position: Int): Int {
        return chatList[position].mine!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = ViewHolder(holder.itemView)
//        chat.bindChat(chatList[position])
        val messageData = chatList[position]
        val content = messageData.text.toString()
        Log.d("textnya ", content)
        val mine = messageData.mine;
        Log.d("mine ", mine.toString())

        when (mine) {
            CHAT_PARTNER -> {
                holder.message.setText(content)
            }
            CHAT_MINE -> {
                holder.message.setText(content)
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message = itemView.findViewById<TextView>(R.id.message)
//        val profPic = itemView.findViewById<ImageView>(R.id.profPic)

//        fun bindChat(get: Get) {
//            setProfilePicture(get.sender!!.avatar)
//        }
//        fun setProfilePicture(url: String){
//            profPic.load(url){
//                transformations(CircleCropTransformation())
//            }
//        }
    }


}
