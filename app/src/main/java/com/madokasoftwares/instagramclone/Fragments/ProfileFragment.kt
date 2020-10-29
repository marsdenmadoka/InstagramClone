package com.madokasoftwares.instagramclone.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.madokasoftwares.instagramclone.AccountSettingActivity
import com.madokasoftwares.instagramclone.Model.User
import com.madokasoftwares.instagramclone.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private lateinit var myprofileid: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
     val view =inflater.inflate(R.layout.fragment_profile, container, false)


        //passing our shared preferences from  UserAdapter ItemView.onClickListener /when we click our recycle view item
        firebaseUser=FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(pref != null){
            this.myprofileid= pref.getString("profileId","none").toString()
        }

      if(myprofileid == firebaseUser.uid)
      {
        view.edit_account_settings_btn.text="Edit Profile" //if its the owner in the  account setting then show the btn as EditProfile
      }else if(myprofileid != firebaseUser.uid){

       checkFollowAndFollowingButtonStatus() // if its not the owner then show the follow and following status

      }


    view.edit_account_settings_btn.setOnClickListener {
        val getButtonText=view.edit_account_settings_btn.text.toString()

        when
        {
            getButtonText == "Edit Profile" -> startActivity(Intent(context,AccountSettingActivity::class.java))

            getButtonText== "Follow"-> { //follow the person
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(myprofileid)
                        .setValue(true)
                }
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(myprofileid)
                        .child("Followers").child(it1.toString())
                        .setValue(true)
                }
            }

            getButtonText== "Following"-> {  //unfollow the person
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(myprofileid)
                        .removeValue()
                }
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(myprofileid)
                        .child("Followers").child(it1.toString())
                        .removeValue()
                }
            }



        }

    }

        checkNoOfFollowers() //get total no of followers and set it in the followers textView
        checkNoOfFollowing() //get total no of following and set it in the following textView
        DisplayUserInfo()

        return view
    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }
        if(followingRef != null){
            followingRef.addValueEventListener(object: ValueEventListener
            {
                override fun onDataChange(snapshot: DataSnapshot) {
             if(snapshot.child(myprofileid).exists()){
                 view?.edit_account_settings_btn?.text="Following" //the Edit profile button turn the ext to Following

             }else{
                 view?.edit_account_settings_btn?.text="Follow"
             }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    ///get and set the total number of followers in the total_followers view
    private fun checkNoOfFollowers(){

        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(myprofileid) //check in our database to get the no of followers REF:refee the code of follow and following in the UserAdapter
                .child("Followers")

        followersRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    view?.total_followers?.text=snapshot.childrenCount.toString() //set the total no of followers in the total_followers view
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }
    //set the total number of following in the total_following view
    private fun checkNoOfFollowing(){

        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(myprofileid)
                .child("Following")

        followersRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    view?.total_following ?.text=snapshot.childrenCount.toString() //set the total no of following in the total_following view
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun DisplayUserInfo(){
        val usersRef=FirebaseDatabase.getInstance().getReference().child("Users").child(myprofileid)
        usersRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
//            if(context != null){
//                return
//            }
                if(snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user !!.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile)
                    view?.profile_fragment_username?.text=user.getUsername()
                    view?.full_name_profile_frag?.text=user.getFullname()
                    view?.bio_profile_frag?.text=user.getBio()

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    override fun onStop() { //when the app is minimised
        super.onStop()

        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }
}