package com.example.lotus.ui.explore.hashtag

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.example.lotus.service.EnvService
//import com.example.lotus.ui.explore.detailpost.DetailPostHashtag
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.explore.hashtag.adapter.HashtagMediaAdapter
import com.example.lotus.ui.explore.hashtag.adapter.HashtagTextAdapter
import com.example.lotus.ui.explore.hashtag.fragment.ListMediaHashtag
import com.example.lotus.ui.explore.hashtag.fragment.ListTextHashtag
import com.example.lotus.ui.explore.hashtag.model.Data
import com.example.lotus.ui.explore.hashtag.model.Hashtag
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_hashtag.*


class HashtagActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null
    private val token = "5f09a143ff07b60aaafd008d"
    var dataHashtag = ArrayList<Data>()

    var anu = dataHashtag

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_hashtag)

        manager = supportFragmentManager
        val srlHashtag: PullRefreshLayout = findViewById(R.id.srlHashtag)

        srlHashtag.setOnRefreshListener {
            dataHashtag.clear()
            getHashtagMedia()
            getHashtagText()

        }
        val tabLayout: TabLayout = findViewById(R.id.tabsHashtag)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val viewPagerAdapterHashtag = ViewPagerAdapterHashtag(supportFragmentManager)

        viewPagerAdapterHashtag.addFragmentHashtag(ListMediaHashtag(), "Media")
        viewPagerAdapterHashtag.addFragmentHashtag(ListTextHashtag(), "Text")

        viewPager.adapter = viewPagerAdapterHashtag
        tabLayout.setupWithViewPager(viewPager)

        AndroidNetworking.initialize(applicationContext)
        dataHashtag.clear()
        getHashtagMedia()
        getHashtagText()

    }

    private fun getHashtagMedia() {
        val bundle = getIntent().getExtras()
        var hashtag = bundle?.getString("hashtag")
        hashtagDiHashtag.text = "#$hashtag"
        dataHashtag.clear()
        srlHashtag.setRefreshing(true)
        AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore/$hashtag?username=testaccount&index=0&type=media")
            .addHeaders("Authorization", "Bearer $token")
            .setTag(this)
            .setPriority(Priority.LOW)
            .build()
            .getAsObject(
                Hashtag::class.java,
                object : ParsedRequestListener<Hashtag> {
                    override fun onResponse(respon: Hashtag) {
                        dataHashtag.clear()
                        srlHashtag.setRefreshing(false)
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            dataHashtag.clear()
                            srlHashtag.setRefreshing(false)
                            for (res in respon.data) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Data::class.java)
                                dataHashtag.add(dataJson)
                            }
                            loadExploreHashtagMedia(dataHashtag, findViewById(R.id.rvHashtagMedia))

                        } else {
                            dataHashtag.clear()
                            srlHashtag.setRefreshing(false)
                        }
                    }

                    override fun onError(anError: ANError) {
                        dataHashtag.clear()
                        srlHashtag.setRefreshing(false)
                        // Next go to error page (Popup error)
                    }
                })
    }

    private fun getHashtagText() {
        val bundle = getIntent().getExtras()
        var hashtag = bundle?.getString("hashtag")
        hashtagDiHashtag.text = "#$hashtag"
        dataHashtag.clear()
        srlHashtag.setRefreshing(true)
        AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore/$hashtag?username=testaccount&index=0&type=text")
            .addHeaders("Authorization", "Bearer $token")
            .setTag(this)
            .setPriority(Priority.LOW)
            .build()
            .getAsObject(
                Hashtag::class.java,
                object : ParsedRequestListener<Hashtag> {
                    override fun onResponse(respon: Hashtag) {
                        dataHashtag.clear()
                        srlHashtag.setRefreshing(false)
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            dataHashtag.clear()
                            srlHashtag.setRefreshing(false)
                            for (res in respon.data) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Data::class.java)
                                dataHashtag.add(dataJson)
                            }
                            loadExploreHashtagText(dataHashtag, findViewById(R.id.rvHashtagText))

                        } else {
                            dataHashtag.clear()
                            srlHashtag.setRefreshing(false)
                        }
                    }

                    override fun onError(anError: ANError) {
                        dataHashtag.clear()
                        srlHashtag.setRefreshing(false)
                        Log.d("asuu nya media hashtag:", anError.toString())
                        // Next go to error page (Popup error)
                    }
                })
    }

    fun loadExploreHashtagMedia(data: ArrayList<Data>, hashtag: RecyclerView) {
        val adapter =
            HashtagMediaAdapter(
                data,
                this
            )
        adapter.notifyDataSetChanged()
        hashtag.adapter = adapter
        hashtag.setHasFixedSize(true)
        hashtag.layoutManager = LinearLayoutManager(this)
    }

    fun loadExploreHashtagText(data: ArrayList<Data>, hashtag: RecyclerView) {
        val adapter =
            HashtagTextAdapter(
                data,
                this
            )
        adapter.notifyDataSetChanged()
        hashtag.adapter = adapter
        hashtag.setHasFixedSize(true)
        hashtag.layoutManager = LinearLayoutManager(this)
    }

//    fun detailPost(data: Data) {
//        LinLayout1.visibility = View.GONE
//        tabsHashtag.visibility = View.GONE
//        val bundle = Bundle()
//        bundle.putParcelable("data", data)
//        val dataPost = DetailPostHashtag()
//        dataPost.arguments = bundle
//        manager?.beginTransaction()
//            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            ?.replace(R.id.fragment_list_media_hashtag, dataPost)
//            ?.commit()
//
//    }

    fun backToHome(view: View) {
        LinLayout1?.visibility = View.VISIBLE
        tabsHashtag.visibility = View.VISIBLE
//        appBarLayout?.setVisibility(View.VISIBLE)
        manager?.beginTransaction()
            ?.replace(R.id.fragment_list_media_hashtag, ListMediaHashtag())?.commit()
    }

    fun backToExplore(view: View) {
        val intent = Intent(this, GeneralActivity::class.java)
        startActivity(intent)
    }

    internal class ViewPagerAdapterHashtag(fragmentManager: FragmentManager) :
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

        fun addFragmentHashtag(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)

        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]

        }
    }

}