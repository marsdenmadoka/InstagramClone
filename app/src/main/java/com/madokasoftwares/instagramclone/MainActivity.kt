package com.madokasoftwares.instagramclone

import android.content.Intent
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
            //textView.setText("Home") //return@OnNavigationItemSelectedListener true
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
               // textView.setText("Add_Post")
                item.isChecked=false //do not turn the btn to blue
                startActivity(Intent (this@MainActivity,AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true

            }
            R.id.nav_notifications -> {
                moveToFragment(NotificationsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
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
        moveToFragment(HomeFragment())
    }

    private fun moveToFragment(fragment: Fragment){ //picks the selected fragment
        val fragmentTrans=supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.frame_container,fragment)
        fragmentTrans.commit()
    }

}