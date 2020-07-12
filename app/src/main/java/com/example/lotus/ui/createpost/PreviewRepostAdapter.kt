package com.example.lotus.ui.createpost

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.example.lotus.R
import com.example.lotus.models.MediaData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.layout_list_preview_post.view.*


class PreviewRepostAdapter(private val listMedia: ArrayList<MediaData>) :
    RecyclerView.Adapter<PreviewRepostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        Log.d(TAG, "onBindViewHolder: called.")
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_list_preview_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        Log.d(TAG, "onBindViewHolder: called.")
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

        fun bindFeed(post: MediaData){
            itemView.apply {
                val image: ImageView = itemView.findViewById(R.id.imagePreviewPost)
                val video: VideoView = itemView.findViewById(R.id.videoPreviewPost)
                val delete: FloatingActionButton = itemView.findViewById(R.id.fabDeleteMedia)

                delete.visibility = View.GONE

                if (post.type =="image"){
                    video.setVisibility(View.INVISIBLE)
                    image.setVisibility(View.VISIBLE)
                    imagePreviewPost.load(post.link){}
                } else if (post.type == "video") {
                    video.setVisibility(View.VISIBLE)
                    image.setVisibility(View.INVISIBLE)
                    val mediacontroller = MediaController(context)
                    mediacontroller.setAnchorView(video)
                    video.setMediaController(mediacontroller)
                    val uri = Uri.parse(post.link)
                    video.setVideoURI(uri)
                }
            }
        }
    }

    companion object {
        private const val TAG = "RecyclerViewAdapter"
    }
}