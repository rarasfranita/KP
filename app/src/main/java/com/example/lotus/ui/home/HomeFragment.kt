package com.example.lotus.ui.home

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.eschao.android.widget.elasticlistview.ElasticListView
import com.example.lotus.R
import com.example.lotus.utils.MainFeedListAdapter
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONArray
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val TAG = "HomeFragment"
    private val mContext: HomeFragment = this@HomeFragment

    private val mPhotos: ArrayList<ContactsContract.CommonDataKinds.Photo>? = null
    private val mPaginatedPhotos: ArrayList<ContactsContract.CommonDataKinds.Photo>? = null
    private val mFollowing: ArrayList<String>? = null
    private val recursionIterator = 0

//        private mListView: ListView? =
    private var mListView: ElasticListView? = null
    private var adapter: MainFeedListAdapter? = null
    private var resultsCount = 0
//    private val mUserAccountSettings: ArrayList<UserAccountSettings>? = null

    //    private ArrayList<UserStories> mAllUserStories = new ArrayList<>();
    private val mMasterStoriesArray: JSONArray? = null

    private val mRecyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val v = inflater.inflate(R.layout.fragment_home, container, false)
//        mListView = view!!.findViewById<View>(R.id.listView) as ElasticListView

//        initListViewRefresh()
//        getFollowing()
        displayPhotos()
        listenAppToolbar(v)

        return v
    }

    private fun listenAppToolbar(v: View){
        val toolbar: Toolbar = v.findViewById(R.id.appToolbar) as Toolbar

        toolbar.setNavigationOnClickListener {
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.explore -> {
                    // Handle search icon press
                    Log.d("Hello", "world")
                    true
                }
                R.id.direct_message -> {
                    // Handle more item (inside overflow menu) press
                    true
                }
                R.id.profile -> {
                    // Handle more item (inside overflow menu) press
                    true
                }
                else -> false
            }
        }
    }

    private fun displayPhotos() {
//        mPaginatedPhotos = new ArrayList<>();
        if (mPhotos != null) {
            try {

                //sort for newest to oldest
//                Collections.sort(mPhotos,
//                    Comparator<Any> { o1, o2 ->
//                        o1.
//                    })

                //we want to load 10 at a time. So if there is more than 10, just load 10 to start
                var iterations: Int = mPhotos.size
                if (iterations > 10) {
                    iterations = 10
                }
                //
                resultsCount = 0
                for (i in 0 until iterations) {
                    mPaginatedPhotos?.add(mPhotos.get(i))
                    resultsCount++
//                    Log.d(
//                        HomeFragment.TAG,
//                        "displayPhotos: adding a photo to paginated list: " + mPhotos.get(i)
//                            .getPhoto_id()
//                    )
                }
                adapter = activity?.let {
                    MainFeedListAdapter(
                        it,
                        R.layout.layout_mainfeed_listitem,
                        mPaginatedPhotos
                    )
                }
                mListView?.setAdapter(adapter)

                // Notify update is done
                mListView?.notifyUpdated()
            } catch (e: IndexOutOfBoundsException) {
                Log.e(
                    TAG,
                    "displayPhotos: IndexOutOfBoundsException:" + e.message
                )
            } catch (e: NullPointerException) {
                Log.e(
                    TAG,
                    "displayPhotos: NullPointerException:" + e.message
                )
            }
        }
    }

//    fun displayMorePhotos() {
//        Log.d(HomeFragment.TAG, "displayMorePhotos: displaying more photos")
//        try {
//            if (mPhotos.size > resultsCount && mPhotos.size > 0) {
//                val iterations: Int
//                iterations = if (mPhotos.size > resultsCount + 10) {
//                    Log.d(
//                        HomeFragment.TAG,
//                        "displayMorePhotos: there are greater than 10 more photos"
//                    )
//                    10
//                } else {
//                    Log.d(
//                        HomeFragment.TAG,
//                        "displayMorePhotos: there is less than 10 more photos"
//                    )
//                    mPhotos.size - resultsCount
//                }
//
//                //add the new photos to the paginated list
//                for (i in resultsCount until resultsCount + iterations) {
//                    mPaginatedPhotos.add(mPhotos.get(i))
//                }
//                resultsCount = resultsCount + iterations
//                adapter.notifyDataSetChanged()
//            }
//        } catch (e: IndexOutOfBoundsException) {
//            Log.e(
//                HomeFragment.TAG,
//                "displayPhotos: IndexOutOfBoundsException:" + e.message
//            )
//        } catch (e: NullPointerException) {
//            Log.e(HomeFragment.TAG, "displayPhotos: NullPointerException:" + e.message)
//        }
//    }
}