package com.example.lotus.ui.profile

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.lotus.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.media_profile_fragment.*
import kotlinx.android.synthetic.main.snippet_myprofile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var button: Button

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
        ShowUserProfile()
    }

    private fun ShowMyProfile(){
        btnEditProfile.visibility= View.VISIBLE
        follow.visibility = View.GONE
        following.visibility=View.GONE
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
    private fun popupMyProfile(){
        val popupMenu= PopupMenu(applicationContext,ivEllipses)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
         //       R.id.navSecurity -> {
           //     }
            }
        }
    }

}