package matrixsystems.nestedexpandablerecyclerview

import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lotus.R
import com.example.lotus.models.CommentRowModel
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.utils.dateToFormatTime
import kotlinx.android.synthetic.main.comment_child.view.*
import kotlinx.android.synthetic.main.comment_parent.view.*

class RowAdapter (val context: Context, var commentRowModels: MutableList<CommentRowModel>, detailPost: DetailPost) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var actionLock = false
    private var mDetailPost = detailPost

    class CommentParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var textComment: TextView
        internal var like: TextView
        internal var time: TextView
        internal var avatar: ImageView

        init {
            this.textComment = itemView.findViewById(R.id.textCommentParent) as TextView
            this.like = itemView.findViewById(R.id.textLikeComentParent) as TextView
            this.time = itemView.findViewById(R.id.textTimeCommentParent) as TextView
            this.avatar = itemView.findViewById(R.id.imageAvatarCommentParent) as ImageView
        }
    }

    class CommentChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var textComment: TextView
        internal var like: TextView
        internal var time: TextView
        internal var avatar: ImageView
        internal var reply: TextView

        init {
            this.textComment = itemView.findViewById(R.id.textCommentChild) as TextView
            this.like = itemView.findViewById(R.id.textLikeComentChild) as TextView
            this.time = itemView.findViewById(R.id.textTimeCommentChild) as TextView
            this.avatar = itemView.findViewById(R.id.imageAvatarCommentChild) as ImageView
            this.reply = itemView.findViewById(R.id.replyCommentChild) as TextView
        }
    }

    override fun getItemViewType(position: Int): Int {
        val type = commentRowModels[position].type
        return type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder = when (viewType) {
            CommentRowModel.PARENT -> CommentParentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_parent, parent, false))
            CommentRowModel.CHILD -> CommentChildViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_child, parent, false))
            else -> CommentParentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_parent, parent, false))
        }
        return viewHolder
    }


    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val row = commentRowModels[p1]
        when(row.type){
            CommentRowModel.PARENT -> {
                (p0 as CommentParentViewHolder).textComment.setText(Html.fromHtml("<b>" + row.parent.username +"</b> " + row.parent.text))
                p0.like.visibility = View.GONE
                dateToFormatTime(p0.time, row.parent.createdAt)
//                p0.like.setText(row.parent.name)
                if(row.parent.replies == null || row.parent.replies!!.size == 0) {
                    p0.itemView.textShowReplayComment.visibility = View.GONE
                }
                else {
                    if(p0.itemView.textShowReplayComment.visibility == View.GONE){
                        p0.itemView.textShowReplayComment.visibility = View.VISIBLE
                    }

                    if (row.isExpanded) {
                        p0.itemView.textShowReplayComment.text = "Show less"
                    }else{
                        p0.itemView.textShowReplayComment.text = "Load more replies"
                    }

                    p0.itemView.textShowReplayComment.setOnClickListener {
                        if (!actionLock) {
                            actionLock = true
                            if (row.isExpanded) {
                                row.isExpanded = false
                                collapse(p1)
                            } else {
                                row.isExpanded = true
                                expand(p1)
                            }
                        }
                    }
                }

                p0.itemView.replyCommentParent.setOnClickListener {
                    mDetailPost.setCommentID(row.parent.id!!)
                    mDetailPost.openEditTextComment(mDetailPost.requireView())
                }
            }
            CommentRowModel.CHILD -> {
                (p0 as CommentChildViewHolder).textComment.setText(Html.fromHtml("<b>" + row.child.username +"</b> " + row.child.text))
//                p0.like.setText(row.child.name)
                p0.like.visibility = View.GONE
                dateToFormatTime(p0.time, row.child.createdAt)
                p0.itemView.replyCommentChild.setOnClickListener {
                    mDetailPost.setCommentID(row.child.parentId!!)
                    mDetailPost.openEditTextComment(mDetailPost.requireView())
                }
            }
        }
    }


    fun expand(position: Int) {
        var nextPosition = position
        val row = commentRowModels[position]

        when (row.type) {
            CommentRowModel.PARENT -> {
                for (state in row.parent.replies!!) {
                    commentRowModels.add(++nextPosition, CommentRowModel(CommentRowModel.CHILD, state))
                }
                notifyDataSetChanged()
            }
        }
        actionLock = false
    }

    fun collapse(position: Int) {
        Log.d("Cek collapse", position.toString())
        val row = commentRowModels[position]
        val nextPosition = position + 1

        when (row.type) {

            CommentRowModel.PARENT -> {
                outerloop@ while (true) {
                    if (nextPosition == commentRowModels.size || commentRowModels.get(nextPosition).type === CommentRowModel.PARENT) {
                        break@outerloop
                    }
                    commentRowModels.removeAt(nextPosition)
                }
                notifyDataSetChanged()
            }

            CommentRowModel.CHILD -> {
                outerloop@ while (true) {
                    if (nextPosition == commentRowModels.size || commentRowModels.get(nextPosition).type === CommentRowModel.PARENT || commentRowModels.get(nextPosition).type === CommentRowModel.CHILD
                    ) {
                        break@outerloop
                    }
                    commentRowModels.removeAt(nextPosition)
                }
                notifyDataSetChanged()
            }
        }
        actionLock = false
    }

    override fun getItemCount() = commentRowModels.size

}