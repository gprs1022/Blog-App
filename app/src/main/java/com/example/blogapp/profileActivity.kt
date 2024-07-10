package com.example.blogapp



import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.blogapp.databinding.ActivityProfileBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class profileActivity : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
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

        //go to add article activity
        binding.addArticleButton.setOnClickListener {
            startActivity(Intent(this, AddArticleActivity::class.java))
        }
  //to go to your Article activity
        binding.yourArticle.setOnClickListener {
            startActivity(Intent(this, yourArticleActivity::class.java))
        }


        // to logOut

        binding.logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, welcomeActivity::class.java))
            finish()
        }

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://blog-app-ab0e9-default-rtdb.firebaseio.com/").reference.child("users")
        val userId = auth.currentUser?.uid


        if (userId != null){

            loadUserProfileImage(userId)
        }



    }

    private fun loadUserProfileImage(userId: String) {
   val userReference = databaseReference.child(userId)

        //load user profile Image

        userReference.child("profileImage").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                if (profileImageUrl != null) {
                    Glide.with(this@profileActivity)
                        .load(profileImageUrl)
                        .into(binding.UserProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@profileActivity, "Failed to load user profile image", Toast.LENGTH_SHORT).show()
            }

        })

//load user name
        userReference.child("name").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.getValue(String::class.java)

                if (username != null) {
                    binding.userName.text = username
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}