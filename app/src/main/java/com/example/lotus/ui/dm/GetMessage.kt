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

        adapter = GetMessageAdapter(chatList, this);
        rv_listMessage.adapter = adapter

        val layoutManager = LinearLayoutManager(this)
        rv_listMessage.layoutManager = layoutManager

        mSocket.connect()
        mSocket.on(userId, onConnect)
        mSocket.on("updateChat", onUpdateChat)

        Log.d("Socket Connect", mSocket.connected().toString())
//        mSocket.on("updateChat", onUpdateChat)

    }

    var onConnect = Emitter.Listener { args ->
        this.runOnUiThread(Runnable {
            val data = args[0] as JSONObject

            val gson = Gson()
            val dataJson = gson.fromJson(data.toString(), Get::class.java)

            Log.d("Data", dataJson.toString())
            Log.d("DATA CHANNEL di getmessage", dataJson.channelId.toString())


            Log.d("Socket on", mSocket.connected().toString())

        })
    }
    var onUpdateChat = Emitter.Listener {
        val chat: Get = gson.fromJson(it[0].toString(), Get::class.java)
        chat.mine = MessageType.CHAT_MINE.index
        addItemToRecyclerView(chat)
    }

    private fun addItemToRecyclerView(get: Get) {
        runOnUiThread {
            chatList.add(get)
            adapter.notifyItemInserted(chatList.size)
            edMessage.setText("")
            rv_listMessage.scrollToPosition(chatList.size - 1) //move focus on last message
        }
    }

    private fun sendMessage() {
        val message = edMessage.text
        val bundle = getIntent().getExtras()
        val userid = bundle?.getString("userId")

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
                            chatlist = temp
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

    override fun getItemCount(): Int = chatList.size

    override fun getItemViewType(position: Int): Int {
        return chatList[position].mine!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = ViewHolder(holder.itemView)
//        chat.bindChat(chatList[position])
        val messageData = chatList[position]
        val content = messageData.text.toString()
        val mine = messageData.mine;

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