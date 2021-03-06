package com.madokasoftwares.instagramclone.Fragments

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.madokasoftwares.instagramclone.Adapter.PostAdapter
import com.madokasoftwares.instagramclone.Adapter.StoryAdapter
import com.madokasoftwares.instagramclone.Model.Post
import com.madokasoftwares.instagramclone.Model.Story
import com.madokasoftwares.instagramclone.R

// TODO: Rename parameter arguments, choose names that match

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
//we will be dispalying all our posts in this fragment
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>?=null
    private var followingList : MutableList<String>?=null

    //for displaying our story
    private var storyAdapter:StoryAdapter?=null
    private var storyList:MutableList<Story>?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // NetworkX.startObserving(this, NetworkXObservingStrategy.HIGH)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val cm=context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        //val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        // Inflate the layout for this fragment
     val view =inflater.inflate(R.layout.fragment_home, container, false)
        var recyclerViewStory:RecyclerView? = null
        var recyclerView:RecyclerView? = null

        //for our posts
        recyclerView = view.findViewById(R.id.recycler_view_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager=linearLayoutManager



        //story recycler view

        recyclerViewStory = view.findViewById(R.id.recycler_view_Story)
        recyclerViewStory.setHasFixedSize(true)
        val linearLayoutManager2 = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recyclerViewStory.layoutManager=linearLayoutManager2

        val progressdialog= ProgressDialog(getActivity())
        //check the internet
        if(activeNetwork == null){

            progressdialog.setMessage("no internet please try again. please wait retring...")
            progressdialog.show()
            Toast.makeText(getActivity(),"no internet",Toast.LENGTH_LONG).show();
        }


            postList=ArrayList()
            postAdapter= context?.let{PostAdapter(it,postList as ArrayList<Post>)}
            recyclerView.adapter=postAdapter


            storyList=ArrayList()
            storyAdapter= context?.let{StoryAdapter(it,storyList as ArrayList<Story>)}
            recyclerViewStory.adapter=storyAdapter
            //end
            checkFollowings()
         // progressdialog.dismiss()


        return view
    }

    private fun checkFollowings() { //we want to display our post and stories based on who your following
      followingList = ArrayList()
        val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following")
    followingRef.addValueEventListener(object: ValueEventListener{
    override fun onDataChange(datasnapshot: DataSnapshot) {
if(datasnapshot.exists()){
    (followingList as ArrayList<String>).clear()
    for(snapshot in datasnapshot.children)
    {
        snapshot.key?.let{(followingList as ArrayList<String> ).add(it)}
    }

    retrieveAllPost()
    retrieveStories()
}
    }

    override fun onCancelled(error: DatabaseError) {

    }
})
    }

    private fun retrieveAllPost(){ //fetching all posts form our database
    val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
       postsRef.addValueEventListener(object:ValueEventListener{
           override fun onDataChange(datasnapshot: DataSnapshot) {
               postList?.clear()
               for(snapshot in datasnapshot.children){
              val post=snapshot.getValue(Post::class.java)

                   for(id in (followingList as ArrayList<String>)){ //we will display post for those your followi
                       if(post!!.getPublisher()== id)
                       {
                          postList!!.add(post)
                       }
                       postAdapter!!.notifyDataSetChanged()
                   }
               }
           }

           override fun onCancelled(error: DatabaseError) {

           }
       })
}

    private fun retrieveStories() {

        val storyRef = FirebaseDatabase.getInstance().reference.child("Story")
        storyRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {

                var timeCurrent=System.currentTimeMillis() //get current time

                (storyList as ArrayList<Story>).clear()
                (storyList as ArrayList<Story>).add(Story("",0,0,"",FirebaseAuth.getInstance().currentUser!!.uid)) //add the story with these values in our storylist found in our model class


          for(id in followingList!!){ //we will display story for those your following
              var countyStory = 0 //intial value
              var story:Story?=null

              for(snapshot in datasnapshot.child(id).children){
                  story=snapshot.getValue(Story::class.java)

                  //we want to show only to apear only in 24hours
                  if(timeCurrent>story!!.getTimeStart() && timeCurrent<story!!.getTimeEnd()){
                      countyStory++ //count the stories
                  }
              }

              if(countyStory>0){
                  (storyList as ArrayList<Story>).add(story!!) //add story to the storyList
              }
      storyAdapter!!.notifyDataSetChanged()
          }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }


}