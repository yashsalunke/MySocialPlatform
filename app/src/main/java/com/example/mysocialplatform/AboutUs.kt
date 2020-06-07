package com.example.mysocialplatform

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class AboutUs :PostsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        val bottomNavigation : BottomNavigationView = findViewById(R.id.btm_nav)



        bottomNavigation.setOnNavigationItemSelectedListener {item ->
            when(item.itemId){
                R.id.home -> {
                    val intent = Intent(this,PostsActivity::class.java)
                    startActivity(intent)
                }

                R.id.addPost -> {
                    val intent = Intent(this,CreatePost::class.java)
                    startActivity(intent)
                }

                R.id.profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra(EXTRA_USERNAME,signedInUser?.name)
                    startActivity(intent)
                }

            }
            true
        }

    }
}