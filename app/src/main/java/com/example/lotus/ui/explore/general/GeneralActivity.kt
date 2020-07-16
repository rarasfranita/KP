package com.example.lotus.ui.explore.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.baoyz.widget.PullRefreshLayout
import com.example.lotus.R
import com.example.lotus.models.Respons
import com.example.lotus.service.EnvService
//import com.example.lotus.ui.explore.detailpost.DetailPostHashtag
import com.example.lotus.ui.explore.general.adapter.GeneralMediaAdapter
import com.example.lotus.ui.explore.general.adapter.GeneralTextAdapter
import com.example.lotus.ui.explore.general.fragment.ListMediaGeneral
import com.example.lotus.ui.explore.general.fragment.ListTextGeneral
import com.example.lotus.ui.explore.general.model.Data
import com.example.lotus.ui.home.HomeActivity
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_explore_general.*


class GeneralActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null
    private val token = "5f09a143ff07b60aaafd008d"
    var dataExplore = ArrayList<Data>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_explore_general)

        listenAppToolbar()
        manager = supportFragmentManager
        val srlMediaGeneral: PullRefreshLayout = findViewById(R.id.srlMediaGeneral)

        srlMediaGeneral.setOnRefreshListener {
            dataExplore.clear()
            getExploreMedia()
            getExploreText()

        }
        val tabLayout: TabLayout = findViewById(R.id.tabs)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(ListMediaGeneral(), "Media")
        viewPagerAdapter.addFragment(ListTextGeneral(), "Text")

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        AndroidNetworking.initialize(applicationContext)
        dataExplore.clear()
        getExploreMedia()
        getExploreText()

    }

    private fun listenAppToolbar() {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.tbExplore)

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getExploreMedia() {
        dataExplore.clear()
        srlMediaGeneral.setRefreshing(true)
        AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore?username=testaccount1&index=0&type=media")
            .addHeaders("Authorization", "Bearer $token")
            .setTag(this)
            .setPriority(Priority.LOW)
            .build()
            .getAsObject(Respons::class.java, object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        dataExplore.clear()
                        srlMediaGeneral.setRefreshing(false)
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            dataExplore.clear()
                            for (res in respon.data) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Data::class.java)
                                dataExplore.add(dataJson)
                            }
                            loadExploreMedia(dataExplore, findViewById(R.id.rvExploreMedia))

                        } else {
                        }
                    }

                    override fun onError(anError: ANError) {
                        dataExplore.clear()
                        srlMediaGeneral.setRefreshing(false)
                        Log.d("asuu nya media explore : ", anError.toString())
                        // Next go to error page (Popup error)
                    }
                })
    }

    private fun getExploreText() {
        dataExplore.clear()
        AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore?username=testaccount1&index=0&type=text")
            .addHeaders("Authorization", "Bearer $token")
            .setTag(this)
            .setPriority(Priority.LOW)
            .build()
            .getAsObject(
                Respons::class.java,
                object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        dataExplore.clear()
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            dataExplore.clear()
                            for (res in respon.data) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Data::class.java)
                                dataExplore.add(dataJson)
                            }
                            loadExploreText(dataExplore, findViewById(R.id.rvExploreText))

                        } else {
                            dataExplore.clear()
                        }
                    }

                    override fun onError(anError: ANError) {
                        dataExplore.clear()
                        Log.d("asuu nya text explore :", anError.toString())
                        // Next go to error page (Popup error)
                    }
                })
    }

    fun loadExploreMedia(data: ArrayList<Data>, explore: RecyclerView) {
        explore.setHasFixedSize(true)
        explore.layoutManager = LinearLayoutManager(this)
        val adapter =
            GeneralMediaAdapter(data, this)
        adapter.notifyDataSetChanged()

        explore.adapter = adapter
    }

    fun loadExploreText(data: ArrayList<Data>, explore: RecyclerView) {
        explore.setHasFixedSize(true)
        explore.layoutManager = LinearLayoutManager(this)
        val adapter =
            GeneralTextAdapter(
                data,
                this
            )
        adapter.notifyDataSetChanged()

        explore.adapter = adapter
    }

    fun backToHome(view: View) {
        appBarLayout?.visibility = View.VISIBLE
        tabs.visibility = View.VISIBLE
        edSearchbar.visibility = View.VISIBLE
        manager?.beginTransaction()
            ?.replace(R.id.fragmentExplore, ListMediaGeneral())?.commit()
    }

    //move to hashtagActivity
//    fun more(data: Data) {
//        appBarLayout?.visibility = View.GONE
//        tabs.visibility = View.GONE
//        edSearchbar.visibility = View.GONE
//        val bundle = Bundle()
//        bundle.putParcelable("data", data)
//        val dataPost = DetailPostHashtag()
//        dataPost.arguments = bundle
//        manager?.beginTransaction()
//            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            ?.replace(R.id.fragmentExplore, dataPost)
//            ?.commit()
//    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {
        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>

        init {
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)

        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]

        }
    }

}