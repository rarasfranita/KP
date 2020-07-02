package com.example.lotus.models

import android.net.Uri

public class MediaPost {
    val mediaPath: Uri
    val type: String

    constructor(mediaPath: Uri, type: String) {
        this.mediaPath = mediaPath
        this.type = type
    }
}