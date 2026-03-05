package com.gitbro

import com.gitbro.ui.GitbroGui
import javax.swing.SwingUtilities


// Main.kt  –  The entry point of Gitbro

fun main() {
    
    SwingUtilities.invokeLater {
        GitbroGui().show()
    }
}
