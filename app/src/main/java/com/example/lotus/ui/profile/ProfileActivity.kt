package com.example.lotus.ui.profile

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import coil.api.load
import coil.transform.CircleCropTransformation
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.Respon
import com.example.lotus.models.User
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.media_profile_fragment.*
import kotlinx.android.synthetic.main.snippet_myprofile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var button: Button
    private var myProfile: Boolean = true
    private val username = SharedPrefManager.getInstance(this).user.username
    private val token = SharedPrefManager.getInstance(this).token.token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        val extra = intent.getStringExtra("username")
        if (extra != null){
            getProfileData(extra)
        }else{
            setProfile(SharedPrefManager.getInstance(this).user)
        }
        //viewpager
        val tableLayout: TabLayout = findViewById(R.id.tab_Layout)
        val viewPager: ViewPager = findViewById(R.id.view_Pager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(MediaProfile(), title = "media")
        viewPagerAdapter.addFragment(TextFragment(), title = "text")

        viewPager.adapter = viewPagerAdapter
        tableLayout.setupWithViewPager(viewPager)

        ShowMyProfile()
    }

    fun getProfileData(username: String){
        AndroidNetworking.get(EnvService.ENV_API + "/users/{username}")
            .addHeaders("Authorization", "Bearer " + token)
            .addPathParameter("username", username)
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
                            val data = gson.fromJson(jsonRes, User::class.java)
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

    fun setProfile(data: User){
        if (data.avatar != null){
            profilePicture.load(data.avatar){
                transformations(CircleCropTransformation())
            }
        }

        usernameProfile.text = "${data.username}'s profile"
        nameprofile.text = data.name
        tvBiografi.text = data.bio
        totalPost.text = data.postsCount.toString()
    }

//    fun setMyProfile(){
//        val name = SharedPrefManager.getInstance(this).user.name
//        val profPic = SharedPrefManager.getInstance(this).user.avatar
//        val bio = SharedPrefManager.getInstance(this).user.bio
//        val postCount = SharedPrefManager.getInstance(this).user.postsCount
//        Log.d("AVATAR", profPic.toString())
//
//        if (profPic != null){
//            profilePicture.load(profPic){
//                transformations(CircleCropTransformation())
//            }
//        }
//
//        usernameProfile.text = "$username's profile"
//        nameprofile.text = name
//        tvBiografi.text = bio
//        totalPost.text = postCount.toString()
//
//    }

    private fun ShowMyProfile(){
        btnEditProfile.visibility= View.VISIBLE
        follow.visibility = View.GONE
        ivcollection.visibility=View.GONE
    }

    private fun ShowUserProfile(){
        follow.visibility=View.VISIBLE
        btnEditProfile.visibility=View.GONE
    }
    private fun showImages (images: List<Image>){
        rvprofilemedia.layoutManager = LinearLayoutManager(this)
        rvprofilemedia.adapter=ImageAdapter(images)
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

    /** override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navSecurity -> {
                Toast.makeText(this,"goChangePassoword",Toast.LENGTH_SHORT)
            }
            R.id.navRate-> {
                Toast.makeText(this,"goChangePassoword",Toast.LENGTH_SHORT)
            }
            R.id.navRate -> {
                Toast.makeText(this,"goChangePassoword",Toast.LENGTH_SHORT)
            }
            R.id.navLogout -> {
                Toast.makeText(this,"goChangePassoword",Toast.LENGTH_SHORT)
            }
        }
        return super.onOptionsItemSelected(item)
    }
*/
}