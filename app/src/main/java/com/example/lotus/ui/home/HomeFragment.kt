package com.example.lotus.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.baoyz.widget.PullRefreshLayout
import com.example.lotus.R
import com.example.lotus.ui.profile.ProfileActivity
import com.example.lotus.models.Post
import com.example.lotus.models.Respons
import com.example.lotus.service.EnvService
import com.example.lotus.ui.dm.MainActivityDM
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.notification.NotificationActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val TAG = "HomeFragment"
    private val token = "5f02b3361718f5360aeff6d2"
    var dataFeed = ArrayList<Post>()
    val username = "testaccount4"
    var idBucket = -1

    lateinit var adapter: PostFeedAdapter
    lateinit var scrollListener: RecyclerViewLoadMoreScroll
    lateinit var mLayoutManager:RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        val reloadFeed: PullRefreshLayout = v.findViewById(R.id.reloadFeed)

        listenAppToolbar(v)
        v!!.setOnTouchListener { v, event ->
            Log.d(TAG, event.toString())
            if (event.action == MotionEvent.ACTION_MOVE) {
                Log.d(TAG, event.toString())
            }
            true
        }
        getFeedsData(null)
//        setRVScrollListener(v) TODO

        reloadFeed.setOnRefreshListener {
            getFeedsData(reloadFeed)
        }

        return v
    }

    fun getFeedsData(v: PullRefreshLayout?){
        if (v !=  null){
            v.setRefreshing(true)
        }

        AndroidNetworking.get(EnvService.ENV_API + "/feeds/{username}/{id}")
            .addPathParameter("username", username)
            .addPathParameter("id", idBucket.toString())
            .addHeaders("Authorization", "Bearer " + token)
            .setTag(this)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respons::class.java,
                object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        reloadFeed.setRefreshing(false)
                        val gson = Gson()
                        var tempDataFeed = ArrayList<Post>()
                        if (respon.code.toString() == "200") {
                            for ((i, res) in respon.data.withIndex()) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Post::class.java)
                                tempDataFeed.add(dataJson)

                                /* TODO: For load data scrolling
                                if (i.equals(respon.data.size-1)){
                                    idBucket = dataJson.id!!
                                }
                                 */
                            }
                            dataFeed = tempDataFeed
                            loadFeed(dataFeed, rvHomeFeed)

                        }else {
                            Toast.makeText(context, "Error ${respon.code}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        reloadFeed.setRefreshing(false)
                        Toast.makeText(context, "Error ${anError.errorCode}", Toast.LENGTH_SHORT).show()
                        Log.d("Errornya disini kah?", anError.toString())
                    }
                })
    }

    fun loadFeed(data: ArrayList<Post>, homeFeed: RecyclerView){
        adapter = PostFeedAdapter(data, requireContext())
        adapter.notifyDataSetChanged()

        homeFeed.adapter = adapter
        homeFeed.setHasFixedSize(true)
        homeFeed.layoutManager = LinearLayoutManager(context)
    }

    private fun listenAppToolbar(v: View){
        val toolbar: Toolbar = v.findViewById(R.id.appToolbar) as Toolbar

        toolbar.setLogo(R.drawable.ic_notification)

        toolbar.setNavigationOnClickListener {
            rvHomeFeed.smoothScrollToPosition(0);
        }

        val logoView: View? = getToolbarLogoIcon(toolbar)
        logoView?.setOnClickListener{
            startActivity(Intent(context, NotificationActivity::class.java))
        }


        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.explore -> {
                    toolbar.context.startActivity(Intent(context, GeneralActivity::class.java))
                }
                R.id.direct_message -> {
                    toolbar.context.startActivity(Intent(context, MainActivityDM::class.java))
                }
                R.id.profile -> {
                    toolbar.context.startActivity(Intent(context,ProfileActivity::class.java))
                }
                else -> false
            }
            true
        }

    }

    private fun setRVScrollListener(v: View) {
        // TODO
        mLayoutManager = LinearLayoutManager(context)
        scrollListener = RecyclerViewLoadMoreScroll(mLayoutManager as LinearLayoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore() {
                LoadMoreData()
            }
        })

        val rvfeed =  v.findViewById<RecyclerView>(R.id.rvHomeFeed)
        rvfeed.addOnScrollListener(scrollListener)
    }

    private fun LoadMoreData() {
        // TODO
        adapter.addLoadingView()
        val moreData: ArrayList<Post> = ArrayList()
        Handler().postDelayed({
            AndroidNetworking.get(EnvService.ENV_API + "/feeds/{username}/0")
                .addPathParameter("username", username)
                .addHeaders("Authorization", "Bearer " + token)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(
                    Respons::class.java,
                    object : ParsedRequestListener<Respons> {
                        override fun onResponse(respon: Respons) {
                            val gson = Gson()
                            if (respon.code.toString() == "200") {
                                for (res in respon.data) {
                                    val strRes = gson.toJson(res)
                                    val dataJson = gson.fromJson(strRes, Post::class.java)
                                    moreData.add(dataJson)
                                }
                                adapter.removeLoadingView()
                                adapter.addData(moreData)
                                scrollListener.setLoaded()
                                rvHomeFeed.post {
                                    adapter.notifyDataSetChanged()
                                }

                            }else {
                                Toast.makeText(context, "Error ${respon.code}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onError(anError: ANError) {
                            Toast.makeText(context, "Error ${anError.errorCode}", Toast.LENGTH_SHORT).show()
                            Log.d("Errornya disini kah?", anError.toString())
                        }
                    })


        }, 3000)

    }


    fun getToolbarLogoIcon(toolbar: Toolbar): View? {
        //check if contentDescription previously was set
        val hadContentDescription =
            TextUtils.isEmpty(toolbar.logoDescription)
        val contentDescription: String =
            (if (!hadContentDescription) toolbar.logoDescription else "logoContentDescription").toString()
        toolbar.logoDescription = contentDescription
        val potentialViews = ArrayList<View>()
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(
            potentialViews,
            contentDescription,
            View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION
        )
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        var logoIcon: View? = null
        if (potentialViews.size > 0) {
            logoIcon = potentialViews[0]
        }
        //Clear content description if not previously present
        if (hadContentDescription) toolbar.logoDescription = null
        return logoIcon
    }
}
