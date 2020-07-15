package com.example.lotus.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.lotus.R
import com.example.lotus.models.Post
import com.example.lotus.ui.notification.NotificationActivity
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
        v!!.setOnTouchListener { v, event ->
            Log.d(TAG, event.toString())
            if (event.action == MotionEvent.ACTION_MOVE) {
                Log.d(TAG, event.toString())
            }
            true
        }
        return v
    }

    private fun listenAppToolbar(v: View){
        val toolbar: Toolbar = v.findViewById(R.id.appToolbar) as Toolbar

        toolbar.setLogo(R.drawable.ic_notification)

        toolbar.setNavigationOnClickListener {
            rcHomeFeed.smoothScrollToPosition(0);
        }

        val logoView: View? = getToolbarLogoIcon(toolbar)
        logoView?.setOnClickListener{
            startActivity(Intent(context, NotificationActivity::class.java))
        }


        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.explore -> {
                    true
                }
                R.id.direct_message -> {
                    true
                }
                R.id.profile -> {
                    true
                }
                else -> false
            }
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