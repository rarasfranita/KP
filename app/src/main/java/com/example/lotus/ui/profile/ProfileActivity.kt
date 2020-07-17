package com.example.lotus.ui.profile

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import coil.api.load
import coil.transform.CircleCropTransformation
import com.example.lotus.R
import com.example.lotus.storage.SharedPrefManager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.media_profile_fragment.*
import kotlinx.android.synthetic.main.snippet_myprofile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var button: Button
    private val username = SharedPrefManager.getInstance(this).user.username
    private val token = SharedPrefManager.getInstance(this).token.token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //viewpager
        val tableLayout: TabLayout = findViewById(R.id.tab_Layout)
        val viewPager: ViewPager = findViewById(R.id.view_Pager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(MediaProfile(), title = "media")
        viewPagerAdapter.addFragment(TextFragment(), title = "text")

        viewPager.adapter = viewPagerAdapter
        tableLayout.setupWithViewPager(viewPager)

        ShowMyProfile()
        setProfile()
    }

    fun getProfileData(){

    }

    fun setProfile(){
        val name = SharedPrefManager.getInstance(this).user.name
        val profPic = SharedPrefManager.getInstance(this).user.avatar
        val bio = SharedPrefManager.getInstance(this).user.bio
        val postCount = SharedPrefManager.getInstance(this).user.postsCount
        Log.d("AVATAR", profPic.toString())

        if (profPic != null){
            profilePicture.load(profPic){
                transformations(CircleCropTransformation())
            }
        }

        usernameProfile.text = "$username's profile"
        nameprofile.text = name
        tvBiografi.text = bio
        totalPost.text = postCount.toString()

    }

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