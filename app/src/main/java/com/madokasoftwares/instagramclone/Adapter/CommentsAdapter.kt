package com.madokasoftwares.instagramclone.Adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.madokasoftwares.instagramclone.Model.Comment
import com.madokasoftwares.instagramclone.R
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter (private val mContext:Context,
        private val mComment:MutableList<Comment>?
):RecyclerView.Adapter<CommentAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var imageProfile:CircleImageView
        var usernameTV:TextView
        var CommentTV:TextView

        init {
            imageProfile=itemView.findViewById(R.id.user_profile_image_comment)
            usernameTV=itemView.findViewById(R.id.user_name_comment)
            CommentTV=itemView.findViewById(R.id.comment_comment)
        }
    }

}