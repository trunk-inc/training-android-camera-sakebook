package com.sakebook.android.sample.adobecamera.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.sakebook.android.sample.adobecamera.BuildConfig;
import com.sakebook.android.sample.adobecamera.activities.MainActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sakemotoshinya on 16/03/07.
 */
public final class Util {

    private Util() {
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video
     * https://developer.android.com/intl/ja/guide/topics/media/camera.html#saving-media
     * */
    public static Uri getOutputMediaFileUri(Context context, int type){
        File file = getOutputMediaFile(type);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Uri.fromFile(file);
        } else  {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        }
    }

    /** Create a File for saving an image or video
     * https://developer.android.com/intl/ja/guide/topics/media/camera.html#saving-media
     * */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), MainActivity.TAG);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(MainActivity.TAG, "failed to create directory");
                return null;
            }
        }
        Log.d(MainActivity.TAG, "mediaStorageDir: " + mediaStorageDir.getAbsolutePath());

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
