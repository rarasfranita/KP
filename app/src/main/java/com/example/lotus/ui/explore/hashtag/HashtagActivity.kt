package com.example.lotus.ui.explore.hashtag

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
import com.example.lotus.ui.explore.detailpost.DetailPostHashtag
import com.example.lotus.ui.explore.hashtag.fragment.ListMediaHashtag
import com.example.lotus.ui.explore.hashtag.fragment.ListTextHashtag
import com.example.lotus.ui.explore.hashtag.model.Data
import com.example.lotus.ui.explore.hashtag.model.Hashtag
import com.example.lotus.ui.home.HomeFragment
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_hashtag.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_home.*

class HashtagActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null
    private val TAG = "HashtagActivity"
    private val token = "5f09a143ff07b60aaafd008d"
    var dataHashtag = ArrayList<Data>()

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
//            gethashtagText()

        }
        val tabLayout: TabLayout = findViewById(R.id.tabsHashtag)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val viewPagerAdapterHashtag = ViewPagerAdapterHashtag(supportFragmentManager)

        viewPagerAdapterHashtag.addFragmentHashtag(ListMediaHashtag(), "Media")
        viewPagerAdapterHashtag.addFragmentHashtag(ListTextHashtag(), "Text")

        viewPager.adapter = viewPagerAdapterHashtag
        tabLayout.setupWithViewPager(viewPager)

        AndroidNetworking.initialize(getApplicationContext());
        getHashtagMedia()
    }

//    private fun gethashtagText() {
//        TODO("Not yet implemented")
//    }

    private fun getHashtagMedia() {
        dataHashtag.clear()
        srlHashtag.setRefreshing(true)
        AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore/testing?username=testaccount&index=0&type=media")
            .addHeaders("Authorization", "Bearer " + token)
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
                            loadExploreHashtag(dataHashtag, findViewById(R.id.rvHashtagMedia))

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

    fun loadExploreHashtag(data: ArrayList<Data>, hashtag: RecyclerView) {
        val adapter = HashtagMediaAdapter(data, this)
        adapter.notifyDataSetChanged()
        hashtag.adapter = adapter
        hashtag.setHasFixedSize(true)
        hashtag.layoutManager = LinearLayoutManager(this)
    }

    fun detailPost(data: Data) {
        LinLayout1.visibility = View.GONE
        tabsHashtag.visibility = View.GONE
        val bundle = Bundle()
        bundle.putParcelable("data", data)
        val dataPost = DetailPostHashtag()
        dataPost.arguments = bundle
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.fragment_list_media_hashtag, dataPost)
            ?.commit()

    }

    fun backToHome(view: View) {
        LinLayout1?.setVisibility(View.VISIBLE)
        tabsHashtag.setVisibility(View.VISIBLE)
//        appBarLayout?.setVisibility(View.VISIBLE)
        manager?.beginTransaction()
            ?.replace(R.id.fragment_list_media_hashtag, ListMediaHashtag())?.commit()
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