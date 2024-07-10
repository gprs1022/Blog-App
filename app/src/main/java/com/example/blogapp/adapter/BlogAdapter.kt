package com.example.blogapp.adaptor

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.R
import com.example.blogapp.ReadMoreActivity
import com.example.blogapp.databinding.BlogItemBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BlogAdapter(private val items: MutableList<BlogItemModel>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {


    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blog-app-ab0e9-default-rtdb.firebaseio.com/").reference

    private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BlogItemBinding.inflate(inflater, parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = items[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class BlogViewHolder(private val binding: BlogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItemModel: BlogItemModel) {
            val postId = blogItemModel.postId
            val context = binding.root.context
            binding.heading.text = blogItemModel.heading
            Glide.with(binding.profile.context)
                .load(blogItemModel.profileImage)
                .into(binding.profile)
            binding.userName.text = blogItemModel.userName
            binding.date.text = blogItemModel.date
            binding.post.text = blogItemModel.post
            binding.likeCount.text = blogItemModel.likeCount.toString()


            //setOn clicklistener


            binding.root.setOnClickListener {

                val context = binding.root.context
                val intent = Intent(context, ReadMoreActivity::class.java)

                intent.putExtra("blogItem", blogItemModel)

                context.startActivity(intent)

            }

            // check if the current user has liked the post and update the like count accordingly
            val postLikeReference =
                databaseReference.child("blogs").child(postId.toString()).child("likes")

            val currentUserLiked = currentUser?.uid?.let { uid ->
                postLikeReference.child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                binding.Like.setImageResource(R.drawable.redlike)
                            } else {
                                binding.Like.setImageResource(R.drawable.blacklike)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }


                    })
            }


            // handle like button clicks

            binding.Like.setOnClickListener {

                if (currentUser != null) {
                    handleLikeButtonClicked(postId, blogItemModel, binding)
                } else {

                    Toast.makeText(context, "Please login to like", Toast.LENGTH_SHORT).show()
                }

            }

            //set the intial icon based on the saved satatus

            val userReference = databaseReference.child("users").child(currentUser!!.uid)
            val postSaveReference = userReference.child("saveBlogPost").child(postId.toString())

            postSaveReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        binding.postSaveButton.setImageResource(R.drawable.purered)
                    } else {
                        binding.postSaveButton.setImageResource(R.drawable.redsave)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }


            })

// handle save button clickes

            binding.postSaveButton.setOnClickListener {
                if (currentUser != null) {
                    handleSaveButtonClicked(postId, blogItemModel, binding)
                } else {

                    Toast.makeText(context, "Please login to like", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }



    private fun handleLikeButtonClicked(
        postId: String?,
        blogItemModel: BlogItemModel,
        binding: BlogItemBinding
    ) {


        val userReference = databaseReference.child("users").child(currentUser!!.uid)

        val postLikeReference =
            databaseReference.child("blogs").child(postId.toString()).child("likes")

        // check user has already liked the post , so unlike it

        postLikeReference.child(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        userReference.child("likes").child(postId.toString()).removeValue()
                            .addOnSuccessListener {
                                postLikeReference.child(currentUser.uid).removeValue()

                                blogItemModel.likedBy?.remove(currentUser.uid)
                                updateLikeButtonImage(binding, false)


                                // decrement the like in the databse


                                val newLikeCount = blogItemModel.likeCount - 1
                                blogItemModel.likeCount = newLikeCount
                                databaseReference.child("blogs").child(postId.toString())
                                    .child("likeCount").setValue(newLikeCount)

                                notifyDataSetChanged()

                            }

                            .addOnFailureListener { e ->

                                Log.e("LickedClicked", "onDataChange: Failed to unlike $e")
                                Toast.makeText(
                                    binding.root.context,
                                    "Failed to unlike",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {

                        userReference.child("likes").child(postId.toString()).setValue(true)
                            .addOnSuccessListener {

                                postLikeReference.child(currentUser.uid).setValue(true)
                                blogItemModel.likedBy?.add(currentUser.uid)
                                updateLikeButtonImage(binding, true)

                                val newLikeCount = blogItemModel.likeCount + 1
                                blogItemModel.likeCount = newLikeCount
                                databaseReference.child("blogs").child(postId.toString())
                                    .child("likeCount").setValue(newLikeCount)

                                notifyDataSetChanged()
                            }

                            .addOnFailureListener { e ->
                                Log.e("LickedClicked", "onDataChange: Failed to like  the blog $e")
                                Toast.makeText(
                                    binding.root.context,
                                    "Failed to like the blog ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
    }

    private fun handleSaveButtonClicked(
        postId: String?,
        blogItemModel: BlogItemModel,
        binding: BlogItemBinding
    ) {

        val userReference = databaseReference.child("users").child(currentUser!!.uid)

        userReference.child("saveBlogPosts").child(postId.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               // The blog is currently saved, so un saved it

                if(snapshot.exists()){

                    userReference.child("saveBlogPosts").child(postId.toString()).removeValue()
                        .addOnSuccessListener {

                            //update the Ui

                            val clickedBlogItem = items.find { it.postId == postId }
                            clickedBlogItem?.isSaved = false
                            notifyDataSetChanged()

                            val Context = binding.root.context
                            Toast.makeText(Context, "Blog Un Saved", Toast.LENGTH_SHORT).show()
                        } .addOnFailureListener {

                            val Context = binding.root.context
                            Toast.makeText(Context, "Failed to un save the blog", Toast.LENGTH_SHORT).show()

                        }

                    binding.postSaveButton.setImageResource(R.drawable.redsave)
                } else {

                    // the blog is not saved , so save it

                    userReference.child("saveBlogPosts").child(postId.toString()).setValue(true)
                        .addOnSuccessListener {

                            // update Ui

                            val clickedBlogItem = items.find { it.postId == postId }

                            clickedBlogItem?.isSaved = true
                            notifyDataSetChanged()

                            val Context = binding.root.context
                            Toast.makeText(Context, "Blog Saved", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {

                            val Context = binding.root.context
                            Toast.makeText(Context, "Failed to save the blog", Toast.LENGTH_SHORT).show()
                        }

                    //change the save button ui

                    binding.postSaveButton.setImageResource(R.drawable.purered)
                }



            }

            override fun onCancelled(error: DatabaseError) {

            }


        })


    }


    private fun updateLikeButtonImage(binding: BlogItemBinding, isLiked: Boolean) {
        if (isLiked) {

            binding.Like.setImageResource(R.drawable.blacklike)
        } else {

            binding.Like.setImageResource(R.drawable.redlike)
        }

    }

    fun updateData(savedBlosArtical: List<BlogItemModel>) {
        items.clear()
        items.addAll(savedBlosArtical)
        notifyDataSetChanged()
    }
}