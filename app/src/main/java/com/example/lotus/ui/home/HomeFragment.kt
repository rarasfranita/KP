package com.example.lotus.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.lotus.R
import com.example.lotus.ui.dm.MainActivityDM
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.explore.hashtag.HashtagActivity
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        listenAppToolbar(v)

        return v
    }

    private fun listenAppToolbar(v: View) {
        val toolbar: Toolbar = v.findViewById(R.id.appToolbar) as Toolbar
        toolbar.setTitleTextAppearance(context, R.style.My_TextAppearance_Toolbar)

        toolbar.setLogo(R.drawable.ic_notification)

        toolbar.setNavigationOnClickListener {
            rcHomeFeed.smoothScrollToPosition(0);
        }

        val logoView: View? = getToolbarLogoIcon(toolbar)
        logoView?.setOnClickListener{
            // TODO Route to Notification

        }

        toolbar.setOnMenuItemClickListener() {
            when (it.itemId) {
                R.id.direct_message ->
                    toolbar.context.startActivity(Intent(context, MainActivityDM::class.java))
            }
            when (it.itemId) {
                R.id.explore ->
                    toolbar.context.startActivity(Intent(context, GeneralActivity::class.java))
            }
            when (it.itemId) {
                R.id.profile ->
                    toolbar.context.startActivity(Intent(context, HashtagActivity::class.java))
            }
            true
        }
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