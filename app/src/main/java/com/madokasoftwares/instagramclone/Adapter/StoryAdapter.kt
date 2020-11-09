package com.madokasoftwares.instagramclone.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.madokasoftwares.instagramclone.AddStoryActivity
import com.madokasoftwares.instagramclone.MainActivity
import com.madokasoftwares.instagramclone.Model.Story
import com.madokasoftwares.instagramclone.Model.User
import com.madokasoftwares.instagramclone.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.view.*

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

        UserInfo(holder,story.getUserId(),position)//our method

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, AddStoryActivity::class.java)
           intent.putExtra("userid",story.getUserId())
            mContext.startActivity(intent)

        }
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


    private fun UserInfo(viewHolder: ViewHolder,userid:String,position: Int){
        val usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(userid)
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user !!.getImage()).placeholder(R.drawable.profile).into(viewHolder.story_image)

                   if(position != 0){
                       Picasso.get().load(user !!.getImage()).placeholder(R.drawable.profile).into(viewHolder.story_image_seen)
                       viewHolder.story_username!!.text=user.getUsername()
                   }


                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

}