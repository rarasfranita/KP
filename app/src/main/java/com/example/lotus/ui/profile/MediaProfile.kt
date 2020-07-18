package com.example.lotus.ui.profile

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.lotus.R
import com.example.lotus.models.Post
import kotlinx.android.extensions.LayoutContainer

class MediaProfile : Fragment() {
    private var postData: ArrayList<Post>? = null

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