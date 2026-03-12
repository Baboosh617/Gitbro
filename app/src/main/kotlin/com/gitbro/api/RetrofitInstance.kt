package com.gitbro.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit is built once and reused everywhere (singleton pattern).
// Building it is expensive — we never want to build it per request.
//
// `by lazy` means: don't create this until the first time it's accessed.
// After that, the same instance is returned every time.

object RetrofitInstance {

    private const val BASE_URL = "https://api.github.com/"

    val api: GitHubApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON → data class
            .build()
            .create(GitHubApiService::class.java)
    }
}