package com.madokasoftwares.instagramclone.Fragments

import android.os.Bundle
import android.os.UserManager
import android.text.Editable
import android.text.TextWatcher
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
import com.google.firebase.database.ktx.FirebaseDatabaseKtxRegistrar
import com.google.firebase.ktx.Firebase
import com.madokasoftwares.instagramclone.Adapter.UserAdapter
import com.madokasoftwares.instagramclone.Model.User
import com.madokasoftwares.instagramclone.R
import kotlinx.android.synthetic.main.fragment_search.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
//this is our search fragment
    private var recyclerView:RecyclerView? = null
    private var userAdapter:UserAdapter?=null //UserAdapter is our Adapter
    private var mUser:MutableList<User>?=null //User here is our  Model class called User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView=view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager=LinearLayoutManager(context)

        //here we connect our recycleView with the UserAdapter ie our user_item_layout.xml to our recycleView in the fragment_search.xml
        mUser=ArrayList()
        userAdapter=context?.let{ UserAdapter(it,mUser as ArrayList<User>,true) }
        recyclerView?.adapter= userAdapter

        //making our edit text respond to textwatcher
        view.search_edit_text.addTextChangedListener(object: TextWatcher //making our search edit text to listen to text changer i.e every letter/TextWatcher
        {

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

            //listen on text change
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if(view.search_edit_text.text.toString()== "") {
                //do nothing if not text change
            }else{

                recyclerView?.visibility=View.VISIBLE //make our recyclerview visible

                retrieveUsers() //run the function
                searchUser(s.toString().toLowerCase()) //run the function to search in small letters even if you write capital letter thy will be converted into small letters inside the db search
            }
            }
        })

        return view
    }


    private fun retrieveUsers() { //retrive the users from db
        val usersRef= FirebaseDatabase.getInstance().getReference().child("Users")
        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {

                if(view?.search_edit_text?.text.toString()== ""){
                    mUser?.clear()
                    for(snapshot in datasnapshot.children){
                        val user=snapshot.getValue(User::class.java)
                        if(user != null){
                            mUser?.add(user)
                        }
                    }
                    userAdapter?.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun searchUser(input: String) { //search for the user
        val query= FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .orderByChild("fullname") //search by fullname
            .startAt(input)  //start at the first letter
            .endAt(input + "\uf8ff") //end at the last letter

        query.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {
                mUser?.clear()

                for(snapshot in datasnapshot.children){
                    val user=snapshot.getValue(User::class.java)
                    if(user != null){
                        mUser?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })


    }


}