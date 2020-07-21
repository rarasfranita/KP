package com.example.lotus.ui.dm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_maindm.*
import kotlinx.android.synthetic.main.fragment_homedm.*
import kotlinx.android.synthetic.main.layout_chatting.*


class HomeFragmentDM : Fragment() {

    var dmData = ArrayList<Dm>()
    var token: String? = null
    var userID: String? = null
    var username: String? = null


    lateinit var adapter: DmAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        token = SharedPrefManager.getInstance(requireContext()).token.token
        userID = SharedPrefManager.getInstance(requireContext()).user._id
        username = SharedPrefManager.getInstance(requireContext()).user.username


        val v = inflater.inflate(R.layout.fragment_homedm, container, false)
        val reload :PullRefreshLayout = v.findViewById(R.id.realodDM)
        listenAppToolbar(v)
        getListMessage(null)
        reload.setOnRefreshListener {
            getListMessage(reload)
        }
        return v
    }

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
