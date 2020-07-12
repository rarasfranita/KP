package com.example.lotus.ui.explore.hashtag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.lotus.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val INDEX_OF_MEDIA = 0
private const val INDEX_OF_TEXT = 1

private val TAB_TITLES = mapOf(
    INDEX_OF_MEDIA to "Media",
    INDEX_OF_TEXT to "Text"
)

class TabsAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment =
        if (position == INDEX_OF_MEDIA) {
            ListMediaHashtag()
        } else {
            ListTextHashtag()
        }
}

class HashtagFragment : Fragment() {
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_hashtag, container, false)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fragmentView =
            requireNotNull(view) { "View should not be null when calling onActivityCreated" }

        val tabLayout: TabLayout = fragmentView.findViewById(R.id.tabsHashtag)
        viewPager = fragmentView.findViewById(R.id.view_pagerHashtag)
        viewPager.adapter = TabsAdapter(
            childFragmentManager,
            lifecycle
        )

//         connect the tabs and view pager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = TAB_TITLES[position]
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
        tabLayout.getTabAt(INDEX_OF_MEDIA)?.let { tabLayout.selectTab(it) }
    }
}