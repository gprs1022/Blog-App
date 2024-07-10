package com.example.blogapp


import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blogapp.adaptor.BlogAdapter

import com.example.blogapp.databinding.ActivityMainBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    private lateinit var databaseReference: DatabaseReference
    private val blogItems = mutableListOf<BlogItemModel>()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        // to go profile activity

        binding.profleImage.setOnClickListener {
            startActivity(Intent(this, profileActivity::class.java))

        }

        //to go save Artical button

        binding.saveArticalButton.setOnClickListener {
            startActivity(Intent(this, SavedArticleActivity::class.java))

        }

        auth = FirebaseAuth.getInstance()


        databaseReference =
            FirebaseDatabase.getInstance("https://blog-app-ab0e9-default-rtdb.firebaseio.com/").reference.child(
                "blogs"
            )
        val userId = auth.currentUser?.uid


        /// set user profile

        if (userId != null) {
            loadUserProfileImage(userId)
        }


        // set blog post into Recycler view


        // Intilize  the recycler view and set adapter

        val recylerView = binding.blogRecyclerView
        val blogAdapter = BlogAdapter(blogItems)
        recylerView.adapter = blogAdapter
        recylerView.layoutManager = LinearLayoutManager(this)


        //fetch data from firebase databse

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
              blogItems.clear()

                for (snapshot in snapshot.children) {
                    val blogItem = snapshot.getValue(BlogItemModel::class.java)
                    if (blogItem != null) {

                        blogItems.add(blogItem)

                    }

                }
               //reverse the list

                blogItems.reverse()

                //notify the adapter that data has changed

                blogAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }


        })

        binding.floatingArticleButton.imageTintList = ColorStateList.valueOf(Color.WHITE)
        binding.floatingArticleButton.setOnClickListener {
            startActivity(Intent(this, AddArticleActivity::class.java))
        }


    }

    private fun loadUserProfileImage(userId: String) {


        val userReference =
            FirebaseDatabase.getInstance("https://blog-app-ab0e9-default-rtdb.firebaseio.com/").reference.child(
                "users"
            ).child(userId)

        userReference.child("profileImage").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)


                if (profileImageUrl != null) {
                    Glide.with(this@MainActivity)
                        .load(profileImageUrl)
                        .into(binding.profleImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Failed to load user profile image",
                    Toast.LENGTH_SHORT
                ).show()
            }


        })

    }
}