package com.example.lotus.ui.profile

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import coil.api.load
import coil.transform.CircleCropTransformation
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.MediaData
import com.example.lotus.models.Post
import com.example.lotus.models.Respon
import com.example.lotus.models.UserProfile
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.utils.downloadMedia
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_media_profile.*
import kotlinx.android.synthetic.main.fragment_text_profile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var button: Button
    private var myProfile: Boolean = true
    private val myUserID = SharedPrefManager.getInstance(this).user._id
    private val myUsername = SharedPrefManager.getInstance(this).user.username
    private val token = SharedPrefManager.getInstance(this).token.token
    private var isFollowing: Boolean = false

    private  var mediaData = ArrayList<Post>()
    private  var textData = ArrayList<Post>()
    private var manager: FragmentManager? = null
    private var totalFollower = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        val extra = intent.getStringExtra("userID")
        if (extra != null && extra != myUserID){
            showUserProfile()
            getProfileData(extra)
        }else{
            getProfileData(myUserID.toString())
            showMyProfile()
        }
        //viewpager
        val tableLayout: TabLayout = findViewById(R.id.tab_Layout)
        val viewPager: ViewPager = findViewById(R.id.view_Pager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(MediaProfileFragment(), title = "media")
        viewPagerAdapter.addFragment(TextFragment(), title = "text")

        viewPager.adapter = viewPagerAdapter
        tableLayout.setupWithViewPager(viewPager)
        manager = getSupportFragmentManager()
        listenToolbar()
    }

    fun getProfileData(UID: String){
        AndroidNetworking.get(EnvService.ENV_API + "/users/{userID}/profile?viewer=$myUserID")
            .addHeaders("Authorization", "Bearer " + token)
            .addPathParameter("userID", UID)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsObject(
                Respon::class.java,
                object : ParsedRequestListener<Respon> {
                    override fun onResponse(respon: Respon) {
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            Log.d("Get Profile Data", "Success")
                            val jsonRes = gson.toJson(respon.data)
                            val data = gson.fromJson(jsonRes, UserProfile::class.java)
                            setProfile(data)
                        }else {
                            Toast.makeText(this@ProfileActivity, "Error while getting data profile, code: ${respon.code} \n${respon.data}", Toast.LENGTH_SHORT).show()
                            Log.e("ERROR!!!", "Like Post ${respon.code}, ${respon.data}")
                        }
                    }

                    override fun onError(anError: ANError) {
                        Toast.makeText(this@ProfileActivity, "Error while getting data profile, code: ${anError.errorDetail}", Toast.LENGTH_SHORT).show()
                        Log.e("ERROR!!!", "Like Post ${anError.errorCode}")

                    }
                })
    }

    fun setProfile(data: UserProfile){
        Log.d("PROFILE PICTURE", data.profilePicture.toString())
        if (data.profilePicture != null){
            profilePicture.load(data.profilePicture.toString()){
                transformations(CircleCropTransformation())
            }
        }

        totalFollower = data.follower!!

        separatePostData(data.posts)

        profileTitle.text = "${data.username}'s profile"
        nameprofile.text = data.name
        tvBiografi.text = data.bio
        totalFollowers.text = totalFollower.toString()
        totalFollowing.text = data.following.toString()
        if (data.posts == null){
            totalPost.text = "0"
        }else{
            totalPost.text = data.posts.size.toString()
        }

        if (data.isFollowing!!.equals(1)){
            isFollowing = true
        }

        updateButtonFollow(isFollowing)

        btnFollowProfile.setOnClickListener {
            follow(data.username.toString())
        }

        loadProfileMedia(mediaData, rvprofilemedia)
        loadProfileText(textData, rvTextProfile)
    }

    fun loadProfileMedia(data: ArrayList<Post>, postProfile: RecyclerView) {
        if (data.size < 1){
            postProfile.visibility = View.GONE
        }else {
            dataEmpty.visibility = View.GONE
            postProfile.setHasFixedSize(true)
            postProfile.layoutManager = GridLayoutManager(this, 3)
            val adapter =
                MediaProfileAdapter(data, this)
            adapter.notifyDataSetChanged()

            postProfile.adapter = adapter
        }
    }

    fun loadProfileText(data: ArrayList<Post>, postProfile: RecyclerView) {
        if (data.size < 1){
            postProfile.visibility = View.GONE
        }else {
            textProfileEmpty.visibility = View.GONE
            postProfile.layoutManager = LinearLayoutManager(this)
            postProfile.setHasFixedSize(true)
            val adapter =
                TextProfileAdapter(data, this)
            adapter.notifyDataSetChanged()

            postProfile.adapter = adapter
        }
    }

    fun separatePostData(posts: ArrayList<Post>?){
        if (posts != null){
            for (post in posts){
                if (post.media!!.size < 1){
                    textData.add(post)
                }else{
                    mediaData.add(post)
                }
            }
        }
    }

    fun follow(username: String){
        if (isFollowing){
            AndroidNetworking.get(EnvService.ENV_API + "/users/$myUsername/unfollow/${username}")
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsObject(
                    Respon::class.java,
                    object : ParsedRequestListener<Respon> {
                        override fun onResponse(respon: Respon) {
                            if (respon.code.toString() == "200") {
                                Log.d("Respon Data Follow", respon.data.toString())
                                totalFollower--
                                isFollowing = false
                                updateButtonFollow(isFollowing)
                            }else {
                                Log.e("ERROR!!!", "Following ${respon.code}")
                            }
                        }

                        override fun onError(anError: ANError) {
                            Log.e("ERROR!!!", "While following ${anError.errorCode}")

                        }
                    })
        }else{
            AndroidNetworking.get(EnvService.ENV_API + "/users/$myUsername/follow/${username}")
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsObject(
                    Respon::class.java,
                    object : ParsedRequestListener<Respon> {
                        override fun onResponse(respon: Respon) {
                            if (respon.code.toString() == "200") {
                                Log.d("Respon Data Follow", respon.data.toString())
                                isFollowing = true
                                totalFollower++
                                updateButtonFollow(isFollowing)
                            }else {
                                Log.e("ERROR!!!", "Following ${respon.code}")
                            }
                        }

                        override fun onError(anError: ANError) {
                            Log.e("ERROR!!!", "While following ${anError.errorCode}")

                        }
                    })
        }
    }

    fun updateButtonFollow(following: Boolean){
        if (following){
            totalFollowers.text =  totalFollower.toString()
            btnFollowProfile.setText("Following")
            btnFollowProfile.setTextColor(getColor(R.color.colorPrimary))
            btnFollowProfile.setBackgroundColor(getColor(R.color.white))
        }else{
            totalFollowers.text =  totalFollower.toString()
            btnFollowProfile.setText("Follow")
            btnFollowProfile.setTextColor(getColor(R.color.white))
            btnFollowProfile.setBackgroundColor(getColor(R.color.colorPrimary))
        }
    }

    private fun showMyProfile(){
        btnEditProfile.visibility= View.VISIBLE
        follow.visibility = View.GONE
    }

    private fun showUserProfile(){
        follow.visibility=View.VISIBLE
        btnEditProfile.visibility=View.GONE
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var mnInflater = menuInflater
        mnInflater.inflate(R.menu.popup_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun backOnClick(view: View) {
        this.onBackPressed()
    }

    fun gotoDetailPost(postId: String) {
        val bundle = Bundle().apply {
            putString("postId", postId)
        }

        val dataPost = DetailPost()
        appbarProfile.visibility = View.INVISIBLE
        dataPost.arguments = bundle
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.containerProfile, dataPost)
            ?.addToBackStack("Profile")
            ?.commit()
    }

    fun listenToolbar(){
        toolbarProfile.setNavigationOnClickListener{
            this.onBackPressed()
        }
    }

    fun setAppBarVisible(){
        appbarProfile.visibility = View.VISIBLE
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
                Toast.makeText(this, "No media to be Share", Toast.LENGTH_SHORT).show()
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
                putExtra(Intent.EXTRA_TEXT, media.link)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share To"))
        }
    }
}