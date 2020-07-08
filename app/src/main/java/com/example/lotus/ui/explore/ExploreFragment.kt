package com.example.lotus.ui.explore

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.lotus.R
import com.example.lotus.ui.home.HomeActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val INDEX_OF_MEDIA = 0
private const val INDEX_OF_TEXT = 1
private const val NUM_TABS = 2

private val TAB_TITLES = mapOf(
    INDEX_OF_MEDIA to "media",
    INDEX_OF_TEXT to "text"
)

class TabsAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = NUM_TABS
    override fun createFragment(position: Int): Fragment =
        if (position == INDEX_OF_MEDIA) {
            ListMedia()
        } else {
            ListText()
        }
}

class ExploreFragment : Fragment() {
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_explore, container, false)
        listenAppToolbar(v)

        return v
    }

    private fun listenAppToolbar(v:View) {
        val toolbar: Toolbar = v.findViewById(R.id.tbExplore) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity: AppCompatActivity = activity as AppCompatActivity
        val fragmentView =
            requireNotNull(view) { "View should not be null when calling onActivityCreated" }

        val toolbar: Toolbar = fragmentView.findViewById(R.id.toolbar)
        activity.setSupportActionBar(toolbar)

        toolbar.setOnMenuItemClickListener() {
            when (it.itemId) {
                R.id.fragmentNewDM ->
                    view?.findNavController()
                        ?.navigate(R.id.nav_home_to_channel)
            }
            true
        }

        val tabLayout: TabLayout = fragmentView.findViewById(R.id.tabs)
        viewPager = fragmentView.findViewById(R.id.view_pager)
        viewPager.adapter = TabsAdapter(childFragmentManager, lifecycle)

//         connect the tabs and view pager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = TAB_TITLES[position]
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
        tabLayout.getTabAt(INDEX_OF_MEDIA)?.let { tabLayout.selectTab(it) }
    }
}