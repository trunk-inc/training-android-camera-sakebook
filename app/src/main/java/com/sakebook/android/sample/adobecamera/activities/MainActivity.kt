package com.sakebook.android.sample.adobecamera.activities

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

import com.adobe.creativesdk.aviary.AdobeImageIntent
import com.sakebook.android.sample.adobecamera.R
import com.sakebook.android.sample.adobecamera.utils.CallAction
import com.sakebook.android.sample.adobecamera.utils.FileUtil

class MainActivity : AppCompatActivity() {

    private lateinit var image: ImageView
    private var fileUri: Uri? = null
    private var editedFileUri: Uri? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image = findViewById<ImageView>(R.id.image_result)
        image.setOnClickListener { v ->
            if (editedFileUri == null) {
                Toast.makeText(this, getString(R.string.image_caution), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            shareImage()
        }
        findViewById<Button>(R.id.button_launch_camera).setOnClickListener { v -> checkPermission(CallAction.Camera) }
        findViewById<Button>(R.id.button_launch_gallery).setOnClickListener { v -> checkPermission(CallAction.Gallery) }

        checkPermission(CallAction.None)
    }


    /**
     *
     * Android 6.0以降でのパーミッション対応。
     * パーミッションに問題なければ引数のアクションを起動
     * http://dev.classmethod.jp/etc/android-marshmallow-permission/
     *
     * @param call
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun checkPermission(call: CallAction) {
        // パーミッション対応は不要
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            doAction(call)
            return
        }
        // 許可済み
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            doAction(call)
            return
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showPermissionExplainDialog(call)
            return
        }
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstLaunch = preferences.getBoolean(KEY_FIRST_LAUNCH, true)
        // 初回起動時のみ
        if (isFirstLaunch) {
            // 初めてのPermission要求のときのみ
            preferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            return
        }

        // 初回以外でここに来るときは設定へ促す。
        Snackbar.make(image, getString(R.string.snack_title), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.snack_action)) { v -> openSettings() }
                .show()
    }

    private fun doAction(callAction: CallAction) {
        when (callAction) {
            CallAction.Camera -> launchCamera()
            CallAction.Gallery -> launchGallery()
        }
    }

    private fun launchEditor(uri: Uri?) {
        val imageEditorIntent = AdobeImageIntent.Builder(this)
                .setData(uri)
                .build()
        startActivityForResult(imageEditorIntent, REQUEST_CODE_EDITOR)
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fileUri = FileUtil.getOutputMediaFileUri(this, FileUtil.MEDIA_TYPE_IMAGE) // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri) // set the image file name
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    /**
     * https://developer.android.com/intl/ja/training/sharing/send.html
     */
    private fun shareImage() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, editedFileUri)
        shareIntent.type = "image/jpeg"
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)))
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun showPermissionExplainDialog(call: CallAction) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.dialog_title))
        alertDialogBuilder.setMessage(getString(R.string.dialog_message))
        alertDialogBuilder.setPositiveButton(getString(R.string.dialog_ok)
        ) { dialog, which ->
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    if (call == CallAction.Camera) REQUEST_CODE_CAMERA else REQUEST_CODE_GALLERY)
        }
        alertDialogBuilder.setCancelable(true)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_EDITOR -> {
                    editedFileUri = data?.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI)
                    Log.d(TAG, "editor editedFileUri : " + editedFileUri)
                    image.setImageURI(editedFileUri)
                }
                REQUEST_CODE_CAMERA -> {
                    Log.d(TAG, "camera fileUri: " + fileUri)
                    launchEditor(fileUri)
                }
                REQUEST_CODE_GALLERY -> {
                    fileUri = data?.data
                    Log.d(TAG, "gallery fileUri: " + fileUri)
                    launchEditor(fileUri)
                }
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_CAMERA -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            }
            REQUEST_CODE_GALLERY -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchGallery()
            }
        }
    }

    companion object {

        val REQUEST_CODE_CAMERA = 10
        val REQUEST_CODE_EDITOR = 20
        val REQUEST_CODE_GALLERY = 30
        val KEY_FIRST_LAUNCH = "first_launch"

        val TAG = "AdobeCamera"
    }
}

inline fun <T: View> AppCompatActivity.findViewById(resId: Int): T {
    return findViewById(resId) as T
}
