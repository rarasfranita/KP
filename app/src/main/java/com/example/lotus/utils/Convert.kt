package com.example.lotus.utils

import android.widget.TextView
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

fun dateToFormatTime(v: TextView, time: String?){
    val current = Calendar.getInstance();
    var timePost = Calendar.getInstance()
    val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
    val timeRemove = time?.removeRange(19, 23)

    timePost.setTime(sdf.parse(timeRemove))
    val diff: Long = current.getTime().time - timePost.getTime().time

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    if (seconds < 60 ){
        v.text = "Now"
    }else if(seconds < 61){
        v.text = "$minutes minute ago"
    }else if(minutes < 60){
        v.text = "$minutes minutes ago"
    }else if(minutes < 61){
        v.text = "$hours hour ago"
    }else if(hours < 24){
        v.text = "$hours hours ago"
    }else if(hours < 49){
        v.text = "Yesterday"
    }else {
        var format1 = SimpleDateFormat("dd MMMM yyyy")
        val formatted = format1.format(timePost.getTime());

        v.text = formatted
    }
}

fun getFileSize(size: Long): String {
    if (size <= 0)
        return "0"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()

    return DecimalFormat("#,##0.#").format(
        size / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}