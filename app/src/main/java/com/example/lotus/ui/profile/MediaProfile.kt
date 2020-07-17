package com.example.lotus.ui.profile

import android.media.Image
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lotus.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.listmedia.*
import kotlinx.android.synthetic.main.media_profile_fragment.*

class MediaProfile : Fragment() {

    companion object {
        fun newInstance() = MediaProfile().apply{
            arguments=Bundle().apply {
            }
        }
    }

    private lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //columnCount=itgetInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.media_profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val images = listOf(
            ImageProfile(""),
            ImageProfile(""),
            ImageProfile(""),
            ImageProfile("")
        )
    }

    class ImageAdapter(val images: List<Image>) :
        RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            return ImageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.listmedia, parent, false)
            )
        }

        override fun getItemCount(): Int = images.size

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val image = images[position]
        }

        class ImageViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    }

    fun replaceItems(images: List<Image>) {

    }

    inner class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer
}