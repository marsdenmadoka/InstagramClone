package com.madokasoftwares.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.madokasoftwares.instagramclone.Adapter.StoryAdapter
import com.madokasoftwares.instagramclone.Model.Story
import com.madokasoftwares.instagramclone.Model.User
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.activity_display__story_.*

//activity to display our story
class Display_Story_Activity : AppCompatActivity(), StoriesProgressView.StoriesListener {

    var currentUserId:String=""
    var userId:String =""
    var counter =0


    var imagesList:List<String>?=null
    var storyIdsList:List<String>?=null

    var storiesProgressView:StoriesProgressView? =null
   var pressTime=0L
    var limit=500L

    private val onTouchListener=View.OnTouchListener { view, motionEvent ->
        when(motionEvent.action){
            MotionEvent.ACTION_DOWN ->
            {
  pressTime=System.currentTimeMillis()
                storiesProgressView!!.pause()
            }
            MotionEvent.ACTION_UP->{
              val now =System.currentTimeMillis()
                storiesProgressView!!.resume()
                return@OnTouchListener limit<now - pressTime
            }
        }
            false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display__story_)

        currentUserId=FirebaseAuth.getInstance().currentUser!!.uid
        userId=intent.getStringExtra("userId")//from
       // counter=intent.getStringExtra("userid")

        storiesProgressView=findViewById(R.id.stories_progress)

        //we want these to be visible to the owner only of the story
        layout_seen.visibility= View.GONE
        story_delete.visibility= View.GONE

        if(userId ==currentUserId){
            layout_seen.visibility= View.VISIBLE
            story_delete.visibility= View.VISIBLE
        }

        getStories(userId!!)
        UserInfo(userId!!)


        var reverse:View=findViewById(R.id.reverse)
        reverse.setOnClickListener { storiesProgressView!!.reverse()}
        reverse.setOnTouchListener(onTouchListener)


        var skip:View=findViewById(R.id.skip)
        skip.setOnClickListener { storiesProgressView!!.skip()}
        skip.setOnTouchListener(onTouchListener)

        seen_number.setOnClickListener{
            val intent =Intent(this@Display_Story_Activity,ShowUsersActivity::class.java)
            intent.putExtra("id",userId)
            intent.putExtra("storyid",storyIdsList!![counter])
            intent.putExtra("title","views")
            startActivity(intent)
        }

        story_delete.setOnClickListener {
            val ref=FirebaseDatabase.getInstance().reference
                .child("Story")
                .child(userId)
                .child(storyIdsList!![counter])

            ref.removeValue()
                .addOnCompleteListener {task ->
                    if(task.isSuccessful){
                        Toast.makeText(this@Display_Story_Activity,"Deleted",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }



    private fun addViewToStory(storyId: String){ //adding view to story
        FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!).child(storyId)
            .child("views")
            .child(currentUserId) //the one viewing someone story
            .setValue(true)
    }

    private fun seenNumber(storyId:String){ //see the nuber of views
        val ref=FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!).child(storyId)
            .child("views")

        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {

                seen_number.text=""+datasnapshot.childrenCount

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun getStories(userId: String){
         imagesList=ArrayList()
        storyIdsList=ArrayList()

      val ref=FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!)
        ref.addListenerForSingleValueEvent(object:ValueEventListener{

            override fun onDataChange(datasnapshot: DataSnapshot) {
                (imagesList as ArrayList<String>).clear()
                (storyIdsList as ArrayList<String>).clear()
                for(snapshot in datasnapshot.children){
                    val story: Story?=snapshot.getValue<Story>(Story::class.java)
                    val timeCurrent = System.currentTimeMillis()
                    if(timeCurrent>story!!.getTimeStart() && timeCurrent<story.getTimeEnd()){
                        (imagesList as ArrayList<String>).add(story.getImageUrl())
                        (storyIdsList as ArrayList<String>).add(story.getStoryId())

                    }

                }
                storiesProgressView!!.setStoriesCount((imagesList as ArrayList<String>).size)
                storiesProgressView!!.setStoryDuration(6000L)
                storiesProgressView!!.setStoriesListener(this@Display_Story_Activity)
                storiesProgressView!!.startStories(counter)
                Picasso.get().load(imagesList!!.get(counter)).placeholder(R.drawable.profile).into(image_story)

                addViewToStory(storyIdsList!!.get(counter))
                seenNumber(storyIdsList!!.get(counter))
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

    private fun UserInfo(userid:String){
        val usersRef= FirebaseDatabase.getInstance().getReference().child("Users").
        child(userId)
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user !!.getImage()).placeholder(R.drawable.profile).into(story_profile_image)
                    story_username.text=user.getUsername()


                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    override fun onNext() {
        Picasso.get().load(imagesList!![++counter]).placeholder(R.drawable.profile).into(image_story)
        addViewToStory(storyIdsList!![counter])
        seenNumber(storyIdsList!![counter])
    }

    override fun onPrev() { //once the story is seen by user we decrease teh index by one number
                            //e.g a user moves from story 4 to story 3
        if(counter-1 < 0) return
        Picasso.get().load(imagesList!![--counter]).placeholder(R.drawable.profile).into(image_story)
        seenNumber(storyIdsList!![counter])
    }
    override fun onComplete() {
        finish() //when the user finishes seeing the stories

    }


    override fun onDestroy() {

        super.onDestroy()
        storiesProgressView!!.destroy()
    }

    override fun onPause() {
        super.onPause()
        storiesProgressView!!.pause()

    }

    override fun onResume() {
        super.onResume()
     storiesProgressView!!.resume()
    }

}