package com.gitbro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gitbro.models.GitHubRepo
import com.gitbro.models.GitHubUser
import com.gitbro.ui.theme.*
import com.gitbro.utils.Formatter

// LazyColumn is Compose's equivalent of RecyclerView.
// It only renders items that are currently visible on screen —
// critical for performance when displaying long repo lists.

@Composable
fun ProfileScreen(
    user: GitHubUser,
    repos: List<GitHubRepo>,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // ── Top bar ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "@${user.login}",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Divider(color = BorderColor, thickness = 1.dp)

        // ── Scrollable content ────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // User profile card
            item { UserCard(user) }

            // Section header
            item {
                Text(
                    text = "📦  Top Repositories (${minOf(10, repos.size)} of ${repos.size})",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Repo cards — only top 10
            items(repos.take(10)) { repo ->
                RepoCard(repo)
            }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ── USER CARD ─────────────────────────────────────────────────────────────────

@Composable
fun UserCard(user: GitHubUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = BgSurface),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = user.name ?: user.login,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextPrimary
            )

            if (user.name != null) {
                Text(
                    text = "@${user.login}",
                    color = AccentBlue,
                    fontSize = 14.sp
                )
            }

            if (!user.bio.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = user.bio,
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = BorderColor)
            Spacer(modifier = Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Repos",     value = "${user.publicRepos}")
                StatItem(label = "Followers", value = Formatter.formatNumber(user.followers))
                StatItem(label = "Following", value = Formatter.formatNumber(user.following))
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
        Text(label, fontSize = 12.sp, color = TextMuted)
    }
}

// ── REPO CARD ─────────────────────────────────────────────────────────────────

@Composable
fun RepoCard(repo: GitHubRepo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = BgSurface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = repo.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = AccentBlue
            )

            if (!repo.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = repo.description,
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics row
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                if (repo.language != null) {
                    MetricChip(icon = "💻", label = repo.language, color = LangPurple)
                }
                MetricChip(
                    icon  = "⭐",
                    label = Formatter.formatNumber(repo.stargazersCount),
                    color = StarYellow
                )
                MetricChip(
                    icon  = "🍴",
                    label = Formatter.formatNumber(repo.forksCount),
                    color = ForkPink
                )
            }
        }
    }
}

@Composable
fun MetricChip(icon: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(icon, fontSize = 13.sp)
        Text(label, fontSize = 13.sp, color = color, fontWeight = FontWeight.Medium)
    }
}