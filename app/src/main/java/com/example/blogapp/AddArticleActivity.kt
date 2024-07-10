package com.example.blogapp


import android.os.Bundle

import android.widget.Toast
import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blogapp.databinding.ActivityAddArticleBinding
import com.example.blogapp.model.BlogItemModel
import com.example.blogapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class AddArticleActivity : AppCompatActivity() {

    private val binding: ActivityAddArticleBinding by lazy {
        ActivityAddArticleBinding.inflate(layoutInflater)
    }


    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blog-app-ab0e9-default-rtdb.firebaseio.com/")
            .getReference("blogs")
    private val userReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blog-app-ab0e9-default-rtdb.firebaseio.com/")
            .getReference("users")
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.addArticle.setOnClickListener {
            val title = binding.blogTitle.text.toString().trim()
            val description = binding.blogDescription.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {

                Toast.makeText(this, "Please Fill all the fields", Toast.LENGTH_SHORT).show()
            }

            // get current user
            val user: FirebaseUser? = auth.currentUser


            if (user != null) {
                val userId = user.uid
                val userName = user.displayName ?: "Annonymous"
                val userImageUrl = user.photoUrl ?: ""

                //fetch user name and user profile from database
                userReference.child(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userData = snapshot.getValue(UserData::class.java)
                            if (userData != null) {

                                val userNameFromDB = userData.name

                                val userImageUrlFromDb = userData.profileImage

                                val currentDate = SimpleDateFormat("dd-MM-yyyy").format(Date())


                                //create blogItem

                                val blogItem = BlogItemModel(
                                    date = currentDate,
                                    heading = title,
                                    likeCount = 0,
                                    post = description,
                                    profileImage = userImageUrlFromDb,
                                    userId = userId,
                                    userName = userNameFromDB


                                    )


                                //generate unique key for blog item
                                val key = databaseReference.push().key

                                if (key != null) {

                                    blogItem.postId = key

                                    val blogReference = databaseReference.child(key)
                                    blogReference.setValue(blogItem)
                                        .addOnCompleteListener {

                                            if (it.isSuccessful) {
                                                finish()
                                            } else {

                                                Toast.makeText(
                                                    this@AddArticleActivity,
                                                    "Failed to add article",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        }
                                }
                            }


                        }

                        override fun onCancelled(error: DatabaseError) {

                        }


                    })
            }
        }
    }
}