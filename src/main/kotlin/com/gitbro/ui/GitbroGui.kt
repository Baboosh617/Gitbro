package com.gitbro.ui

import com.gitbro.api.GitHubApiClient
import com.gitbro.models.GitHubUser
import com.gitbro.models.GitHubRepo
import com.gitbro.utils.Formatter
import java.awt.*
import java.awt.event.*
import java.net.URI
import javax.swing.*
import javax.swing.border.*
import javax.swing.text.*


// GitbroGui.kt  –  The Graphical User Interface (GUI) layer

class GitbroGui {

    private val BG_DARK      = Color(13,  17,  23)   
    private val BG_SURFACE   = Color(22,  27,  34)   
    private val BG_INPUT     = Color(33,  38,  45)   
    private val BORDER_COLOR = Color(48,  54,  61)   
    private val ACCENT_BLUE  = Color(88,  166, 255)  
    private val ACCENT_GREEN = Color(63,  185, 80)   
    private val TEXT_PRIMARY = Color(230, 237, 243)  
    private val TEXT_MUTED   = Color(139, 148, 158)  
    private val TEXT_YELLOW  = Color(210, 153, 34)   
    private val TEXT_PINK    = Color(240, 100, 100)  
    private val ACCENT_PURPLE= Color(163, 113, 247)  

    
    private val FONT_MONO    = Font("JetBrains Mono",  Font.PLAIN,  13).let {
    
        if (it.family == "JetBrains Mono") it
        else Font("Consolas", Font.PLAIN, 13).let { c ->
            if (c.family == "Consolas") c else Font(Font.MONOSPACED, Font.PLAIN, 13)
        }
    }
    private val FONT_BOLD    = Font("Segoe UI", Font.BOLD,  15)
    private val FONT_REGULAR = Font("Segoe UI", Font.PLAIN, 13)
    private val FONT_LARGE   = Font("Segoe UI", Font.BOLD,  22)
    private val FONT_SMALL   = Font("Segoe UI", Font.PLAIN, 11)

    
    private lateinit var frame: JFrame
    private lateinit var searchField: JTextField
    private lateinit var searchButton: JButton
    private lateinit var statusLabel: JLabel
    private lateinit var resultsPane: JTextPane
    private lateinit var loadingPanel: JPanel
    private lateinit var welcomePanel: JPanel
    private lateinit var resultsScrollPane: JScrollPane
    private lateinit var contentStack: JPanel

    

    
    fun show() {
        setupLookAndFeel()
        buildWindow()
        frame.isVisible = true
    }

    private fun setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (_: Exception) { /* keep default */ }

