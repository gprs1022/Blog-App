package com.example.blogapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blogapp.databinding.ActivitySigninBinding


import com.google.firebase.auth.FirebaseAuth


class signinActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val binding: ActivitySigninBinding by lazy {
        ActivitySigninBinding.inflate(layoutInflater)
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


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()


        // Set up login button click listener
        binding.login.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            // Check if email and password fields are not empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sign in user with email and password
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {


                        // Sign in successful
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        val Intent = Intent(this, MainActivity::class.java)
                        startActivity(Intent)
                        // Redirect the user to another activity or perform other actions
                    } else {
                        // Sign in failed
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

            binding.signUpRegistation.setOnClickListener {
                val Intent = Intent(this, registrationactivity::class.java)
                startActivity(Intent)
            }


        }


    }
}