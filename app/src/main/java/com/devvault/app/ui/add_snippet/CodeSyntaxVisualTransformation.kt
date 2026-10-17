package com.devvault.app.ui.add_snippet

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CodeSyntaxVisualTransformation(
    private val language: String = ""
) : VisualTransformation {

    // Paleta de syntax highlighting alineada con el tema DevVault
    private val keywordColor = Color(0xFF33C8F2) // PrimaryCyan
    private val stringColor = Color(0xFF7FD9A8)  // CodeText (Verde claro suave)
    private val commentColor = Color(0xFF6B7280) // Gris tenue
    private val numberColor = Color(0xFFF5C542)  // StarFavorite (Amarillo dorado)
    private val sqlKeywordColor = Color(0xFFFF79C6) // Magenta suave para comandos SQL

    // Palabras clave comunes según lenguajes (Kotlin, Bash, SQL, Go, Docker, etc.)
    private val generalKeywords = setOf(
        "fun", "val", "var", "class", "interface", "object", "package", "import",
        "return", "if", "else", "when", "for", "while", "do", "break", "continue",
        "true", "false", "null", "override", "private", "public", "protected", "internal",
        "data", "sealed", "const", "suspend", "coroutine", "init", "try", "catch", "finally",
        "throw", "in", "is", "as", "by", "companion", "enum", "typealias",
        // Bash & CLI
        "echo", "sudo", "export", "alias", "source", "chmod", "chown", "grep",
        "mkdir", "rm", "cp", "mv", "touch", "cat", "ssh", "scp", "find",
        // Go
        "func", "package", "type", "struct", "go", "chan", "defer", "select", "case", "default"
    )

    private val sqlKeywords = setOf(
        "select", "from", "where", "insert", "into", "values", "update", "set", "delete",
        "join", "inner", "left", "right", "outer", "group", "by", "order", "asc", "desc",
        "having", "limit", "offset", "count", "distinct", "table", "create", "drop", "alter",
        "primary", "key", "foreign", "references", "null", "not", "and", "or", "like", "as"
    )

    private val dockerKeywords = setOf(
        "from", "run", "cmd", "label", "maintainer", "expose", "env", "add", "copy",
        "entrypoint", "volume", "user", "workdir", "arg", "onbuild", "stopsignal",
        "healthcheck", "shell", "docker", "compose", "up", "build", "ps", "stop"
    )

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val builder = AnnotatedString.Builder(raw)

        // 1. Números
        val numberRegex = Regex("\\b\\d+(\\.\\d+)?\\b")
        for (match in numberRegex.findAll(raw)) {
            builder.addStyle(
                SpanStyle(color = numberColor),
                match.range.first,
                match.range.last + 1
            )
        }

        // 2. Palabras clave / keywords
        val wordRegex = Regex("\\b[A-Za-z_][A-Za-z0-9_]*\\b")
        for (match in wordRegex.findAll(raw)) {
            val word = match.value
            val lower = word.lowercase()
            when {
                lower in sqlKeywords -> {
                    builder.addStyle(
                        SpanStyle(color = sqlKeywordColor, fontWeight = FontWeight.SemiBold),
                        match.range.first,
                        match.range.last + 1
                    )
                }
                lower in dockerKeywords || word in generalKeywords -> {
                    builder.addStyle(
                        SpanStyle(color = keywordColor, fontWeight = FontWeight.SemiBold),
                        match.range.first,
                        match.range.last + 1
                    )
                }
            }
        }

        // 3. Cadenas de texto ("..." y '...')
        val stringRegex = Regex("\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^'\\\\])*'")
        for (match in stringRegex.findAll(raw)) {
            builder.addStyle(
                SpanStyle(color = stringColor),
                match.range.first,
                match.range.last + 1
            )
        }

        // 4. Comentarios (//..., #..., --...)
        val commentRegex = Regex("(//.*$)|(#.*$)|(--.*$)", RegexOption.MULTILINE)
        for (match in commentRegex.findAll(raw)) {
            builder.addStyle(
                SpanStyle(color = commentColor),
                match.range.first,
                match.range.last + 1
            )
        }

        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }
}
