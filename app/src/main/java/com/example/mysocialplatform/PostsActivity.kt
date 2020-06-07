package com.example.mysocialplatform

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mysocialplatform.models.Post
import com.example.mysocialplatform.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_posts.*

private const val TAG = "PostsActivity"
public const val EXTRA_USERNAME = "EXTRA_USERNAME"
open class PostsActivity : AppCompatActivity() {

    var signedInUser: User? = null
    lateinit var firestoreDB: FirebaseFirestore
    lateinit var posts: MutableList<Post>
    lateinit var adapter : PostsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)



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

        posts = mutableListOf()
        adapter = PostsAdapter(this,posts)

        rvPosts.adapter = adapter
        rvPosts.layoutManager = LinearLayoutManager(this)


        firestoreDB = FirebaseFirestore.getInstance()

        firestoreDB.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG,"Signed in as $signedInUser")
            }
            .addOnFailureListener{exception->
                Log.e(TAG,"Failure in Fetching Signed in User",exception)
            }

        var postsReference = firestoreDB
            .collection("posts")
            .limit(20)
            .orderBy("creation_time_ms",Query.Direction.DESCENDING)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        if(username != null){
            supportActionBar?.title = username
            postsReference = postsReference.whereEqualTo("user.name",username)
        }
        postsReference.addSnapshotListener { snapshot, exception ->
            if(exception != null || snapshot == null) {
                Log.e(TAG,"Exception while quering Posts",exception)
                return@addSnapshotListener
            }
            val postList = snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()
            for(post in postList){
                Log.i(TAG,"Post ${post} ")
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_home,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.aboutUs_menu){
            Log.i(TAG,"User Wants to Learn About you")
            startActivity(Intent(this,AboutUs::class.java))

        }
        return super.onOptionsItemSelected(item)
    }
}
