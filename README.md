# 🐙 Gitbro

**Gitbro** is a lightweight desktop GitHub profile viewer written in Kotlin. Type any GitHub username and instantly see their public profile and their top repositories — all in a dark, GitHub-inspired interface.

## Features

- 🔍 **Search any GitHub user** by username
- 👤 **Profile overview** — name, bio, public repo count, followers, following
- 📦 **Top 10 repositories**, ranked by stars, with description, ⭐ stars, 🍴 forks and language
- 🔗 **Clickable links** that open profiles and repos in your browser
- ⚡ **Quick-try examples** (`octocat`, `torvalds`, `gaearon`, `sindresorhus`) on the welcome screen
- 🌙 **Dark theme** inspired by GitHub's own colour palette
- 🧵 **Responsive UI** — network calls run in the background so the window never freezes
- ❗ **Friendly error messages** for unknown users and API rate limits

## Tech Stack

| | |
|---|---|
| Language | Kotlin 1.9 (JVM 17) |
| UI | Java Swing |
| HTTP | `java.net.HttpURLConnection` |
| JSON | [`org.json`](https://github.com/stleary/JSON-java) |
| Build | Gradle 8.7 (Kotlin DSL) + [Shadow](https://github.com/johnrengelman/shadow) for a single runnable JAR |

## Getting Started

### Prerequisites

- **JDK 17** or newer
- **Gradle 8.7** (only needed once to generate the wrapper JAR — see below)

### Build

```bash
git clone https://github.com/Baboosh617/Gitbro.git
cd Gitbro

# The Gradle wrapper JAR isn't committed, so generate it once:
gradle wrapper --gradle-version 8.7

# Build the fat JAR
./gradlew build
```

The runnable JAR is written to `build/libs/gitbro-1.0.0.jar`.

### Run

```bash
java -jar build/libs/gitbro-1.0.0.jar
```

or run straight from source:

```bash
./gradlew run
```

## Project Structure

```
src/main/kotlin/com/gitbro/
├── Main.kt                  # Entry point — launches the Swing window
├── api/
│   └── GitHubApiClient.kt   # Calls the GitHub REST API and parses JSON
├── models/
│   ├── GitHubUser.kt        # User profile data class
│   └── GitHubRepo.kt        # Repository data class
├── ui/
│   └── GitbroGui.kt         # The whole desktop UI (header, welcome, loading, results)
└── utils/
    └── Formatter.kt         # Number formatting helpers (e.g. 12,345)
```

## How It Works

1. You enter a username and press **Search** (or hit Enter).
2. Gitbro calls two public GitHub API endpoints in the background:
   - `GET https://api.github.com/users/{username}`
   - `GET https://api.github.com/users/{username}/repos?per_page=100&sort=stars`
3. The responses are parsed into `GitHubUser` and `GitHubRepo` objects.
4. The results screen renders the profile and the top 10 repos sorted by stars.

## Limitations

- Uses the **unauthenticated** GitHub API, which is limited to **60 requests per hour per IP**. Each search uses 2 requests, so you can do about 30 searches an hour. If you hit the limit, Gitbro will tell you — just wait a bit and try again.
- Only **public** profile data and repositories are shown.

## Android Version

An Android port built with **Jetpack Compose**, **Retrofit** and a **ViewModel** lives on the [`Mobile-v`](https://github.com/Baboosh617/Gitbro/tree/Mobile-v) branch.

## License

Released under the [MIT License](LICENSE) © 2026 Muhammad Ibrahim Shehu.
