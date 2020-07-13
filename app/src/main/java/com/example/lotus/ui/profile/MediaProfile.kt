package com.example.lotus.ui.profile

import android.media.Image
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lotus.R
import kotlinx.android.synthetic.main.media_profile_fragment.*

class MediaProfile : Fragment() {

    companion object {
        fun newInstance() = MediaProfile()
    }

    private lateinit var viewModel: MediaProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.media_profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MediaProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun showImages (images: List<Image>){
        rvprofilemedia.layoutManager = LinearLayoutManager(activity)
        rvprofilemedia.adapter=ImageAdapter(images)
    }

}