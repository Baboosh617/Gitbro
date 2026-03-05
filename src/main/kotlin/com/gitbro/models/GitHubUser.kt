package com.gitbro.models

// GitHubUser.kt  –  The "shape" of a GitHub user in our application

data class GitHubUser(
    val login: String,         
    val name: String?,         
    val bio: String?,          
    val publicRepos: Int,      
    val followers: Int,        
    val following: Int,        
    val avatarUrl: String,     
    val htmlUrl: String        
)
