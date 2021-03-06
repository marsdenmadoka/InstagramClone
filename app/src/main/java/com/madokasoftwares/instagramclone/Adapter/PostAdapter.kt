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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.madokasoftwares.instagramclone.CommentsActivity
import com.madokasoftwares.instagramclone.MainActivity
import com.madokasoftwares.instagramclone.Model.Post
import com.madokasoftwares.instagramclone.Model.User
import com.madokasoftwares.instagramclone.R
import com.madokasoftwares.instagramclone.ShowUsersActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

//n/b POSTS WILL BE DISPLAYED BASED ON THE PERSONS YOOU FOLLOW
class PostAdapter(private val mContext: Context,
                  private val mPost:List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() //Post is our Adapter
{
    private var firebaseUser:FirebaseUser? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(mContext).inflate(R.layout.display_post_home_layout,parent,false)
     return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      firebaseUser= FirebaseAuth.getInstance().currentUser

        val post=mPost[position]
        Picasso.get().load(post.getPostimage()).into(holder.postImage) //dislpay the post image
          holder.description.setText(post.getDescription())//dislap the post description    //post is our Modelclass                                                          //display the post discription


        publisherInfo(holder.profileImage,holder.userName,holder.publisher,post.getPublisher()) //refer this to our method PublisherInfo
        NumberOfLikes(holder.likes,post.getPostid())//get total no of likes
        NumberOfComments(holder.comments,post.getPostid()) //get total no comments
        isLikes(post.getPostid(),holder.likeButton) //method to change button colors for like button
        checkImageSavedStatus(post.getPostid(),holder.saveButton) //check the image save status if its save or not
        
    holder.likeButton.setOnClickListener {
        //liking the post
        if(holder.likeButton.tag == "Like"){
    FirebaseDatabase.getInstance().reference.child("Likes") //the person likes the post
        .child(post.getPostid())
        .child(firebaseUser!!.uid)
        .setValue(true)
            AddNotification(post.getPublisher(),post.getPostid()) //wen one like we want to send a notification that so and so liked your pic
        }else{
            //unliking the post
            FirebaseDatabase.getInstance().reference.child("Likes") //remove like from the database
                .child(post.getPostid())
                .child(firebaseUser!!.uid)
                .removeValue()
            val intent = Intent(mContext,MainActivity::class.java)
             mContext.startActivity(intent)
        }

    }

         holder.likes.setOnClickListener {//when we click this we see the names of those who liked our posts
            val intent = Intent(mContext, ShowUsersActivity::class.java)
            intent.putExtra("id",post.getPostid()) //we added myprofileid so that we can get any person
            intent.putExtra("title","likes")
            mContext.startActivity(intent)
        }

        holder.commentButton.setOnClickListener {//when we want to write comments
            val intentComment = Intent(mContext,CommentsActivity::class.java)
            intentComment.putExtra("postid",post.getPostid())
            intentComment.putExtra("publisherid",post.getPublisher())
            mContext.startActivity(intentComment)
        }
       //when we click the view All comments
        holder.comments.setOnClickListener {
            val intentComment = Intent(mContext,CommentsActivity::class.java)
            intentComment.putExtra("postid",post.getPostid())
            intentComment.putExtra("publisherid",post.getPublisher())
            mContext.startActivity(intentComment)
        }
       //save our pic
        holder.saveButton.setOnClickListener {
            if(holder.saveButton.tag =="Save"){ //refer the Save to  checkImageSavedStatus() Method
                FirebaseDatabase.getInstance().reference.child("Saves") //save the image
                    .child(firebaseUser!!.uid).child(post.getPostid())
                    .setValue(true)
                Toast.makeText(mContext,"Image Saved!",Toast.LENGTH_SHORT).show();

            }else{
                FirebaseDatabase.getInstance().reference.child("Saves")
                    .child(firebaseUser!!.uid).child(post.getPostid())
                    .removeValue()
            }
        }
    }

    private fun isLikes(postid: String, likeButton: ImageView) // setting the correct image resource when one like and unlikes the image
    {
val firebaseUser = FirebaseAuth.getInstance().currentUser
        val LikesRef=FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)


         LikesRef.addValueEventListener(object:ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 if(snapshot.child(firebaseUser!!.uid).exists()){
                     likeButton.setImageResource(R.drawable.heart_clicked) //set the red iconimage resource when one clicks the like image btn
                     likeButton.tag="Liked" //set tag
                 }else{
            likeButton.setImageResource(R.drawable.heart_not_clicked)
                     likeButton.tag="Like" //
                 }
             }

             override fun onCancelled(error: DatabaseError) {

             }
         })

    }

    private fun NumberOfLikes(likes: TextView, postid: String)
    {
        val LikesRef=FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)
        LikesRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                   likes.text=snapshot.childrenCount.toString() + " likes"//getting total number of likes and setting it in the likesText view

                }else{

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun NumberOfComments(comments: TextView, postid: String)
    {
        val CommentsRef=FirebaseDatabase.getInstance().reference
            .child("Comments").child(postid)
        CommentsRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    comments.text="view all"+ snapshot.childrenCount.toString() + " comments"//getting total number of likes and setting it in the likesText view

                }else{

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImage:CircleImageView
        var postImage:ImageView
        var likeButton:ImageView
        var commentButton:ImageView
        var saveButton:ImageView
        var likes:TextView
        var publisher:TextView
        var userName:TextView
        var description:TextView
        var comments:TextView

        init {
            profileImage=itemView.findViewById(R.id.user_profile_image_post)
            postImage=itemView.findViewById(R.id.post_image_home)
            likeButton=itemView.findViewById(R.id.post_image_like_btn)
            commentButton=itemView.findViewById(R.id.post_image_comment_btn)
            saveButton=itemView.findViewById(R.id.post_save_comment_btn)
            userName=itemView.findViewById(R.id.user_name_post)
            likes=itemView.findViewById(R.id.likes)
            publisher=itemView.findViewById(R.id.publisher)
            description=itemView.findViewById(R.id.description)
            comments=itemView.findViewById(R.id.comments)
        }
    }


    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String) { //we will display this on top of our cardview on above of our post/picture
        var usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
       usersRef.addValueEventListener(object : ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.exists()){

                   val user = snapshot.getValue<User>(User::class.java) //User here is our model class
                   Picasso.get().load(user !!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                   userName.setText(user.getUsername())
                   publisher.setText(user.getFullname())

               }
           }
           override fun onCancelled(error: DatabaseError) {
           }
       })
    }

  private fun  checkImageSavedStatus(postid:String,imageView: ImageView){
      val savesRef =  FirebaseDatabase.getInstance().reference
          .child("Saves")
          .child(firebaseUser!!.uid)
savesRef.addValueEventListener(object:ValueEventListener{
    override fun onDataChange(snapshot: DataSnapshot) {
        //if image is saved
        if(snapshot.child(postid).exists()){
            imageView.setImageResource(R.drawable.save_large_icon)
            imageView.tag="Saved"
        }else{ //if the image is not saved
            imageView.setImageResource(R.drawable.save_unfilled_large_icon)
            imageView.tag="Save"
        }
    }

    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }
})
  }


    private fun AddNotification(userId:String,postId: String){
        val NotificationRef=FirebaseDatabase.getInstance().reference
            .child("Notifications").child(userId)
        val notiMap=HashMap<String,Any>()
        notiMap["userid"] = firebaseUser!!.uid //the online person who is going to like my post
        notiMap["text"]="liked your post"
        notiMap["postid"]=postId
        notiMap["ispost"]=true

        NotificationRef.push().setValue(notiMap)

    }
}