package com.madokasoftwares.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.madokasoftwares.instagramclone.Model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {
    private var postid = ""
    private var publisherid=""
    private var firebaseUser:FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)


        val intent = intent   //receving our extras it must match from where it came from ie from PostAdapter
        postid=intent.getStringExtra("postid")
        publisherid=intent.getStringExtra("publisherid")

        firebaseUser= FirebaseAuth.getInstance().currentUser //get the currentuser the one commenting

        UserCommentInfo() //follow the xml fie to understand the viewHolder where it works  //retrieve the image of the one commenting


        post_comment.setOnClickListener(View.OnClickListener {  //post/publish our comment in the db
           if(add_comment!!.text.toString() == "")
          {
           Toast.makeText(this@CommentsActivity,"Please write a comment",Toast.LENGTH_LONG).show()
          }else{
               AddComment() //user adding comment
           }

        })
    }

    private fun AddComment() { //sroing comments in our comments collection
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

}