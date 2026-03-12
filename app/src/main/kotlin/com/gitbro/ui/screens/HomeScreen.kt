package com.gitbro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gitbro.ui.theme.*

// @Composable means this function draws UI.
// Compose is declarative: you describe WHAT the UI should look like
// given the current state — not HOW to change it step by step.
// When state changes, Compose re-runs the function automatically.

@Composable
fun HomeScreen(
    onSearch: (String) -> Unit   // callback: tells the ViewModel to search
) {
    // remember keeps this value alive across recompositions.
    // mutableStateOf triggers recomposition when the value changes.
    var username by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ── App icon + title ──────────────────────────────────────────────
        Text(text = "🐙", fontSize = 64.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text       = "Gitbro",
            fontSize   = 32.sp,
            fontWeight = FontWeight.Bold,
            color      = AccentBlue
        )

        Text(
            text  = "GitHub Profile Viewer",
            color = TextMuted,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // ── Search input ──────────────────────────────────────────────────
        OutlinedTextField(
            value         = username,
            onValueChange = { username = it },
            placeholder   = { Text("Enter a GitHub username…", color = TextMuted) },
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(12.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AccentBlue,
                unfocusedBorderColor = BorderColor,
                focusedTextColor     = TextPrimary,
                unfocusedTextColor   = TextPrimary,
                cursorColor          = AccentBlue,
                focusedContainerColor   = BgInput,
                unfocusedContainerColor = BgInput,
            ),
            // ImeAction.Search shows the search icon on the keyboard
            // and triggers onSearch when tapped
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(username.trim()) })
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Search button ─────────────────────────────────────────────────
        Button(
            onClick  = { onSearch(username.trim()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape  = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentBlue,
                contentColor   = BgDark
            )
        ) {
            Text("Search", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ── Example chips ─────────────────────────────────────────────────
        Text("Try an example:", color = TextMuted, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("octocat", "torvalds", "gaearon").forEach { example ->
                ExampleChip(label = example, onClick = { onSearch(example) })
            }
        }
    }
}

@Composable
fun ExampleChip(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape   = RoundedCornerShape(50.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
        colors  = ButtonDefaults.outlinedButtonColors(
            containerColor = BgInput,
            contentColor   = AccentBlue
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Text(label, fontSize = 12.sp)
    }
}