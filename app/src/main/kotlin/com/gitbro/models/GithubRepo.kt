package com.gitbro.models

import com.google.gson.annotations.SerializedName

data class GitHubRepo(
    val name: String,
    val description: String?,
    @SerializedName("stargazers_count") val stargazersCount: Int,
    val language: String?,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("forks_count") val forksCount: Int
)