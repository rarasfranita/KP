package com.example.lotus.ui.explore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.baoyz.widget.PullRefreshLayout
import com.example.lotus.R
import com.example.lotus.models.Respons
import com.example.lotus.models.User
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.explore.general.GeneralActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null
    var dataSearch = ArrayList<User>()
    var username = SharedPrefManager.getInstance(this).user.username
    var token = SharedPrefManager.getInstance(this).token.token
    lateinit var adapter: SearchUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_search)

        listenAppToolbar()
        manager = supportFragmentManager
        val srlSearch: PullRefreshLayout = findViewById(R.id.srlSearch)
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

        getSearch(null)
        srlSearch.setOnRefreshListener {
            getSearch(srlSearch)
        }

    }

    private fun listenAppToolbar() {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.tbSearch)

        toolbar.setNavigationOnClickListener {
            this.onBackPressed()
        }

    }

    private fun getSearch(v: PullRefreshLayout?) {
        v?.setRefreshing(true)
        AndroidNetworking.get(EnvService.ENV_API + "/users")
            .addHeaders("Authorization", "Bearer " + token)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respons::class.java,
                object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        srlSearch.setRefreshing(false)
                        val gson = Gson()
                        val temp = ArrayList<User>()
                        if (respon.code.toString() == "200") {
                            Log.e("RESPON!!", respon.data.toString())
                            for ((i, res) in respon.data.withIndex()) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, User::class.java)
                                temp.add(dataJson)
                                Log.d("TEMP", temp.toString())
                            }
                            val context: Context = this@SearchActivity
                            dataSearch = temp
                            loadSearch(dataSearch, rvSearch)
                        }
                    }

                    override fun onError(anError: ANError) {
                        srlSearch.setRefreshing(false)
                        Log.e("ERROR!!!", "While search ${anError.errorCode}")

                    }
                })

    }

    private fun loadSearch(data: ArrayList<User>, search: RecyclerView) {
        adapter = SearchUser(data, this)
        adapter.notifyDataSetChanged()

        search.adapter = adapter
        search.setHasFixedSize(true)
        search.layoutManager = LinearLayoutManager(this)
    }
}