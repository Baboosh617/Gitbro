package com.gitbro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gitbro.ui.screens.HomeScreen
import com.gitbro.ui.screens.ProfileScreen
import com.gitbro.ui.theme.AccentBlue
import com.gitbro.ui.theme.BgDark
import com.gitbro.ui.theme.ForkPink
import com.gitbro.ui.theme.GitbroTheme
import com.gitbro.ui.theme.TextPrimary
import com.gitbro.viewmodel.GitHubViewModel
import com.gitbro.viewmodel.UiState

class MainActivity : ComponentActivity() {

    // viewModels() creates the ViewModel and ties its lifecycle
    // to this Activity. If you rotate the phone, the same ViewModel
    // instance is returned — your search results survive rotation.
    private val viewModel: GitHubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent replaces the old XML layout system.
        // Everything inside here is Compose UI.
        setContent {
            GitbroTheme {

                // collectAsState turns the StateFlow into a Compose State.
                // Whenever uiState changes, this whole block re-renders.
                val uiState by viewModel.uiState.collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgDark),
                    contentAlignment = Alignment.Center
                ) {
                    // Show different screens based on the current state
                    when (val state = uiState) {

                        is UiState.Idle -> HomeScreen(
                            onSearch = { viewModel.searchUser(it) }
                        )

                        is UiState.Loading -> CircularProgressIndicator(
                            color = AccentBlue
                        )

                        is UiState.Success -> ProfileScreen(
                            user   = state.user,
                            repos  = state.repos,
                            onBack = { viewModel.reset() }
                        )

                        is UiState.Error -> {
                            // Show error then snap back to home after a moment
                            HomeScreen(onSearch = { viewModel.searchUser(it) })
                            // Error is displayed via a Snackbar — add if needed
                            Text(
                                text = "⚠ ${state.message}",
                                color = ForkPink,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
            }
        }
    }
}