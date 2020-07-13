package com.example.lotus.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
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


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val TAG = "HomeFragment"

    private val mPhotos: ArrayList<ContactsContract.CommonDataKinds.Photo>? = null
    private val mPaginatedPhotos: ArrayList<ContactsContract.CommonDataKinds.Photo>? = null
    private var resultsCount = 0

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

        toolbar.setNavigationOnClickListener {
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
}