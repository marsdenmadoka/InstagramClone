package com.madokasoftwares.instagramclone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.madokasoftwares.instagramclone.Fragments.GalleryImageDetailsFragment
import com.madokasoftwares.instagramclone.Fragments.ProfileFragment
import com.madokasoftwares.instagramclone.Model.Notification
import com.madokasoftwares.instagramclone.Model.Post
import com.madokasoftwares.instagramclone.Model.User
import com.madokasoftwares.instagramclone.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class NotificationAdapter(private val mContext: Context?,
                          private val mNotification:List<Notification>)
    :RecyclerView.Adapter<NotificationAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(mContext).inflate(R.layout.notifications_item_layout,parent,false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification=mNotification[position]
        UserInfo(holder.profileImage,holder.userName,notification.getUserId())

        if(notification.getText().equals("started following you")) //this message is in our database
        {
        holder.text.text="started following you" //in the text Textview we are displaying username
        }else if (notification.getText().equals("liked your post"))
        {
            holder.text.text="liked your post"
        }else if(notification.getText().contains("commented:")){
            holder.text.text=notification.getText().replace("commented:","commented: ")
        }else{
            holder.text.text=notification.getText()
        }

        if(notification.isIsPost()){ //if it is a post
            holder.postImage.visibility=View.VISIBLE
            getPostImage(holder.postImage,notification.getPostId())
        }else{ //if it is related to following matters
            holder.postImage.visibility=View.GONE
        }
   //when one clicks the item in our notification recycler view
        holder.itemView.setOnClickListener {
            if(notification.isIsPost()){ //if your click on a post notification//the will send you to the postdetails
                val editor=mContext!!.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                editor.putString("postId",notification.getPostId())
                editor.apply()
                (mContext as FragmentActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, GalleryImageDetailsFragment()).commit()

            }else{ //else if you click on a following/follow notification send to aprrofile fragment
                val editor=mContext!!.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                editor.putString("profileId",notification.getUserId())
                editor.apply()
                (mContext as FragmentActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, ProfileFragment()).commit()
            }
        }


    }

    override fun getItemCount(): Int {
        return mNotification.size
    }
    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var postImage:ImageView
        var profileImage:CircleImageView
        var userName:TextView
        var text:TextView

        init {
            postImage=itemView.findViewById(R.id.notification_post_image)
            profileImage=itemView.findViewById(R.id.notification_profile_image)
            userName=itemView.findViewById(R.id.username_notification)
            text=itemView.findViewById(R.id.comment_notification)

        }
    }
    private fun UserInfo(imageView: ImageView,UserName:TextView,publisherId:String){
        val usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId)
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user !!.getImage()).placeholder(R.drawable.profile).into(imageView)
                    UserName.text=user.getUsername()

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    private fun getPostImage(imageView: ImageView,postID:String){
        val postRef= FirebaseDatabase.getInstance()
            .reference.child("Posts").child(postID)

        postRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val post=snapshot.getValue<Post>(Post::class.java)
                    Picasso.get().load(post!!.getPostimage()).placeholder(R.drawable.profile).into(imageView)

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}