package com.madokasoftwares.instagramclone.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.madokasoftwares.instagramclone.Adapter.PostAdapter
import com.madokasoftwares.instagramclone.Model.Post
import com.madokasoftwares.instagramclone.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GalleryImageDetailsFragment : Fragment() {

    //in this fragment we get the details of our image when we clicked it in the gallery at profilefragment
    //het the details of any image when clicked the gallery at profile fragment
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>?=null
    private var postid:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_post_details, container, false)
         val preferences = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)
        if(preferences!=null){
            postid = preferences.getString("postId","none").toString()
        }

        var recyclerView:RecyclerView
        recyclerView=view.findViewById(R.id.recycler_view_postdetails)
        val linearLayoutManager=LinearLayoutManager(context)
        recyclerView.layoutManager=linearLayoutManager

        postList=ArrayList()
        postAdapter= context?.let{PostAdapter(it,postList as ArrayList<Post>)}
        recyclerView.adapter=postAdapter

        retrievePost()
      return view
    }

    private fun retrievePost(){ //f
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")
            .child(postid)


        postsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                postList?.clear()
                val post=datasnapshot.getValue(Post::class.java)
                postList!!.add(post!!)
                postAdapter!!.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}