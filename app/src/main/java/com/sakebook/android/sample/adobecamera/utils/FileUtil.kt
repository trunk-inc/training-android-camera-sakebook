package com.sakebook.android.sample.adobecamera.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import android.text.format.DateFormat
import android.util.Log

import java.io.File
import java.util.Date

/**
 * Created by sakemotoshinya on 16/03/07.
 */
object FileUtil {

    val MEDIA_TYPE_IMAGE = 1
    val MEDIA_TYPE_VIDEO = 2
    private const val TAG = "FileUtil"

    /** Create a file Uri for saving an image or video
     * https://developer.android.com/intl/ja/guide/topics/media/camera.html#saving-media
     */
    fun getOutputMediaFileUri(context: Context, type: Int): Uri {
        val file = getOutputMediaFile(type)
        return when(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            true -> Uri.fromFile(file)
            false -> FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        }
    }

    /** Create a File for saving an image or video
     * https://developer.android.com/intl/ja/guide/topics/media/camera.html#saving-media
     */
    private fun getOutputMediaFile(type: Int): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        val mediaStorageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.w(TAG, "failed to create directory")
                return null
            }
        }
        // Create a media file name
        val timeStamp = DateFormat.format("yyyyMMdd_HHmmss", Date())
        return when (type) {
            MEDIA_TYPE_IMAGE -> File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
            MEDIA_TYPE_VIDEO -> File("${mediaStorageDir.path}${File.separator}VID_$timeStamp.mp4")
            else -> null
        }.apply {
            Log.i(TAG, "Create file path: ${this?.absolutePath}")
        }
    }
}
