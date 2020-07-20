package com.example.lotus.ui.explore

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.baoyz.widget.PullRefreshLayout
import com.example.lotus.R
import com.example.lotus.models.CommentRowModel
import com.example.lotus.models.DataSearch
import com.example.lotus.models.Respons
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.explore.general.adapter.GeneralMediaAdapter
import com.example.lotus.ui.explore.general.model.Data
import com.example.lotus.ui.home.HomeActivity
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_explore_general.*
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null
    var dataSearch = ArrayList<DataSearch>()
    var username = SharedPrefManager.getInstance(this).user.username
    var token = SharedPrefManager.getInstance(this).token.token
    lateinit var adapter: SearchAdapter
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

        getSearch(null)
        srlSearch.setOnRefreshListener {
            getSearch(srlSearch)

        }


        user_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })
    }


    private fun listenAppToolbar() {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.tbSearch)

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, GeneralActivity::class.java)
            startActivity(intent)
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
                        val temp = ArrayList<DataSearch>()
                        if (respon.code.toString() == "200") {
                            Log.e("RESPON!!", respon.data.toString())
                            for ((i, res) in respon.data.withIndex()) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, DataSearch::class.java)
                                temp.add(dataJson)
                                Log.d("TEMP", temp.toString())
                            }
                            val context : Context = this@SearchActivity
                            adapter = SearchAdapter(temp, context)
                            dataSearch = temp
                            loadSearch(dataSearch as ArrayList<DataSearch>, findViewById(R.id.rvSearch))
                        }
                    }

                    override fun onError(anError: ANError) {
                        srlSearch.setRefreshing(false)
                        Log.e("ERROR!!!", "While search ${anError.errorCode}")

                    }
                })

    }

    private fun loadSearch(data: ArrayList<DataSearch>, search: RecyclerView) {
        search.setHasFixedSize(true)
        search.layoutManager = LinearLayoutManager(this)
        val adapter = SearchAdapter(data, this)
        adapter.notifyDataSetChanged()
        search.adapter = adapter
    }
}