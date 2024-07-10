package com.example.blogapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.blogapp.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth


class welcomeActivity : AppCompatActivity() {



    private val binding: ActivityWelcomeBinding by lazy {
        ActivityWelcomeBinding.inflate(layoutInflater)
    }

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


        auth = FirebaseAuth.getInstance()
        binding.loginButton.setOnClickListener {
            val Intent = Intent(this, signinActivity::class.java)
            startActivity(Intent)
            finish()
        }
        binding.RegisterButton.setOnClickListener {
            val Intent = Intent(this, registrationactivity::class.java)
            startActivity(Intent)
            finish()
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already logged in, proceed to the main app content
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Optional: Close the login activity
        } else {
            // User is not logged in, show the login screen
        }
    }

//    override fun onStart() {
//
//        super.onStart()
//        val currentUser = auth.currentUser
//
//        if(currentUser == null){
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
    }
