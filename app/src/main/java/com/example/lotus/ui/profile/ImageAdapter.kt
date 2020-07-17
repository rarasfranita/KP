package com.example.lotus.ui.profile

import android.media.Image
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lotus.R
import kotlinx.android.synthetic.main.listmedia.view.*

class ImageAdapter(val images :List<Image>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.listmedia,parent,false)
        )
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
    }

    class ImageViewHolder(val view:View) :RecyclerView.ViewHolder(view)
}