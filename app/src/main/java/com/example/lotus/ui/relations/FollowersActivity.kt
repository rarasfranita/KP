package com.example.lotus.ui.relations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.Respons
import com.example.lotus.models.User
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.profile.ProfileActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search.*

class FollowersActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null
    var dataSearch = ArrayList<User>()
    private val myUsername = SharedPrefManager.getInstance(this).user.username
    private val token = SharedPrefManager.getInstance(this).token.token
    lateinit var adapter: SearchUserFollowers
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_followers)

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

        getSearchFollowers()
    }

    private fun listenAppToolbar() {
        val toolbar: MaterialToolbar = findViewById(R.id.tbSearch)

        toolbar.setNavigationOnClickListener {
            val intent =
                Intent(this@FollowersActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getSearchFollowers() {
        AndroidNetworking.get(EnvService.ENV_API + "/users/{username}/followers")
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

    private fun loadSearch(data: ArrayList<User>, searchFollowers: RecyclerView) {
        adapter = SearchUserFollowers(data, this)
        adapter.notifyDataSetChanged()

        searchFollowers.adapter = adapter
        searchFollowers.setHasFixedSize(true)
        searchFollowers.layoutManager = LinearLayoutManager(this)
    }
}