        UIManager.put("Panel.background",          BG_DARK)
        UIManager.put("TextField.background",       BG_INPUT)
        UIManager.put("TextField.foreground",       TEXT_PRIMARY)
        UIManager.put("TextField.caretForeground",  ACCENT_BLUE)
        UIManager.put("Button.background",          ACCENT_BLUE)
        UIManager.put("Button.foreground",          BG_DARK)
        UIManager.put("ScrollPane.background",      BG_DARK)
        UIManager.put("ScrollBar.background",       BG_SURFACE)
        UIManager.put("ScrollBar.thumb",            BORDER_COLOR)
    }


    private fun buildWindow() {
        frame = JFrame("Gitbro — GitHub Profile Viewer").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            setSize(860, 680)
            minimumSize = Dimension(680, 520)
            setLocationRelativeTo(null)   // centres on screen
            background = BG_DARK
            contentPane.background = BG_DARK

            layout = BorderLayout(0, 0)
        }

        frame.add(buildHeaderPanel(),  BorderLayout.NORTH)
        frame.add(buildContentPanel(), BorderLayout.CENTER)
        frame.add(buildStatusBar(),    BorderLayout.SOUTH)
    }

    private fun buildHeaderPanel(): JPanel {
        val header = JPanel(BorderLayout()).apply {
            background = BG_SURFACE
            border = CompoundBorder(
                MatteBorder(0, 0, 1, 0, BORDER_COLOR),
                EmptyBorder(20, 28, 20, 28)
            )
        }

        val titleRow = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply {
            background = BG_SURFACE
            isOpaque   = false
        }

        val octocatLabel = JLabel("🐙 ").apply {
            font = Font("Segoe UI Emoji", Font.PLAIN, 24)
            foreground = ACCENT_BLUE
        }

        val titleLabel = JLabel("Gitbro").apply {
            font = FONT_LARGE
            foreground = TEXT_PRIMARY
        }

        val subtitleLabel = JLabel("  GitHub Profile Viewer").apply {
            font      = FONT_SMALL
            foreground = TEXT_MUTED
        }

        titleRow.add(octocatLabel)
        titleRow.add(titleLabel)
        titleRow.add(subtitleLabel)

        val searchRow = JPanel(BorderLayout(10, 0)).apply {
            background = BG_SURFACE
            isOpaque   = false
            border     = EmptyBorder(14, 0, 0, 0)
        }

        searchField = JTextField().apply {
            font        = FONT_REGULAR
            background  = BG_INPUT
            foreground  = TEXT_PRIMARY
            border      = CompoundBorder(
                LineBorder(BORDER_COLOR, 1, true),
                EmptyBorder(10, 14, 10, 14)
            )

            putClientProperty("JTextField.placeholderText", "Enter a GitHub username…")
            
            addActionListener { triggerSearch() }
        }
        addPlaceholder(searchField, "Enter a GitHub username  (e.g. octocat)")

        searchButton = JButton("  Search  ").apply {
            font            = Font("Segoe UI", Font.BOLD, 13)
            background      = ACCENT_BLUE
            foreground      = BG_DARK
            border          = CompoundBorder(
                LineBorder(ACCENT_BLUE, 1, true),
                EmptyBorder(10, 20, 10, 20)
            )
            isFocusPainted  = false
            cursor          = Cursor(Cursor.HAND_CURSOR)
            addActionListener { triggerSearch() }
        }

        searchRow.add(searchField,  BorderLayout.CENTER)
        searchRow.add(searchButton, BorderLayout.EAST)

        header.add(titleRow,  BorderLayout.NORTH)
        header.add(searchRow, BorderLayout.CENTER)

        return header
    }

    
    private fun buildContentPanel(): JPanel {
        contentStack = JPanel(CardLayout()).apply {
            background = BG_DARK
        }

        welcomePanel      = buildWelcomePanel()
        loadingPanel      = buildLoadingPanel()
        val resultsPanel  = buildResultsPanel()

        contentStack.add(welcomePanel,  "welcome")
        contentStack.add(loadingPanel,  "loading")
        contentStack.add(resultsPanel,  "results")

        showCard("welcome")
        return contentStack
    }

    private fun showCard(name: String) {
        (contentStack.layout as CardLayout).show(contentStack, name)
    }


    private fun buildWelcomePanel(): JPanel {
        val panel = JPanel(GridBagLayout()).apply { background = BG_DARK }
        val inner = JPanel().apply {
            layout     = BoxLayout(this, BoxLayout.Y_AXIS)
            background = BG_DARK
            isOpaque   = true
        }

        fun centredLabel(text: String, font: Font, color: Color): JLabel =
            JLabel(text, SwingConstants.CENTER).apply {
                this.font       = font
                foreground      = color
                alignmentX      = Component.CENTER_ALIGNMENT
            }

        inner.add(Box.createVerticalStrut(20))
        inner.add(centredLabel("🐙", Font("Segoe UI Emoji", Font.PLAIN, 72), TEXT_PRIMARY))
        inner.add(Box.createVerticalStrut(16))
        inner.add(centredLabel("Welcome to Gitbro", FONT_LARGE, TEXT_PRIMARY))
        inner.add(Box.createVerticalStrut(10))
        inner.add(centredLabel("Type a GitHub username above and press Search", FONT_REGULAR, TEXT_MUTED))
        inner.add(Box.createVerticalStrut(6))
        inner.add(centredLabel("to explore any developer's public profile and repositories.", FONT_REGULAR, TEXT_MUTED))
        inner.add(Box.createVerticalStrut(30))

        val examplesLabel = centredLabel("Try an example:", FONT_SMALL, TEXT_MUTED)
        inner.add(examplesLabel)
        inner.add(Box.createVerticalStrut(10))

        val examplesRow = JPanel(FlowLayout(FlowLayout.CENTER, 10, 0)).apply {
            background = BG_DARK
            isOpaque   = true
        }
        listOf("octocat", "torvalds", "gaearon", "sindresorhus").forEach { username ->
            examplesRow.add(makePillButton(username) {
                searchField.text = username
                triggerSearch()
            })
        }
        inner.add(examplesRow)

        panel.add(inner)
        return panel
    }

    private fun makePillButton(label: String, onClick: () -> Unit): JButton {
        return JButton(label).apply {
            font           = FONT_SMALL
            background     = BG_INPUT
            foreground     = ACCENT_BLUE
            border         = CompoundBorder(
                LineBorder(BORDER_COLOR, 1, true),
                EmptyBorder(6, 14, 6, 14)
            )
            isFocusPainted = false
            cursor         = Cursor(Cursor.HAND_CURSOR)
            addActionListener { onClick() }
        }
    }

    private fun buildLoadingPanel(): JPanel {
        val panel = JPanel(GridBagLayout()).apply { background = BG_DARK }
        val inner = JPanel().apply {
            layout     = BoxLayout(this, BoxLayout.Y_AXIS)
            background = BG_DARK
        }

        val progressBar = JProgressBar().apply {
            isIndeterminate = true
            preferredSize   = Dimension(280, 6)
            maximumSize     = Dimension(280, 6)
            alignmentX      = Component.CENTER_ALIGNMENT
            background      = BORDER_COLOR
            foreground      = ACCENT_BLUE
            border          = EmptyBorder(0, 0, 0, 0)
        }

        val loadingLabel = JLabel("Fetching data from GitHub…", SwingConstants.CENTER).apply {
            font       = FONT_REGULAR
            foreground = TEXT_MUTED
            alignmentX = Component.CENTER_ALIGNMENT
        }

        inner.add(progressBar)
        inner.add(Box.createVerticalStrut(14))
        inner.add(loadingLabel)

        panel.add(inner)
        return panel
    }

    private fun buildResultsPanel(): JScrollPane {
        resultsPane = JTextPane().apply {
            isEditable      = false
            background      = BG_DARK
            foreground      = TEXT_PRIMARY
            font            = FONT_MONO
            border          = EmptyBorder(24, 30, 24, 30)

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) { handleLinkClick(e) }
            })
            cursor = Cursor(Cursor.TEXT_CURSOR)
        }

        resultsScrollPane = JScrollPane(resultsPane).apply {
            background          = BG_DARK
            border              = null
            verticalScrollBarPolicy   = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            verticalScrollBar.background   = BG_SURFACE
            horizontalScrollBar.background = BG_SURFACE
        }
        return resultsScrollPane
    }

    private fun buildStatusBar(): JPanel {
        val bar = JPanel(BorderLayout()).apply {
            background = BG_SURFACE
            border = CompoundBorder(
                MatteBorder(1, 0, 0, 0, BORDER_COLOR),
                EmptyBorder(6, 28, 6, 28)
            )
        }

        statusLabel = JLabel("Ready").apply {
            font       = FONT_SMALL
            foreground = TEXT_MUTED
        }

        val versionLabel = JLabel("Gitbro v1.0.0").apply {
            font       = FONT_SMALL
            foreground = BORDER_COLOR
        }

        bar.add(statusLabel,  BorderLayout.WEST)
        bar.add(versionLabel, BorderLayout.EAST)
        return bar
    }

    private fun triggerSearch() {

        val raw = searchField.text.trim()
        val username = if (raw == PLACEHOLDER) "" else raw

        if (username.isBlank()) {
            showStatus("⚠  Please enter a GitHub username.", TEXT_YELLOW)
            searchField.requestFocus()
            return
        }

        if (!username.matches(Regex("[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,37}[a-zA-Z0-9])?"))) {
            showStatus("⚠  Invalid username format. GitHub usernames use letters, numbers and hyphens.", TEXT_YELLOW)
            return
        }

        searchButton.isEnabled = false
        searchButton.text = "  Searching…  "
        showCard("loading")
        showStatus("🔍  Fetching @$username from GitHub…", TEXT_MUTED)

        val worker = object : SwingWorker<Triple<GitHubUser?, List<GitHubRepo>?, String?>, Void>() {

            override fun doInBackground(): Triple<GitHubUser?, List<GitHubRepo>?, String?> {
                val userResult = GitHubApiClient.fetchUser(username)
                if (userResult.isFailure) {
                    return Triple(null, null, userResult.exceptionOrNull()?.message)
                }
                val user = userResult.getOrNull()!!

                val reposResult = GitHubApiClient.fetchRepos(username)
                if (reposResult.isFailure) {
                    return Triple(user, null, reposResult.exceptionOrNull()?.message)
                }

                return Triple(user, reposResult.getOrNull(), null)
            }

            override fun done() {
                searchButton.isEnabled = true
                searchButton.text = "  Search  "

                val (user, repos, error) = get()  

                when {
                    error != null -> {
                        showCard("welcome")
                        showStatus("✗  $error", TEXT_PINK)
                    }
                    user != null -> {
                        renderResults(user, repos ?: emptyList())
                        showCard("results")
                        showStatus(
                            "✓  Showing results for @${user.login}  •  ${repos?.size ?: 0} repositories found",
                            ACCENT_GREEN
                        )
                        // Scroll to top of results
                        SwingUtilities.invokeLater {
                            resultsScrollPane.verticalScrollBar.value = 0
                        }
                    }
                }
            }
        }

        worker.execute()
    }

    private fun renderResults(user: GitHubUser, repos: List<GitHubRepo>) {
        val doc = resultsPane.styledDocument
        doc.remove(0, doc.length)   


        fun insert(text: String, style: AttributeSet) = doc.insertString(doc.length, text, style)
        fun nl(n: Int = 1) = insert("\n".repeat(n), plain())

        // User section 
        insert("  👤  ", h1())
        insert("@${user.login}", h1())
        nl()
        insert("  ${user.name ?: "No name set"}", subheading())
        nl(2)

        // Stats row
        statsLine(doc, "  Followers", Formatter.formatNumber(user.followers))
        statsLine(doc, "  Following", Formatter.formatNumber(user.following))
        statsLine(doc, "  Public Repos", "${user.publicRepos}")
        nl()

        if (user.bio != null) {
            insert("  💬  ", plain())
            insert(user.bio, italic())
            nl()
        }

        insert("  🔗  ", plain())
        insert(user.htmlUrl, link())
        nl(2)

        // Divider
        insert("  ${"─".repeat(50)}", dimmed())
        nl(2)

        //  Repos section 
        insert("  📦  TOP REPOSITORIES", h2())
        insert("   (${minOf(10, repos.size)} of ${repos.size})", dimmed())
        nl(2)

        if (repos.isEmpty()) {
            insert("  No public repositories found.", dimmed())
            nl()
            return
        }

        repos.take(10).forEachIndexed { i, repo ->
            // Repo number + name
            insert("  ${i + 1}.  ", dimmed())
            insert(repo.name, repoName())
            nl()

            // Description
            insert("       ${repo.description ?: "No description provided"}", dimmed())
            nl()

            // Metrics row
            insert("       ⭐ ", plain())
            insert("${Formatter.formatNumber(repo.stargazersCount)}", starred())
            insert("   🍴 ", plain())
            insert("${Formatter.formatNumber(repo.forksCount)}", forked())
            insert("   💻 ", plain())
            insert("${repo.language ?: "—"}", language())
            nl()

            // Link
            insert("       🔗  ", plain())
            insert(repo.htmlUrl, link())
            nl(2)
        }
    }


    private fun plain()      = SimpleAttributeSet().also {
        StyleConstants.setFontFamily(it, FONT_MONO.family)
        StyleConstants.setFontSize(it, 13)
        StyleConstants.setForeground(it, TEXT_PRIMARY)
    }

    private fun dimmed()     = plain().also { StyleConstants.setForeground(it, TEXT_MUTED) }
    private fun italic()     = plain().also {
        StyleConstants.setItalic(it, true)
        StyleConstants.setForeground(it, TEXT_MUTED)
    }

    private fun h1()         = SimpleAttributeSet().also {
        StyleConstants.setFontFamily(it, FONT_BOLD.family)
        StyleConstants.setFontSize(it, 22)
        StyleConstants.setBold(it, true)
        StyleConstants.setForeground(it, TEXT_PRIMARY)
    }

    private fun h2()         = SimpleAttributeSet().also {
        StyleConstants.setFontFamily(it, FONT_BOLD.family)
        StyleConstants.setFontSize(it, 15)
        StyleConstants.setBold(it, true)
        StyleConstants.setForeground(it, TEXT_PRIMARY)
    }

    private fun subheading() = SimpleAttributeSet().also {
        StyleConstants.setFontFamily(it, FONT_REGULAR.family)
        StyleConstants.setFontSize(it, 14)
        StyleConstants.setForeground(it, TEXT_MUTED)
    }

    private fun link()       = plain().also {
        StyleConstants.setForeground(it, ACCENT_BLUE)
        StyleConstants.setUnderline(it, true)
    }

    private fun repoName()   = SimpleAttributeSet().also {
        StyleConstants.setFontFamily(it, FONT_BOLD.family)
        StyleConstants.setFontSize(it, 14)
        StyleConstants.setBold(it, true)
        StyleConstants.setForeground(it, ACCENT_BLUE)
    }

    private fun starred()    = plain().also { StyleConstants.setForeground(it, TEXT_YELLOW) }
    private fun forked()     = plain().also { StyleConstants.setForeground(it, TEXT_PINK) }
    private fun language()   = plain().also { StyleConstants.setForeground(it, ACCENT_PURPLE) }

    /** Inserts a key-value line like "  Followers    4,200" */
    private fun statsLine(doc: StyledDocument, label: String, value: String) {
        doc.insertString(doc.length, "$label", SimpleAttributeSet().also {
            StyleConstants.setFontFamily(it, FONT_MONO.family)
            StyleConstants.setFontSize(it, 12)
            StyleConstants.setForeground(it, TEXT_MUTED)
        })
        doc.insertString(doc.length, "   $value\n", SimpleAttributeSet().also {
            StyleConstants.setFontFamily(it, FONT_BOLD.family)
            StyleConstants.setFontSize(it, 13)
            StyleConstants.setForeground(it, TEXT_PRIMARY)
        })
    }

    private fun handleLinkClick(e: MouseEvent) {
        val offset  = resultsPane.viewToModel2D(e.point).toInt()
        val doc     = resultsPane.styledDocument
        val element = doc.getCharacterElement(offset)
        val attrs   = element.attributes

        if (StyleConstants.isUnderline(attrs)) {
            val start = element.startOffset
            val end   = element.endOffset
            try {
                val url = doc.getText(start, end - start).trim()
                if (url.startsWith("https://")) {
                    Desktop.getDesktop().browse(URI(url))
                }
            } catch (_: Exception) { /* ignore bad URLs */ }
        }
    }


    private fun showStatus(message: String, color: Color = TEXT_MUTED) {
        SwingUtilities.invokeLater {
            statusLabel.text       = message
            statusLabel.foreground = color
        }
    }

    private val PLACEHOLDER = "Enter a GitHub username  (e.g. octocat)"

    private fun addPlaceholder(field: JTextField, hint: String) {
        field.text       = hint
        field.foreground = TEXT_MUTED

        field.addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent) {
                if (field.text == hint) {
                    field.text       = ""
                    field.foreground = TEXT_PRIMARY
                }
            }
            override fun focusLost(e: FocusEvent) {
                if (field.text.isBlank()) {
                    field.text       = hint
                    field.foreground = TEXT_MUTED
                }
            }
        })
    }
}
