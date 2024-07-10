package com.example.blogapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.adaptor.BlogAdapter
import com.example.blogapp.databinding.ActivityAddArticleBinding
import com.example.blogapp.databinding.ActivitySavedArticleBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SavedArticleActivity : AppCompatActivity() {

    private val binding: ActivitySavedArticleBinding by lazy {
        ActivitySavedArticleBinding.inflate(layoutInflater)
    }

    private val savedBlosArtical = mutableListOf<BlogItemModel>()
    private lateinit var blogAdapter: BlogAdapter
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

        // Initialize blogAdaptor

        blogAdapter = BlogAdapter(savedBlosArtical.filter { it.isSaved }. toMutableList())

        val recylerView = binding.savedArticalRecyclerView
        recylerView.adapter = blogAdapter
        recylerView.layoutManager =  LinearLayoutManager(this)

        val userId = auth.currentUser?.uid

        if (userId != null){

            val userReference = FirebaseDatabase.getInstance("https://blog-app-ab0e9-default-rtdb.firebaseio.com/")
                .getReference("users").child(userId).child("saveBlogPosts")

            userReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for(postSnapshot in snapshot.children) {
                        val postId = postSnapshot.key
                        val isSaved = postSnapshot.value as Boolean
                        // fetch the corresponding blog item on postId using a coroutine scope
                        if(postId != null && isSaved)
                        {
                            CoroutineScope(Dispatchers.IO).launch {
                                val blogItem = fetchBlogItem(postId)
                                if (blogItem != null) {
                                    savedBlosArtical.add(blogItem)
                                }

                                launch(Dispatchers.Main){
                                    blogAdapter.updateData(savedBlosArtical)
                                }

                            }


                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
        }

        binding.backButton.setOnClickListener {
            finish()
        }


    }

    private  suspend fun fetchBlogItem(postId: String): BlogItemModel? {
       val blogReference = FirebaseDatabase.getInstance("https://blog-app-ab0e9-default-rtdb.firebaseio.com/")
           .getReference("blogs")

        return try {
            val dataSanpshot = blogReference.child(postId).get().await()
            val blogItem = dataSanpshot.getValue(BlogItemModel::class.java)
            blogItem
        } catch (e: Exception) {
            null
        }
    }
}