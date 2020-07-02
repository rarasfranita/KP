package com.example.lotus.ui.detailpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.lotus.R
import com.example.lotus.models.MediaData
import com.example.lotus.models.Post

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DetailPost : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val mediaDatas: MediaData = MediaData("Halah", "image")
    val arrMd = arrayOf(mediaDatas)
    val has = arrayOf("halo", "semua", "love")
    public  val postData: Post = Post(1, "rhmdnrhuda", "2 Secon ago", "Helo semua apa kabar ", "10k", "100", "", has, arrMd)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_detail_post, container, false)
        setView(v)
        return v
    }

    fun setView(view: View){
        val username :TextView = view.findViewById<View>(R.id.textUsernamePost) as TextView
        val caption : TextView = view.findViewById<View>(R.id.textCaption) as TextView
        val like : TextView = view.findViewById<View>(R.id.textIctLikesPost) as TextView
        val ava : ImageView = view.findViewById<View>(R.id.imageAvatarPost) as ImageView
        val comment: TextView = view.findViewById<View>(R.id.textIcCommentPost) as TextView
        val time: TextView = view.findViewById<View>(R.id.textTimePost) as TextView

        username.text = postData.name
        like.text = postData.likes
        caption.text = postData.caption
        comment.text = postData.comments
        time.text = postData.time
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailPost().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}