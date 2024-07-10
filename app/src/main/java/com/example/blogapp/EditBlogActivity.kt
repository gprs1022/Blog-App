package com.example.blogapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blogapp.databinding.ActivityAddArticleBinding
import com.example.blogapp.databinding.ActivityEditBlogBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.database.FirebaseDatabase

class EditBlogActivity : AppCompatActivity() {

    private val binding: ActivityEditBlogBinding by lazy {
        ActivityEditBlogBinding.inflate(layoutInflater)
    }
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

        val blogItemModel = intent.getParcelableExtra<BlogItemModel>("blogItem")?.copy()

        binding.blogTitle.setText(blogItemModel?.heading)
        binding.blogDescription.setText(blogItemModel?.post)

        binding.SaveArticle.setOnClickListener {
            val updatedTitle = binding.blogTitle.text.toString().trim()
            val updatedDescription = binding.blogDescription.text.toString().trim()

            if(updatedTitle.isEmpty() || updatedDescription.isEmpty()){
                Toast.makeText(this, "Please Fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                blogItemModel?.apply {
                    heading = updatedTitle
                    post = updatedDescription
                }

                blogItemModel?.let { updateDataInFirebase(it) }
            }
        }


    }

    private fun updateDataInFirebase(blogItemModel: BlogItemModel) {
        val databaseReference = FirebaseDatabase.getInstance("https://blog-app-ab0e9-default-rtdb.firebaseio.com/").getReference("blogs")
        val postId = blogItemModel.postId

        databaseReference.child(postId.toString()).setValue(blogItemModel)
            .addOnSuccessListener {
                Toast.makeText(this, "Blog Updated", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update blog", Toast.LENGTH_SHORT).show()
            }
    }

}