package com.madokasoftwares.instagramclone.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.madokasoftwares.instagramclone.Adapter.PostAdapter
import com.madokasoftwares.instagramclone.Model.Post
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
    private var followingList : MutableList<Post>?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
     val view =inflater.inflate(R.layout.fragment_home, container, false)

        var recyclerView:RecyclerView? = null
        recyclerView = view.findViewById(R.id.recycler_view_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager=linearLayoutManager

        postList=ArrayList()
        postAdapter= context?.let{PostAdapter(it,postList as ArrayList<Post>)}
        recyclerView.adapter=postAdapter

        checkFollowings(){
            
        }


    return view
    }

    private fun checkFollowings(function: () -> Unit) {
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

                   for(id in (followingList as ArrayList<String>)){
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

}