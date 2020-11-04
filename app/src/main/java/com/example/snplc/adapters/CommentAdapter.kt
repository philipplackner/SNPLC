package com.example.snplc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.snplc.R
import com.example.snplc.data.entities.Comment
import com.example.snplc.data.entities.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_comment.view.*
import javax.inject.Inject

class CommentAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCommentUsername: TextView = itemView.tvCommentUsername
        val tvComment: TextView = itemView.tvComment
        val ibDeleteComment: ImageButton = itemView.ibDeleteComment
        val ivCommentUserProfilePicture: ImageView = itemView.ivCommentUserProfilePicture
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Comment>() {
        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.commentId == newItem.commentId
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var comments: List<Comment>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_comment,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.apply {
            glide.load(comment.profilePictureUrl).into(ivCommentUserProfilePicture)

            ibDeleteComment.isVisible = comment.uid == FirebaseAuth.getInstance().uid!!

            tvComment.text = comment.comment
            tvCommentUsername.text = comment.username
            tvCommentUsername.setOnClickListener {
                onUserClickListener?.let { click ->
                    click(comment)
                }
            }
            ibDeleteComment.setOnClickListener {
                onDeleteCommentClickListener?.let { click ->
                    click(comment)
                }
            }
        }
    }

    private var onUserClickListener: ((Comment) -> Unit)? = null
    private var onDeleteCommentClickListener: ((Comment) -> Unit)? = null

    fun setOnUserClickListener(listener: (Comment) -> Unit) {
        onUserClickListener = listener
    }

    fun setOnDeleteCommentClickListener(listener: (Comment) -> Unit) {
        onDeleteCommentClickListener = listener
    }

}