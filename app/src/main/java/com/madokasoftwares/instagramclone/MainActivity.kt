package com.madokasoftwares.instagramclone

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.madokasoftwares.instagramclone.Fragments.HomeFragment
import com.madokasoftwares.instagramclone.Fragments.NotificationsFragment
import com.madokasoftwares.instagramclone.Fragments.ProfileFragment
import com.madokasoftwares.instagramclone.Fragments.SearchFragment


class MainActivity : AppCompatActivity() {
internal var selectedFragment: Fragment? = null

//private lateinit var textView: TextView

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
            //textView.setText("Home")
                selectedFragment=HomeFragment()
            }
            R.id.nav_search -> {
      //textView.setText("Search") //return@OnNavigationItemSelectedListener true
                selectedFragment=SearchFragment()
            }
            R.id.nav_add_post -> {
               // textView.setText("Add_Post")
                return@OnNavigationItemSelectedListener true

            }
            R.id.nav_notifications -> {
      //textView.setText("Notifications") //return@OnNavigationItemSelectedListener true
                selectedFragment=NotificationsFragment()
            }
            R.id.nav_profile -> {
           //  textView.setText("Pprofile") //return@OnNavigationItemSelectedListener true
                selectedFragment=ProfileFragment()
            }
        }

        if(selectedFragment !=null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.frame_container,
                selectedFragment!! //picks the selected fragment
            ).commit()
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
//        textView = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

//setting our home fragment as the default view when we open the app
        supportFragmentManager.beginTransaction().replace(
            R.id.frame_container, //from our mainActivity.xml
            HomeFragment()
        ).commit()

    }
}