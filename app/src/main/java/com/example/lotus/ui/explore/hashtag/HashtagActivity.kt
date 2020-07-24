package com.example.lotus.ui.explore.hashtag

//import com.example.lotus.ui.explore.detailpost.DetailPostHashtag
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
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
import com.example.lotus.models.Post
import com.example.lotus.models.Respons
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.explore.hashtag.adapter.HashtagMediaAdapter
import com.example.lotus.ui.explore.hashtag.adapter.HashtagTextAdapter
import com.example.lotus.ui.explore.hashtag.fragment.ListMediaHashtag
import com.example.lotus.ui.explore.hashtag.fragment.ListTextHashtag
import com.example.lotus.utils.downloadMedia
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_explore_general.*
import kotlinx.android.synthetic.main.activity_hashtag.*


class HashtagActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null
    var dataHashtagM = ArrayList<Post>()
    var dataHashtagT = ArrayList<Post>()

    var username = SharedPrefManager.getInstance(this).user.username



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_hashtag)

        manager = supportFragmentManager
        val srlHashtag: PullRefreshLayout = findViewById(R.id.srlHashtag)

        getHashtagMedia(null)
        getHashtagText(null)
        srlHashtag.setOnRefreshListener {
            getHashtagText(srlHashtag)
            getHashtagMedia(srlHashtag)

        }
        val tabLayout: TabLayout = findViewById(R.id.tabsHashtag)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val viewPagerAdapterHashtag = ViewPagerAdapterHashtag(supportFragmentManager)

        viewPagerAdapterHashtag.addFragmentHashtag(ListMediaHashtag(), "Media")
        viewPagerAdapterHashtag.addFragmentHashtag(ListTextHashtag(), "Text")

        viewPager.adapter = viewPagerAdapterHashtag
        tabLayout.setupWithViewPager(viewPager)

        AndroidNetworking.initialize(applicationContext)

    }

    private fun getHashtagMedia(v: PullRefreshLayout?) {
        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            v?.setRefreshing(true)
            val bundle = getIntent().getExtras()
            var hashtag = bundle?.getString("hashtag")
            hashtagDiHashtag.text = "#$hashtag"
            AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore/{hashtag}?{username}&index=0&type=media")
                .addPathParameter("hashtag", hashtag)
                .addQueryParameter("username", username)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(
                    Respons::class.java,
                    object : ParsedRequestListener<Respons> {
                        override fun onResponse(respon: Respons) {
                            srlHashtag.setRefreshing(false)
                            val gson = Gson()
                            val temp = ArrayList<Post>()
                            if (respon.code.toString() == "200") {
                                Log.e("RESPON!!", respon.data.toString())
                                srlHashtag.setRefreshing(false)
                                for ((i, res) in respon.data.withIndex()) {
                                    val strRes = gson.toJson(res)
                                    val dataJson = gson.fromJson(strRes, Post::class.java)
                                    temp.add(dataJson)
                                }
                                dataHashtagM = temp
                                loadExploreHashtagMedia(dataHashtagM, findViewById(R.id.rvHashtagMedia))

                            }
                            else {
                                srlHashtag.setRefreshing(false)
                            }
                        }

                        override fun onError(anError: ANError) {
                            srlHashtag.setRefreshing(false)
                            // Next go to error page (Popup error)
                        }
                    })
        }
        else {
            v?.setRefreshing(true)
            val bundle = getIntent().getExtras()
            var hashtag = bundle?.getString("hashtag")
            hashtagDiHashtag.text = "#$hashtag"
            AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore/{hashtag}?type=media")
                .addPathParameter("hashtag", hashtag)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(
                    Respons::class.java,
                    object : ParsedRequestListener<Respons> {
                        override fun onResponse(respon: Respons) {
                            srlHashtag.setRefreshing(false)
                            val gson = Gson()
                            val temp = ArrayList<Post>()
                            if (respon.code.toString() == "200") {
                                srlHashtag.setRefreshing(false)
                                for ((i, res) in respon.data.withIndex()) {
                                    val strRes = gson.toJson(res)
                                    val dataJson = gson.fromJson(strRes, Post::class.java)
                                    temp.add(dataJson)
                                }
                                dataHashtagM = temp
                                loadExploreHashtagMedia(dataHashtagM, findViewById(R.id.rvHashtagMedia))

                            } else {
                                srlHashtag.setRefreshing(false)
                            }
                        }

                        override fun onError(anError: ANError) {
                            srlHashtag.setRefreshing(false)
                            // Next go to error page (Popup error)
                        }
                    })
        }

    }

    private fun getHashtagText(v: PullRefreshLayout?) {
        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            v?.setRefreshing(true)
            val bundle = getIntent().getExtras()
            var hashtag = bundle?.getString("hashtag")
            hashtagDiHashtag.text = "#$hashtag"
            AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore/{hashtag}?{username}&type=text")
                .addPathParameter("hashtag", hashtag)
                .addQueryParameter("username", username)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(
                    Respons::class.java,
                    object : ParsedRequestListener<Respons> {
                        override fun onResponse(respon: Respons) {
                            srlHashtag.setRefreshing(false)
                            val gson = Gson()
                            val temp = ArrayList<Post>()
                            if (respon.code.toString() == "200") {
                                srlHashtag.setRefreshing(false)
                                for ((i, res) in respon.data.withIndex()) {
                                    val strRes = gson.toJson(res)
                                    val dataJson = gson.fromJson(strRes, Post::class.java)
                                    temp.add(dataJson)
                                }
                                dataHashtagT = temp
                                loadExploreHashtagText(dataHashtagT, findViewById(R.id.rvHashtagText))

                            } else {
                                srlHashtag.setRefreshing(false)
                            }
                        }

                        override fun onError(anError: ANError) {
                            srlHashtag.setRefreshing(false)
                            Log.d("asuu nya media hashtag:", anError.toString())
                            // Next go to error page (Popup error)
                        }
                    })
        }
        else {
            v?.setRefreshing(true)
            val bundle = getIntent().getExtras()
            var hashtag = bundle?.getString("hashtag")
            hashtagDiHashtag.text = "#$hashtag"
            AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore/{hashtag}?type=text")
                .addPathParameter("hashtag", hashtag)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(
                    Respons::class.java,
                    object : ParsedRequestListener<Respons> {
                        override fun onResponse(respon: Respons) {
                            srlHashtag.setRefreshing(false)
                            val gson = Gson()
                            val temp = ArrayList<Post>()
                            if (respon.code.toString() == "200") {
                                srlHashtag.setRefreshing(false)
                                for ((i, res) in respon.data.withIndex()) {
                                    val strRes = gson.toJson(res)
                                    val dataJson = gson.fromJson(strRes, Post::class.java)
                                    temp.add(dataJson)
                                }
                                dataHashtagT = temp
                                loadExploreHashtagText(dataHashtagT, findViewById(R.id.rvHashtagText))

                            } else {
                                srlHashtag.setRefreshing(false)
                            }
                        }

                        override fun onError(anError: ANError) {
                            srlHashtag.setRefreshing(false)
                            Log.d("asuu nya media hashtag:", anError.toString())
                            // Next go to error page (Popup error)
                        }
                    })
        }

    }

    fun loadExploreHashtagMedia(data: ArrayList<Post>, hashtag: RecyclerView) {
        hashtag.setHasFixedSize(true)
        hashtag.layoutManager = LinearLayoutManager(this)
        val adapter =
            HashtagMediaAdapter(data, this)
        adapter.notifyDataSetChanged()

        hashtag.adapter = adapter
    }

    fun loadExploreHashtagText(data: ArrayList<Post>, hashtag: RecyclerView) {
        hashtag.setHasFixedSize(true)
        hashtag.layoutManager = LinearLayoutManager(this)
        val adapter =
            HashtagTextAdapter(
                data,
                this
            )
        adapter.notifyDataSetChanged()

        hashtag.adapter = adapter
    }

    fun backToHome(view: View) {
        LinLayout1?.visibility = View.VISIBLE
        tabsHashtag.visibility = View.VISIBLE
        manager?.beginTransaction()
            ?.replace(R.id.fragment_list_media_hashtag, ListMediaHashtag())?.commit()
    }

    fun backToExplore(view: View) {
        this.onBackPressed()
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

    fun detailPost(postId: String) {
        val bundle = Bundle().apply {
            putString("postId", postId)
        }
        if (appBarLayout != null){
            appBarLayout.visibility = View.INVISIBLE
        }
        val dataPost = DetailPost()
        dataPost.arguments = bundle
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.fragmentHashtag, dataPost)
            ?.addToBackStack("Explore")
            ?.commit()
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
            if (medias.size < 1){
                Toast.makeText(this@HashtagActivity, "No media to be downloaded", Toast.LENGTH_SHORT).show()
            }else {
                shareMediaToOtherApp(medias)
                dialog.dismiss()
            }
        }

        dialog.show()

    }
    fun shareMediaToOtherApp(medias: ArrayList<MediaData>){
        for (media in medias){
            val uri: Uri = Uri.parse(media.link)
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, media.link)
                type = "*"
            }
            startActivity(Intent.createChooser(shareIntent, "Share To"))
        }
    }

    fun gotoDetailPost(item: Post) {
        val bundle = Bundle()
        bundle.putParcelable("data", item)
        val dataPost = DetailPost()
        dataPost.arguments = bundle
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.fragmentHashtag, dataPost)
            ?.addToBackStack("Hashtag")
            ?.commit()
    }
}
