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
import com.madokasoftwares.instagramclone.Adapter.NotificationAdapter
import com.madokasoftwares.instagramclone.Model.Notification
import com.madokasoftwares.instagramclone.R
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationsFragment : Fragment() {
     // private var recyclerView: RecyclerView? = null
    private var notificationAdapter: NotificationAdapter?=null // our Adapter
    private var notificationList:List<Notification>?=null //our model class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_notifications, container, false)

        var recyclerView: RecyclerView
        recyclerView=view.findViewById(R.id.recycler_view_notifications)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager= LinearLayoutManager(context)

                  notificationList=ArrayList()

        notificationAdapter= NotificationAdapter(context,notificationList as ArrayList<Notification>)
        recyclerView.adapter=notificationAdapter

        readNotifications()
        return view
    }

    private fun readNotifications() {
        val NotificationRef= FirebaseDatabase.getInstance().reference
            .child("Notifications").child(FirebaseAuth.getInstance().currentUser!!.uid)

        NotificationRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(datasnapshot.exists()){
                    (notificationList as ArrayList<Notification>).clear()
                    for(snapshot in datasnapshot.children){
                        val notification=snapshot.getValue(Notification::class.java)
                        (notificationList as ArrayList<Notification>).add(notification!!)
                        Collections.reverse(notificationList) //dispkay the in reverse order frm the one comming recenlty
                        notificationAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}