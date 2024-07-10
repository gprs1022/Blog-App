package com.example.blogapp.model

data class UserData(
    val name: String= "",
    val email: String= "",
    val profileImage: String = ""

){
    constructor(): this("", "", "")
}
