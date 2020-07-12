package com.example.lotus.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.Post
import com.example.lotus.models.Respons
import com.example.lotus.service.EnvService
import com.example.lotus.ui.CreatePost
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.explore.general.model.Data
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeActivity : AppCompatActivity() {
    private val TAG = "HomeActivity"
    private var manager: FragmentManager? = null
    private val token = "5f0697c5f165da1a2cc6d3f0"
    var dataFeed = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_main)

        val fabPost = findViewById<View>(R.id.fab_post)

        fabPost.setOnClickListener(View.OnClickListener { fabPostOnClick() })
        navigationMenuLogic()

        manager = getSupportFragmentManager()

        AndroidNetworking.initialize(getApplicationContext());
        getFeedsData()
    }

    fun getFeedsData(){
        AndroidNetworking.get(EnvService.ENV_API + "/feeds/testaccount1/-1")
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
                                dataFeed.add(dataJson)
                            }

                            loadFeed(dataFeed, findViewById(R.id.rcHomeFeed))

                        }else {
//                            TODO: Create error page and show what the error
//                            Toast.makeText(coroutineContext(), "Error ${respon.code}", Toast.LENGTH_SHORT)
                        }
                    }

                    override fun onError(anError: ANError) {
                        Log.d("Errornya disini kah?", anError.toString())
                        // Next go to error page (Popup error)
                    }
                })
    }

    fun loadFeed(data: ArrayList<Post>, homeFeed: RecyclerView){
        homeFeed.setHasFixedSize(true)
        homeFeed.layoutManager = LinearLayoutManager(this)
        val adapter = PostFeedAdapter(data, this)
        adapter.notifyDataSetChanged()

        homeFeed.adapter = adapter
    }

    private fun fabPostOnClick() {
        val intent = Intent(this, CreatePost::class.java)
        startActivity(intent)
    }

    private fun navigationMenuLogic(){
        val llBottomSheet =
            findViewById<View>(R.id.bottom_sheet) as LinearLayout

        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(llBottomSheet)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (BottomSheetBehavior.STATE_DRAGGING == newState) {
                    fab_post.hide()
                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    fab_post.show()
                }
            }

            override fun onSlide(
                bottomSheet: View,
                slideOffset: Float
            ) {
            }
        })

        val btmNav =
            findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val btmNav2  =
            findViewById<BottomNavigationView>(R.id.bottom_navigation_2)

        btmNav2.menu.findItem(R.id.navigation_calendar).setCheckable(false)
        val navController = findNavController(R.id.nav_host_fragment)

        btmNav.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            btmNav2.menu.findItem(R.id.navigation_calendar).setCheckable(false)
            setButtonNavChekable(btmNav, true)

            when (item.itemId) {
                R.id.navigation_meditation -> {
                    btmNav.menu.findItem(item.itemId).setChecked(true)
                    navController.navigate(item.itemId)
                }
                R.id.navigation_home -> {
                    btmNav.menu.findItem(item.itemId).setChecked(true)
                    navController.navigate(item.itemId)
                }
                R.id.navigation_reflection -> {
                    btmNav.menu.findItem(item.itemId).setChecked(true)
                    navController.navigate(item.itemId)
                }
                R.id.navigation_tipitaka -> {
                    btmNav.menu.findItem(item.itemId).setChecked(true)
                    navController.navigate(item.itemId)
                }
            }
            false
        })

        btmNav2.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_calendar -> {
                    btmNav2.menu.findItem(R.id.navigation_calendar).setCheckable(true)
                    setButtonNavChekable(btmNav, false)
                    navController.navigate(item.itemId)
                }
            }
            false
        })
    }

    private fun setButtonNavChekable(btmNav: BottomNavigationView, active: Boolean) {
        btmNav.getMenu().findItem(R.id.navigation_reflection).setCheckable(active);
        btmNav.getMenu().findItem(R.id.navigation_home).setCheckable(active);
        btmNav.getMenu().findItem(R.id.navigation_tipitaka).setCheckable(active);
        btmNav.getMenu().findItem(R.id.navigation_meditation).setCheckable(active);
      }

    fun detailPost(item: Post) {
        val bundle = Bundle()
        bundle.putParcelable("data", item)
        val dataPost = DetailPost()
        dataPost.arguments = bundle
        appBarLayout?.setVisibility(View.INVISIBLE)
        bottom_sheet?.setVisibility(View.INVISIBLE)
        fab_post?.setVisibility(View.INVISIBLE)
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.fragmentHome, dataPost)
            ?.commit()
    }

    fun detailPostFromExplore(item: Data) {
        val bundle = Bundle()
        bundle.putParcelable("data", item)
        val dataPost = DetailPost()
        dataPost.arguments = bundle
        appBarLayout?.setVisibility(View.INVISIBLE)
        bottom_sheet?.setVisibility(View.INVISIBLE)
        fab_post?.setVisibility(View.INVISIBLE)
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.fragmentHome, dataPost)
            ?.commit()
    }

    fun backToHome(view: View) {
        appBarLayout?.setVisibility(View.VISIBLE)
        bottom_sheet?.setVisibility(View.VISIBLE)
        fab_post?.setVisibility(View.VISIBLE)
        manager?.beginTransaction()
            ?.replace(R.id.fragmentHome, HomeFragment())?.commit()
    }

}