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
<<<<<<< HEAD
import com.example.lotus.models.Post
import com.example.lotus.ui.dm.MainActivityDM
import kotlin.collections.ArrayList
=======
>>>>>>> bad00be9e982feb26d17470738f804032427bfa7


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

    private fun listenAppToolbar(v: View){
        val toolbar: Toolbar = v.findViewById(R.id.appToolbar) as Toolbar
        toolbar.setTitleTextAppearance(context, R.style.My_TextAppearance_Toolbar)

        toolbar.setNavigationOnClickListener {
        }

        toolbar.setOnMenuItemClickListener(){
            when (it.itemId){
                R.id.direct_message ->
                    toolbar.context.startActivity(Intent(context, MainActivityDM::class.java))
            }
            true
        }
    }
}