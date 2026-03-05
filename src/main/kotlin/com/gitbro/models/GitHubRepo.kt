package com.gitbro.models

// GitHubRepo.kt  –  The "shape" of a single GitHub repository

data class GitHubRepo(
    val name: String,
    val description: String?,
    val stargazersCount: Int,
    val language: String?,   
    val htmlUrl: String,     
    val forksCount: Int      
)
