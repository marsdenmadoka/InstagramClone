package com.madokasoftwares.instagramclone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.FirebaseDatabaseKtxRegistrar
import com.madokasoftwares.instagramclone.Fragments.ProfileFragment
import com.madokasoftwares.instagramclone.Model.User
import com.madokasoftwares.instagramclone.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

//connects our user_item_layout.xml to our recycleView in the fragment_search.xml
class UserAdapter( private var mContext:Context,
                   private var mUser:List<User>,//our model class
                   private var isFragment:Boolean=false) : RecyclerView.Adapter<UserAdapter.ViewHolder>()

 {

     private var firebaseUser: FirebaseUser?=FirebaseAuth.getInstance().currentUser


     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
      val view=LayoutInflater.from(mContext).inflate(R.layout.user_item_layout,parent,false)
         return UserAdapter.ViewHolder(view)
     }

     override fun getItemCount(): Int {
         return mUser.size
     }

     class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
         var userName:TextView = itemView.findViewById(R.id.username_search) //put the username in a view called username_search
         var FullName:TextView = itemView.findViewById(R.id.user_full_name_search)
         var userProfileImage:CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
         var followButton:Button = itemView.findViewById(R.id.follow_btn_search)

     }


     //assigns  our views to hold data from our database
     override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
         val user =mUser[position]

         holder.userName.text = user.getUsername()  //get Username from db
         holder.FullName.text=user.getFullname()
         Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.userProfileImage)



         checkFollowingStatus(user.getUID(),holder.followButton)


         //when we click the iem in the recyclerview in the search fragment
         holder.itemView.setOnClickListener(View.OnClickListener {
           if(isFragment){
               val pref = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
               pref.putString("profileId",user.getUID())
               pref.apply()

               (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                   .replace(R.id.frame_container,ProfileFragment()).commit()
           }
         })


         holder.followButton.setOnClickListener {// check the CheckFollowingStatus code first
             if(holder.followButton.text.toString()== "Follow"){

                 firebaseUser?.uid.let{it1 ->
                     FirebaseDatabase.getInstance().reference
                         .child("Follow").child(it1.toString())
                         .child("Following").child(user.getUID()) //add the  person in my following list .i.e people who am following
                         .setValue(true) .addOnCompleteListener { task->
                             if(task.isSuccessful)
                             {
                                 firebaseUser?.uid.let{it1 ->
                                     FirebaseDatabase.getInstance().reference
                                         .child("Follow").child(user.getUID())
                                         .child("Followers").child(it1.toString()) //adds me in the person's followers list :people who follow him\her
                                         .setValue(true) .addOnCompleteListener { task->
                                             if(task.isSuccessful){


                                             }
                                             }
                                             }
                                            }
                                            }
                                           }
                              AddNotification(user.getUID())
                                         }

             else{

                 firebaseUser?.uid.let{it1 ->
                     FirebaseDatabase.getInstance().reference
                         .child("Follow").child(it1.toString())
                         .child("Following").child(user.getUID()) //remove the person in my following list
                         .removeValue().addOnCompleteListener { task->
                             if(task.isSuccessful)
                             {
                                 firebaseUser?.uid.let{it1 ->
                                     FirebaseDatabase.getInstance().reference
                                         .child("Follow").child(user.getUID())
                                         .child("Followers").child(it1.toString()) //remove me from person  followers list
                                         .removeValue().addOnCompleteListener { task->
                                             if(task.isSuccessful){


                                             }
                                         }
                                 }
                             }
                         }
                 }
             }
         }


     }

     private fun checkFollowingStatus(uid: String, followButton: Button) {  //check the user if he follows or is following te update te text at follow Button
         val followingRef = firebaseUser?.uid.let { it1 ->
             FirebaseDatabase.getInstance().reference
                 .child("Follow").child(it1.toString())
                 .child("Following")
         }
         followingRef.addValueEventListener(object : ValueEventListener {
             override fun onDataChange(datasnapshot: DataSnapshot) {
               if (datasnapshot.child(uid).exists()){

                   followButton.text="Following"
               }else{
                   followButton.text="Follow"
               }
             }

             override fun onCancelled(error: DatabaseError) {

             }
         })

         
     }

     private fun AddNotification(userId:String){
         val NotificationRef=FirebaseDatabase.getInstance().reference
             .child("Notifications").child(userId)
         val notiMap=HashMap<String,Any>()
         notiMap["userid"] = firebaseUser!!.uid //the online person who is going to like my post
         notiMap["text"]="started following you"
         notiMap["postid"]="" //since there is not post
         notiMap["ispost"]=false//this is not a post

         NotificationRef.push().setValue(notiMap)

     }

 }