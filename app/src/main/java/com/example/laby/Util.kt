package com.example.laby

import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.core.text.HtmlCompat
import com.example.laby.database.Score

fun formatScores(nights: List<Score>, resources: Resources): Spanned {
    val sb = StringBuilder()
    var iterator  = 1
    sb.apply {
        append(resources.getString(R.string.title))
        nights.forEach {
            append("<b>$iterator<b> ${it.username}:\t ${it.score} <br><br>")
            iterator++
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}