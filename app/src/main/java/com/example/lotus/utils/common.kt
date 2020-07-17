package com.example.lotus.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.androidnetworking.interfaces.DownloadProgressListener
import com.example.lotus.models.MediaData

fun downloadMedia(medias: ArrayList<MediaData>, context: Context){
    val downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    Log.d("DOWNLOAD PATH", downloadsPath.toString())
    for (media in medias) {
        val fileName = media.link?.removeRange(0, media.link.length-10)

        AndroidNetworking.download(media.link, downloadsPath.toString(), fileName)
            .setTag("downloadTest")
            .setPriority(Priority.MEDIUM)
            .build()
            .setDownloadProgressListener(object : DownloadProgressListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onProgress(
                    bytesDownloaded: Long,
                    totalBytes: Long
                ) {
//                        Log.d("Progress", "downloaded: $bytesDownloaded from total $totalBytes")
//                        val progress = findViewById<TextView>(R.id.progressDownload)
//                        progress.text = "$bytesDownloaded/$totalBytes"
                }
            })
            .startDownload(object : DownloadListener {
                override fun onDownloadComplete() {
                    Log.d("Complete", "TEEE")
                    Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show()
//                        dialog?.dismiss()
                    // do anything after completion
                }

                override fun onError(error: ANError?) {
                    // handle error
                    Log.d("Error download", error?.errorCode.toString())
                    Log.d("Error download", error!!.errorDetail)
                }
            })
    }
}
