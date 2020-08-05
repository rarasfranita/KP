package com.example.lotus.ui.explore.general

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
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
import com.example.lotus.models.MediaData
import com.example.lotus.models.Respons
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.explore.SearchActivity
import com.example.lotus.ui.explore.general.adapter.GeneralMediaAdapter
import com.example.lotus.ui.explore.general.adapter.GeneralTextAdapter
import com.example.lotus.ui.explore.general.fragment.ListMediaGeneral
import com.example.lotus.ui.explore.general.fragment.ListTextGeneral
import com.example.lotus.ui.explore.general.model.Data
import com.example.lotus.utils.downloadMedia
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_explore_general.*
import kotlinx.android.synthetic.main.fragment_list_media_general.*
import kotlinx.android.synthetic.main.fragment_list_text_general.*


class GeneralActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null
    var dataExploreM = ArrayList<Data>()
    var dataExploreT = ArrayList<Data>()
    var username = SharedPrefManager.getInstance(this).user.username
    var token = SharedPrefManager.getInstance(this).token.token


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

        getExploreMedia(null)
        getExploreText(null)
        srlMediaGeneral.setOnRefreshListener {
            getExploreText(srlMediaGeneral)
            getExploreMedia(srlMediaGeneral)

        }
        val tabLayout: TabLayout = findViewById(R.id.tabs)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        val search = findViewById<EditText>(R.id.edSearchbar)
        viewPagerAdapter.addFragment(ListMediaGeneral(), "Media")
        viewPagerAdapter.addFragment(ListTextGeneral(), "Text")

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        AndroidNetworking.initialize(applicationContext)
    }

    private fun listenAppToolbar() {
        if (!SharedPrefManager.getInstance(this).isLoggedIn) {
            LinLayou1.visibility = View.GONE
        }
    }

    fun search (view : View){
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

//    fun refresh (view : View){
//        setContentView(R.layout.activity_explore_general)
//        val srlMediaGeneral: PullRefreshLayout = findViewById(R.id.srlMediaGeneral)
//        getExploreMedia(null)
//        getExploreText(null)
//        srlMediaGeneral.setOnRefreshListener {
//            getExploreText(srlMediaGeneral)
//            getExploreMedia(srlMediaGeneral)
//
//        }
//        Toast.makeText(this@GeneralActivity, "Terpencet", Toast.LENGTH_SHORT).show()
//
//    }

    private fun getExploreText(v: PullRefreshLayout?) {
        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            v?.setRefreshing(true)
            srlMediaGeneral.setRefreshing(true)
            AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore?{username}&type=text")
                .addQueryParameter("username", username)
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(
                    Respons::class.java,
                    object : ParsedRequestListener<Respons> {
                        override fun onResponse(respon: Respons) {
                            srlMediaGeneral.setRefreshing(false)
                            val gson = Gson()
                            val temp = ArrayList<Data>()
                            if (respon.code.toString() == "200") {
                                for (res in respon.data) {
                                    val strRes = gson.toJson(res)
                                    val dataJson = gson.fromJson(strRes, Data::class.java)
                                    temp.add(dataJson)
                                }
                                dataExploreT = temp
                                loadExploreText(dataExploreT, findViewById(R.id.rvExploreText))

                            } else {
                                srlMediaGeneral.setRefreshing(false)
                            }
                        }

                        override fun onError(anError: ANError) {
                            if (anError.errorDetail == "connectionError"){
                                setContentView(R.layout.internet_error)
                                Toast.makeText(this@GeneralActivity, "${anError.errorDetail}", Toast.LENGTH_SHORT).show()
                            } else {
                                setContentView(R.layout.internal_server_error)
                                Toast.makeText(this@GeneralActivity, "${anError.errorDetail}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
        } else {
            AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore?type=text")
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(
                    Respons::class.java,
                    object : ParsedRequestListener<Respons> {
                        override fun onResponse(respon: Respons) {
                            val gson = Gson()
                            val temp = ArrayList<Data>()
                            if (respon.code.toString() == "200") {
                                for (res in respon.data) {
                                    val strRes = gson.toJson(res)
                                    val dataJson = gson.fromJson(strRes, Data::class.java)
                                    temp.add(dataJson)
                                }
                                dataExploreT = temp

                                loadExploreText(dataExploreT, findViewById(R.id.rvExploreText))

                            } else {
                                srlMediaGeneral.setRefreshing(false)
                            }
                        }

                        override fun onError(anError: ANError) {
                            // Next go to error page (Popup error)
                        }
                    })
        }
    }

    private fun getExploreMedia(v: PullRefreshLayout?) {
        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            v?.setRefreshing(true)
            AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore?{username}&type=media")
                .addQueryParameter("username", username)
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(Respons::class.java, object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        srlMediaGeneral.setRefreshing(false)
                        val gson = Gson()
                        val temp = ArrayList<Data>()
                        if (respon.code.toString() == "200") {
                            for (res in respon.data) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Data::class.java)
                                temp.add(dataJson)
                            }
                            dataExploreM = temp

                            loadExploreMedia(dataExploreM, findViewById(R.id.rvExploreMedia))

                        } else {
                        }
                    }

                    override fun onError(anError: ANError) {
                        srlMediaGeneral.setRefreshing(false)
                    }
                })
        } else {
            srlMediaGeneral.setRefreshing(true)
            AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore?type=media")
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(Respons::class.java, object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        srlMediaGeneral.setRefreshing(false)
                        val gson = Gson()
                        val temp = ArrayList<Data>()

                        if (respon.code.toString() == "200") {
                            for (res in respon.data) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Data::class.java)
                                temp.add(dataJson)
                            }
                            dataExploreM = temp
                            loadExploreMedia(dataExploreM, findViewById(R.id.rvExploreMedia))

                        } else {
                        }
                    }

                    override fun onError(anError: ANError) {
                        srlMediaGeneral.setRefreshing(false)
                    }
                })
        }
    }


    fun loadExploreMedia(data: ArrayList<Data>, explore: RecyclerView) {
        if (data.size < 1){
            explore.visibility = View.GONE
        } else {
            dataEmptyMediaGeneral.visibility = View.GONE
            explore.setHasFixedSize(true)
            explore.layoutManager = LinearLayoutManager(this)
            val adapter =
                GeneralMediaAdapter(data, this)
            adapter.notifyDataSetChanged()
            explore.adapter = adapter
        }
    }

    fun loadExploreText(data: ArrayList<Data>, explore: RecyclerView) {
        if (data.size < 1){
            explore.visibility = View.GONE
        } else {
            dataEmptyTextGeneral.visibility = View.GONE
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
    }

    fun backToHome(view: View) {
        tabs.visibility = View.VISIBLE
        manager?.beginTransaction()
            ?.replace(R.id.fragmentExplore, ListMediaGeneral())?.commit()
    }


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

    fun detailPost(postId: String) {
        val bundle = Bundle().apply {
            putString("postId", postId)
        }
//        appBarLayout.visibility = View.INVISIBLE
        val dataPost = DetailPost()
        dataPost.arguments = bundle
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.fragmentExplore, dataPost)
            ?.addToBackStack("Explore")
            ?.commit()
    }

    fun setAppBarVisible() {
//        appBarLayout.visibility = View.VISIBLE
    }

    fun showDialog(medias: ArrayList<MediaData>) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_menu_post)
        val download = dialog.findViewById<LinearLayout>(R.id.downloadMedia)
        val share = dialog.findViewById<LinearLayout>(R.id.sharePost)
        download.setOnClickListener {
            downloadMedia(medias, this)
            dialog.dismiss()
        }

        share.setOnClickListener {
            if (medias.size < 1) {
                Toast.makeText(
                    this@GeneralActivity,
                    "No media to be downloaded",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                shareMediaToOtherApp(medias)
                dialog.dismiss()
            }
        }

        dialog.show()

    }

    fun backtoExplore(view :View){
        this.onBackPressed()
    }
    fun shareMediaToOtherApp(medias: ArrayList<MediaData>) {
        for (media in medias) {
            val uri: Uri = Uri.parse(media.link)
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, media.link)
                type = "*"
            }
            startActivity(Intent.createChooser(shareIntent, "Share To"))
        }
    }
}