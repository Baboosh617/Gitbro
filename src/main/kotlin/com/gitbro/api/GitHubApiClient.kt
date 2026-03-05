package com.gitbro.api

import com.gitbro.models.GitHubUser
import com.gitbro.models.GitHubRepo
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// GitHubApiClient.kt  –  The "network layer" of Gitbro

object GitHubApiClient {

    private const val BASE_URL = "https://api.github.com"
    fun fetchUser(username: String): Result<GitHubUser> {
        return try {
            val jsonString = makeGetRequest("$BASE_URL/users/$username")
            val jsonObject = JSONObject(jsonString)      
            val user       = parseUser(jsonObject)       
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    
    fun fetchRepos(username: String): Result<List<GitHubRepo>> {
        return try {
            val jsonString = makeGetRequest(
                "$BASE_URL/users/$username/repos?per_page=100&sort=stars&direction=desc"
            )
            val jsonArray = JSONArray(jsonString)
            val repos     = parseRepos(jsonArray)
            Result.success(repos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    
    private fun makeGetRequest(urlString: String): String {
        val url        = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        connection.apply {
            requestMethod = "GET"

    
            setRequestProperty("Accept", "application/vnd.github.v3+json")

    
            setRequestProperty("User-Agent", "Gitbro-App/1.0")

    
            connectTimeout = 10_000
            readTimeout    = 15_000
        }

        val responseCode = connection.responseCode

        
        if (responseCode == 404) {
            throw Exception("GitHub user not found. Please check the username.")
        }

        
        if (responseCode == 403) {
            throw Exception("GitHub API rate limit exceeded. Please wait a minute and try again.")
        }

        
        if (responseCode != 200) {
            throw Exception("GitHub API returned an unexpected error (HTTP $responseCode).")
        }

        
        return connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    private fun parseUser(json: JSONObject): GitHubUser {
        return GitHubUser(
            login       = json.getString("login"),
            name        = json.optString("name",  null)?.takeIf { it.isNotEmpty() },
            bio         = json.optString("bio",   null)?.takeIf { it.isNotEmpty() },
            publicRepos = json.getInt("public_repos"),
            followers   = json.getInt("followers"),
            following   = json.getInt("following"),
            avatarUrl   = json.getString("avatar_url"),
            htmlUrl     = json.getString("html_url")
        )
    }

    private fun parseRepos(jsonArray: JSONArray): List<GitHubRepo> {
        val repos = mutableListOf<GitHubRepo>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            repos.add(
                GitHubRepo(
                    name            = obj.getString("name"),
                    description     = obj.optString("description", null)
                                         ?.takeIf { it.isNotEmpty() },
                    stargazersCount = obj.getInt("stargazers_count"),
                    language        = obj.optString("language", null)
                                         ?.takeIf { it.isNotEmpty() },
                    htmlUrl         = obj.getString("html_url"),
                    forksCount      = obj.getInt("forks_count")
                )
            )
        }

    
        return repos.sortedByDescending { it.stargazersCount }
    }
}
