package com.example.blogapp.model

import android.os.Parcel
import android.os.Parcelable

data class BlogItemModel (
    val date: String? = "null",
    var heading: String? = "null",
    var likeCount: Int = 0,
    var post: String? = "null",
    var postId: String? = "null",
    val profileImage: String? = "null",
    var isSaved:Boolean = false,
    val userId: String? = "null",
    val userName: String? = "null",
    val likedBy: MutableList<String>? = null,





    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"null",
        parcel.readString()?:"null",
        parcel.readInt(),
        parcel.readString()?:"null",
        parcel.readString()?:"null",
        parcel.readString()?:"null",
        parcel.readByte() != 0.toByte(),
        parcel.readString()?:"null",
        parcel.readString()?:"null",
        parcel.createStringArrayList()?.toMutableList(),


    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeString(heading)
        parcel.writeInt(likeCount)
        parcel.writeString(post)
        parcel.writeString(postId)
        parcel.writeString(profileImage)
        parcel.writeByte(if (isSaved) 1 else 0)
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeStringList(likedBy)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlogItemModel> {
        override fun createFromParcel(parcel: Parcel): BlogItemModel {
            return BlogItemModel(parcel)
        }

        override fun newArray(size: Int): Array<BlogItemModel?> {
            return arrayOfNulls(size)
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "date" to date,
            "heading" to heading,
            "likeCount" to likeCount,
            "post" to post,
            "postId" to postId,
            "profileImage" to profileImage,
            "userName" to userName
            // Exclude likedBy here if needed
        )
    }
}
