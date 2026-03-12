package com.gitbro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitbro.api.RetrofitInstance
import com.gitbro.models.GitHubRepo
import com.gitbro.models.GitHubUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// GitHubViewModel.kt
//
// WHAT IS A VIEWMODEL?
//   A ViewModel is Android's answer to the question:
//   "Where does the data live while the user is using the app?"
//
//   It survives screen rotations (unlike an Activity which gets destroyed
//   and recreated when you rotate your phone). It holds the UI state and
//   is the ONLY place that triggers network requests.
//
// WHAT IS StateFlow?
//   StateFlow is a stream that holds ONE current value and emits it to
//   all collectors whenever it changes.
//   The Compose screens "collect" these flows — when the value changes,
//   Compose automatically recomposes (re-renders) the affected UI.
//
//   MutableStateFlow → can be changed (private, only ViewModel touches it)
//   StateFlow        → read-only view exposed to the UI
// ─────────────────────────────────────────────────────────────────────────────

// Sealed class = a restricted class hierarchy.
// UiState can ONLY be one of these four types — nothing else.
// This models every possible state the screen can be in.
sealed class UiState {
    object Idle    : UiState()   // App just opened, no search yet
    object Loading : UiState()   // Waiting for GitHub API response
    data class Success(          // Got data back successfully
        val user: GitHubUser,
        val repos: List<GitHubRepo>
    ) : UiState()
    data class Error(val message: String) : UiState()  // Something went wrong
}

class GitHubViewModel : ViewModel() {

    // _uiState is private and mutable — only this ViewModel can change it
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)

    // uiState is what the UI screens observe — read-only
    val uiState: StateFlow<UiState> = _uiState

    fun searchUser(username: String) {
        if (username.isBlank()) {
            _uiState.value = UiState.Error("Please enter a GitHub username.")
            return
        }

        // viewModelScope is a coroutine scope tied to this ViewModel's lifecycle.
        // When the ViewModel is destroyed, all coroutines in this scope are
        // automatically cancelled — no memory leaks.
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                // suspend functions: the coroutine suspends here while waiting
                // for the network. The UI thread is never blocked.
                val user  = RetrofitInstance.api.getUser(username)
                val repos = RetrofitInstance.api.getRepos(username)
                    .sortedByDescending { it.stargazersCount }

                _uiState.value = UiState.Success(user, repos)

            } catch (e: retrofit2.HttpException) {
                _uiState.value = UiState.Error(
                    when (e.code()) {
                        404  -> "User \"$username\" not found on GitHub."
                        403  -> "GitHub rate limit exceeded. Please wait a minute."
                        else -> "GitHub API error (HTTP ${e.code()})."
                    }
                )
            } catch (e: java.io.IOException) {
                _uiState.value = UiState.Error(
                    "Network error. Check your internet connection."
                )
            }
        }
    }

    fun reset() {
        _uiState.value = UiState.Idle
    }
}