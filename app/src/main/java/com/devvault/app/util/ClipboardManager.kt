package com.devvault.app.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun copiarAlPortapapeles(context: Context, texto: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("snippet", texto))
}