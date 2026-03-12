package com.gitbro.api

import com.gitbro.models.GitHubRepo
import com.gitbro.models.GitHubUser
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// This is a Retrofit "service interface".
// You define what the API endpoints look like as function signatures.
// Retrofit generates the actual HTTP code for you automatically.
//
// @GET("/users/{username}") means:
//   → HTTP GET request to https://api.github.com/users/octocat
//   → {username} is replaced by whatever String you pass into the function
//
// suspend means this function must be called from a coroutine.
// It pauses the coroutine while waiting for the network — without
// blocking any thread (including the UI thread).

interface GitHubApiService {

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): GitHubUser

    @GET("users/{username}/repos")
    suspend fun getRepos(
        @Path("username") username: String,
        @Query("per_page") perPage: Int = 100,
        @Query("sort") sort: String = "stars"
    ): List<GitHubRepo>
}