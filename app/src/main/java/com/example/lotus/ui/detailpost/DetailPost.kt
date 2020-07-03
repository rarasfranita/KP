package com.example.lotus.ui.detailpost

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lotus.R
import com.example.lotus.models.*
import matrixsystems.nestedexpandablerecyclerview.RowAdapter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DetailPost : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val mediaDatas: MediaData = MediaData("Halah", "image")
    val arrMd = arrayOf(mediaDatas)
    val has = arrayOf("halo", "semua", "love")
    public  val postData: Post = Post(1, "rhmdnrhuda", "2 Secon ago", "Helo semua apa kabar ", "10k", "100", "", has, arrMd)

    lateinit var rowAdapter: RowAdapter
    lateinit var rows : MutableList<CommentRowModel>
    lateinit var recyclerView : RecyclerView


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

        recyclerView = v.findViewById(R.id.recycler_view)
        rows = mutableListOf()
        val context: Context = this.requireContext()
        rowAdapter = RowAdapter(context, rows)

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        recyclerView.adapter = rowAdapter

        populateData()
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

    fun populateData(){
        var child1 : MutableList<ChildComment> = mutableListOf()
        child1.add(ChildComment("Hello", "aduh", "hhhhh", "1 second ago", "100"))
        child1.add(ChildComment("gatu", "aduh", "hhhhh", "1 second ago", "23"))
        child1.add(ChildComment("kenapa", "aduh", "hhhhh", "1 second ago", "4000"))
        child1.add(ChildComment("pusing", "aduh", "hhhhh", "1 second ago", "1"))


        rows.add(CommentRowModel(CommentRowModel.PARENT, Comment("Hello world", "aduhduh", "helo", "1 second", "10k", child1)))
        rows.add(CommentRowModel(CommentRowModel.PARENT, Comment("Ih aplikasinya bagus banget suka deh", "rhmdnrhuda", "helo", "1 second", "1", child1)))

        rowAdapter.notifyDataSetChanged()
    }
}