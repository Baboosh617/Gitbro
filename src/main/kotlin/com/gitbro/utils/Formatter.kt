package com.gitbro.utils

import com.gitbro.models.GitHubUser
import com.gitbro.models.GitHubRepo

// Formatter.kt  –  The "presentation layer" helper

object Formatter {
    fun formatUser(user: GitHubUser): String {
        val line = "─".repeat(45)
        return buildString {
            appendLine(line)
            appendLine("  👤  GITHUB USER PROFILE")
            appendLine(line)
            appendLine("  Username     : @${user.login}")
            appendLine("  Name         : ${user.name ?: "Not specified"}")
            appendLine("  Bio          : ${user.bio ?: "No bio available"}")
            appendLine("  Public Repos : ${user.publicRepos}")
            appendLine("  Followers    : ${formatNumber(user.followers)}")
            appendLine("  Following    : ${formatNumber(user.following)}")
            appendLine("  Profile URL  : ${user.htmlUrl}")
            append(line)
        }
    }

    fun formatRepos(repos: List<GitHubRepo>, limit: Int = 10): String {
        val line = "─".repeat(45)
        return buildString {
            appendLine("\n$line")
            appendLine("  📦  TOP REPOSITORIES (showing top ${minOf(limit, repos.size)} of ${repos.size})")
            appendLine(line)

            if (repos.isEmpty()) {
                appendLine("  No public repositories found.")
                append(line)
                return@buildString
            }

            repos.take(limit).forEachIndexed { index, repo ->
                appendLine("  ${index + 1}. ${repo.name}")
                appendLine("     📝  ${repo.description ?: "No description provided"}")
                appendLine("     ⭐  Stars    : ${formatNumber(repo.stargazersCount)}")
                appendLine("     🍴  Forks    : ${formatNumber(repo.forksCount)}")
                appendLine("     💻  Language : ${repo.language ?: "Not detected"}")
                appendLine("     🔗  ${repo.htmlUrl}")
                if (index < minOf(limit, repos.size) - 1) appendLine()
            }
            append(line)
        }
    }

    fun formatNumber(n: Int): String = "%,d".format(n)
}
