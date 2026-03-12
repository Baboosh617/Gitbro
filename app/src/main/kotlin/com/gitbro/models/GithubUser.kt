package com.gitbro.models

import com.google.gson.annotations.SerializedName

// @SerializedName tells Gson: "when you see 'public_repos' in the JSON,
// map it to this field called publicRepos".
// This is how we bridge the gap between JSON's snake_case and Kotlin's camelCase.

data class GitHubUser(
    val login: String,
    val name: String?,
    val bio: String?,
    @SerializedName("public_repos") val publicRepos: Int,
    val followers: Int,
    val following: Int,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("html_url") val htmlUrl: String
)