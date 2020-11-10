package com.madokasoftwares.instagramclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.madokasoftwares.instagramclone.Adapter.UserAdapter
import com.madokasoftwares.instagramclone.Model.User
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class ShowUsersActivity : AppCompatActivity() {
    //this activity shows people who have liked your post when you click the liked by and also people who follow and following
    var id: String = ""
    var title:String=""

    var userAdapter:UserAdapter?=null
    var userList:List<User>?=null
    var idList:List<String>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)


        val intent=intent
        id=intent.getStringExtra("id") //we fetched this from our profile fragment
        title = intent.getStringExtra("title")

        val toolbar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=title  //dislpay our titile in the toolbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {//when user clicks on the back button return them to previous
            finish()
        }

        var recyclerview:RecyclerView
        recyclerview=findViewById(R.id.recycle_view)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager=LinearLayoutManager(this)
        userList = ArrayList()
        userAdapter= UserAdapter(this,userList as ArrayList<User>,false)
        recyclerview.adapter=userAdapter

        idList=ArrayList()
        when(title){
            "likes" -> getLikes()
            "following"->getFollowing()
            "followers"->getFollowers()
            "views"->getViews()
        }
    }


    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id)
            .child("Followers")

        followersRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                (userList as ArrayList<User>).clear()
                for(snapshot in datasnapshot.children){
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                showUsers()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowing() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id)
            .child("Following")

        followersRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                (userList as ArrayList<User>).clear()
                for(snapshot in datasnapshot.children){
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                showUsers()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getLikes() {
        val LikesRef= FirebaseDatabase.getInstance().reference
            .child("Likes").child(id)
        LikesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(datasnapshot.exists()){
                    (idList as ArrayList<String>).clear()
                    for(snapshot in datasnapshot.children){
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUsers()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



    private fun getViews() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Story").child(id!!)
            .child(intent.getStringExtra("storyid"))
            .child("views")

        followersRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                (userList as ArrayList<User>).clear()
                for(snapshot in datasnapshot.children){
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                showUsers()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun showUsers() {
        val usersRef= FirebaseDatabase.getInstance().getReference().child("Users")
        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {

                (userList as ArrayList<User>).clear()
                for(snapshot in datasnapshot.children){
                    val user=snapshot.getValue(User::class.java)
                    for(id in idList!!){
                        if(user!!.getUID()!! ==id){
                            (userList as ArrayList<User>).add(user!!)
                        }
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}