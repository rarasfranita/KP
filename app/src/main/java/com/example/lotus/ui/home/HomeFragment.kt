package com.example.lotus.ui.home

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.example.lotus.R
import com.example.lotus.models.Post
import com.example.lotus.utils.FeedAdapter
import com.example.lotus.utils.MainFeedListAdapter
import org.json.JSONArray
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val TAG = "HomeFragment"

    private val mPhotos: ArrayList<ContactsContract.CommonDataKinds.Photo>? = null
    private val mPaginatedPhotos: ArrayList<ContactsContract.CommonDataKinds.Photo>? = null
    private var adapter: MainFeedListAdapter? = null
    private var resultsCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        val homeFeed: RecyclerView = v.findViewById(R.id.rcHomeFeed)
        loadFeed(homeFeed)

        listenAppToolbar(v)

        return v
    }

    fun loadFeed(homeFeed: RecyclerView){
        val list = ArrayList<Post>()
        val listFeed = arrayOf(
            "Aduh", "ganteng", "Kenapa"
        )
        homeFeed.setHasFixedSize(true)
        homeFeed.layoutManager = LinearLayoutManager(context)
        val urlPost = "https://storage.googleapis.com/fastwork-static/983f21e5-6a7b-44d4-ba88-bb90fdc1daac.jpg"
        for (i in 0 until listFeed.size){
            list.add(
                Post(
                    1,
                    "Gatau",
                    "Gatau",
                    "Gatau",
                    "Gatau",
                    listFeed.get(i),
                    urlPost,
                    "https://storage.googleapis.com/fastwork-static/983f21e5-6a7b-44d4-ba88-bb90fdc1daac.jpg"
                )
            )

            if (listFeed.size - 1 == i ){
                val adapter = FeedAdapter(list)
                adapter.notifyDataSetChanged()

                homeFeed.adapter = adapter
            }
        }
    }

    private fun listenAppToolbar(v: View){
        val toolbar: Toolbar = v.findViewById(R.id.appToolbar) as Toolbar
        toolbar.setTitle("Lotus");
        toolbar.setTitleTextAppearance(context, R.style.My_TextAppearance_Toolbar);

        toolbar.setNavigationOnClickListener {
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

}