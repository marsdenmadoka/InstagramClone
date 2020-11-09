package com.madokasoftwares.instagramclone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.madokasoftwares.instagramclone.Model.Story
import com.madokasoftwares.instagramclone.R
import de.hdodenhof.circleimageview.CircleImageView

class StoryAdapter(private val mContext:Context,private val mStory:List<Story>):
RecyclerView.Adapter<StoryAdapter.ViewHolder>(){

    override fun getItemViewType(position: Int): Int {
        if(position == 0){ //if story is available
            return 0 //if story avaibale storyitem
        }
            return 1 //addstoryitem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //remember we are acessing two layout
       return if(viewType == 0){
            //remember we are acessing two layout
            val view=   LayoutInflater.from(mContext).inflate(R.layout.add_story_item,parent,false)
            ViewHolder(view)
        }
       else{
            //remember we are acessing two layout
            val view=   LayoutInflater.from(mContext).inflate(R.layout.story_item,parent,false)
             ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mStory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val story=mStory[position]
    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {

        //StoryItem
        var story_image_seen: CircleImageView?= null
        var story_image:CircleImageView?=null
        var story_username: TextView?=null

        //AddStoryItem
        var story_plus_btn:ImageView?=null
        var addStory_text: TextView?=null

        init {
            //storyitem
            story_image_seen=itemView.findViewById(R.id.story_seen)
            story_image=itemView.findViewById(R.id.story_image)
            story_username=itemView.findViewById(R.id.story_username)

            //addStoryitem
            story_plus_btn=itemView.findViewById(R.id.story_add)
            addStory_text=itemView.findViewById(R.id.ad_story_text)
        }
    }




}