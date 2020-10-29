package com.madokasoftwares.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.madokasoftwares.instagramclone.Model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
     private var checker =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)
        firebaseUser=FirebaseAuth.getInstance().currentUser!!


        logOut_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingActivity,SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        save_info_profile_btn.setOnClickListener {

            if(checker == "clicked") //if profile image
            {

            }else{ //if no profile image
                updateUserInfoOnly()
            }
        }

        DisplayUserInfo() //to understand how the code work practical check the xml file
    }

    private fun updateUserInfoOnly() //update to firebase
    {
        when{
            TextUtils.isEmpty(full_name.toString())-> Toast.makeText(this,"full name is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(username.toString())-> Toast.makeText(this,"username is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(bio.toString())-> Toast.makeText(this,"About is required", Toast.LENGTH_LONG).show()
            else ->{
                val usersRef= FirebaseDatabase.getInstance().reference.child("Users")

                val userMap=HashMap<String,Any>()
                userMap["fullname"]=full_name.text.toString().toLowerCase() //remember the names of the usermap e.g userMap["fullname"] shoiuld be the same as those of signUp activity to avoid overwritting
                userMap["username"]=username.text.toString().toLowerCase()
                userMap["bio"]=bio.text.toString().toLowerCase()

                usersRef.child(firebaseUser.uid).updateChildren(userMap) //updateChildren since we are updating/changing the info in the database when is already there

                Toast.makeText(this,"Account info update successfully",Toast.LENGTH_LONG).show()
                val intent = Intent(this@AccountSettingActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }


    private fun DisplayUserInfo(){ //display to account activity.xml
        val usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java) //User here is our model class
                    Picasso.get().load(user !!.getImage()).placeholder(R.drawable.profile).into(profile_image_view_profile_frag)
                    username.setText(user.getUsername())
                    full_name.setText(user.getFullname())
                    bio.setText(user.getBio())

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }


}