package com.example.blogapp

import ArticleAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.databinding.ActivityProfileBinding

import com.example.blogapp.databinding.ActivityYourArticleBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class yourArticleActivity : AppCompatActivity() {

    private val binding: ActivityYourArticleBinding by lazy {
        ActivityYourArticleBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var blogAdapter: ArticleAdapter
    private val auth = FirebaseAuth.getInstance()
    private val EDIT_BLOG_REQUEST_CODE = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val currentUser = auth.currentUser?.uid

        binding.articleRecylerView.layoutManager = LinearLayoutManager(this)


       if(currentUser != null){
           blogAdapter = ArticleAdapter(this, emptyList(), object : ArticleAdapter.OnItemClickListener {
               override fun onEditClick(blogItem: BlogItemModel) {
                   // Handle edit click if needed
                   val intent = Intent(this@yourArticleActivity,EditBlogActivity::class.java)
                   intent.putExtra("blogItem", blogItem)
                   startActivityForResult(intent,EDIT_BLOG_REQUEST_CODE)

               }

               override fun onReadMoreClick(blogItem: BlogItemModel) {
                   // Handle read more click if needed

                   val intent = Intent(this@yourArticleActivity, ReadMoreActivity::class.java)
                   intent.putExtra("blogItem", blogItem)
                   startActivity(intent)
               }

               override fun onDeleteClick(blogItem: BlogItemModel) {
                   // Handle delete click if needed
                   deleteBlogPost(blogItem)

               }
           })
       }

        binding.articleRecylerView.adapter = blogAdapter // Ensure adapter is set here

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance("https://blog-app-ab0e9-default-rtdb.firebaseio.com/").getReference("blogs")

        // Retrieve data from Firebase Database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val blogSaveList = ArrayList<BlogItemModel>()
                for (postSnapshot in snapshot.children) {
                    val blogSaved = postSnapshot.getValue(BlogItemModel::class.java)
                    if (blogSaved != null && currentUser == blogSaved.userId) {
                        blogSaveList.add(blogSaved)
                    }
                }
                blogAdapter.setData(blogSaveList) // Ensure setData is called after blogAdapter is initialized
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@yourArticleActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun deleteBlogPost(blogItem: BlogItemModel) {

        var postId = blogItem.postId

        var blogPostReference = databaseReference.child(postId.toString())

        blogPostReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Blog Deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to delete blog", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == EDIT_BLOG_REQUEST_CODE && resultCode == Activity.RESULT_OK){

        }
    }
}