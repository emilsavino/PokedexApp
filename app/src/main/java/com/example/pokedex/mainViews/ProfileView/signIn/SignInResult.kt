package com.example.pokedex.mainViews.ProfileView.signIn

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userID: String,
    val userName: String?,
    val profilePictureURL: String?
)
