package com.madokasoftwares.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.madokasoftwares.instagramclone.Adapter.CommentAdapter
import com.madokasoftwares.instagramclone.Model.Comment
import com.madokasoftwares.instagramclone.Model.Post
import com.madokasoftwares.instagramclone.Model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {
    private var postid = ""
    private var publisherid=""
    private var firebaseUser:FirebaseUser? = null
    private var commentAdapter: CommentAdapter?=null
    private var commentList:MutableList<Comment>?=null //our model class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)


        val intent = intent   //receving our extras it must match from where it came from ie from PostAdapter
        postid=intent.getStringExtra("postid")
        publisherid=intent.getStringExtra("publisherid")

        firebaseUser= FirebaseAuth.getInstance().currentUser //get the currentuser the one commenting

        //our recycleview to displaying our comments
        var recyclerView:RecyclerView
        recyclerView = findViewById(R.id.recycle_view_comments)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout=true
        recyclerView.layoutManager=linearLayoutManager

        commentList=ArrayList()
        commentAdapter= CommentAdapter(this,commentList)
        recyclerView.adapter = commentAdapter



        UserCommentInfo() //follow the xml fie to understand the viewHolder where it works  //retrieve the image of the one commenting
        readComments() //displaying our comments
        UserPostImage()//show the user post imae

        post_comment.setOnClickListener(View.OnClickListener {  //post/publish our comment in the db
           if(add_comment!!.text.toString() == "")
          {
           Toast.makeText(this@CommentsActivity,"Please write a comment",Toast.LENGTH_LONG).show()
          }else{
               AddComment() //user adding comment
           }

        })
    }



    private fun AddComment() { //stoing comments in our comments collection
        val CommentsRef= FirebaseDatabase.getInstance().reference
            .child("Comments").child(postid)
        val commentsMap = HashMap<String,Any>()
        commentsMap["comment"] = add_comment.text.toString() //form our comments textview
        commentsMap["publisher"]=firebaseUser!!.uid

        CommentsRef.push().setValue(commentsMap)

        add_comment!!.text.clear()//clear the textview so that the user can add another comment if they wish

    }


    private fun UserCommentInfo(){ //display to account activity.xml
        val usersRef= FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java) //User here is our model class
                    Picasso.get().load(user !!.getImage()).placeholder(R.drawable.profile).into(profile_image_comments)

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



    private fun UserPostImage(){ //display to account activity.xml
        val postRef= FirebaseDatabase.getInstance()
            .reference.child("Posts").child(postid).child("postimage")

        postRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val image = snapshot.value.toString()
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(post_image_comments)

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun readComments(){ //read our commets
        val commentsRef=FirebaseDatabase.getInstance().reference
            .child("Comments").child(postid)

        commentsRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
               if(datasnapshot.exists()){
                   commentList!!.clear()

                   for(snapshot in datasnapshot.children)
                   {
                       val comment = snapshot.getValue(Comment::class.java) //comment is our model class
                       commentList!!.add(comment!!)
                   }
                commentAdapter!!.notifyDataSetChanged()
               }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}