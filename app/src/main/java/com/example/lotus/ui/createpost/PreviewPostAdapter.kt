package com.example.lotus.ui.createpost

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.lotus.R
import com.example.lotus.models.MediaPost
import kotlinx.android.synthetic.main.layout_list_preview_post.view.*


class PreviewPostAdapter(private val listMedia: ArrayList<MediaPost>) :
    RecyclerView.Adapter<PreviewPostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_list_preview_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        Log.d(TAG, "onBindViewHolder: called.$position")
        holder.bindFeed(listMedia[position])
        holder.fabDeleteMediaItem.setOnClickListener(View.OnClickListener {
            listMedia.removeAt(position)
            notifyItemRemoved(position)
        })
    }

    override fun getItemCount(): Int {
        return listMedia.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val fabDeleteMediaItem = itemView.findViewById<View>(R.id.fabDeleteMedia)

        fun bindFeed(post: MediaPost){
            itemView.apply {
                var image: ImageView
                var video: VideoView

                image = itemView.findViewById(R.id.imagePreviewPost)
                video = itemView.findViewById(R.id.videoPreviewPost)

                if (post.type =="image"){
                    video.setVisibility(View.INVISIBLE)
                    image.setVisibility(View.VISIBLE)
                    imagePreviewPost.setImageURI(post.mediaPath)

                } else if (post.type == "video") {
                    video.setVisibility(View.VISIBLE)
                    image.setVisibility(View.INVISIBLE)
                    val mediacontroller = MediaController(context)
                    mediacontroller.setAnchorView(video)
                    video.setMediaController(mediacontroller)
                    video.setVideoURI(post.mediaPath)
                }
            }
        }
    }

    companion object {
        private const val TAG = "RecyclerViewAdapter"
    }
}