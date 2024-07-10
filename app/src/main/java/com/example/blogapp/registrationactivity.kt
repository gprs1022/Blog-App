package com.example.blogapp


import android.content.Intent
import android.net.Uri
import android.os.Bundle

import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.databinding.ActivityRegistrationactivityBinding

import com.example.blogapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class registrationactivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseStorage: FirebaseStorage
    private var ImageUri: Uri? = null
    private val IMAGE_PICK_REQUEST = 1

    private val binding: ActivityRegistrationactivityBinding by lazy {
        ActivityRegistrationactivityBinding.inflate(layoutInflater)
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
        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance()
        // Initialize Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance()

        // Set up select image button click listener
        binding.profileCardView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "select Image"), IMAGE_PICK_REQUEST)

        }

        binding.BtnRegister.setOnClickListener {

            val registerEmail = binding.editTextEmail.text.toString().trim()
            val registerPassword = binding.editTextPassword.text.toString().trim()
            val registerName = binding.editTextName.text.toString().trim()

            // Check if email and password fields are not empty
            if (registerEmail.isEmpty() || registerPassword.isEmpty() || registerName.isEmpty() || ImageUri == null) {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            } else {

                // Create user with email and password
                auth.createUserWithEmailAndPassword(registerEmail, registerPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Registration successful
                            val user = auth.currentUser


                            user?.let {
                                val userReference = firebaseDatabase.getReference("users")
                                val userId = user.uid
                                val userData = UserData(registerName, registerEmail)


                                userReference.child(userId).setValue(userData)

                                //uplode image to firebase storage
                                val storageReference =
                                    firebaseStorage.reference.child("profile_images/$userId.jpg")
                                Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()

                                storageReference.putFile(ImageUri!!)
                                    .addOnCompleteListener { task ->

                                        if (task.isSuccessful) {
                                            storageReference.downloadUrl.addOnCompleteListener { imageUri ->

                                                val imageUrl = imageUri.result.toString()

                                                userReference.child(userId).child("profileImage")
                                                    .setValue(imageUrl)

                                            }
                                        }
                                    }


                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT)
                                    .show()


                            }

                            startActivity(Intent(this, signinActivity::class.java))
                            finish()
                            // You can add further actions here like redirecting the user to another activity
                        } else {

                            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                            // Registration failed

                        }
                    }

            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            ImageUri = data.data
            Glide.with(this).load(ImageUri).apply(RequestOptions.circleCropTransform())
                .into(binding.ProfileImageView)


        }
    }
}



