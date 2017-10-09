package com.sakebook.android.sample.adobecamera.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.sakebook.android.sample.adobecamera.R;
import com.sakebook.android.sample.adobecamera.utils.CallAction;
import com.sakebook.android.sample.adobecamera.utils.Util;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CAMERA = 10;
    public static final int REQUEST_CODE_EDITOR = 20;
    public static final int REQUEST_CODE_GALLERY = 30;
    public static final String KEY_FIRST_LAUNCH = "first_launch";

    public static final String TAG = "AdobeCamera";

    private ImageView image;
    private Uri fileUri;
    private Uri editedFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.image_result);
        image.setOnClickListener(v -> {
            if (editedFileUri == null) {
                Toast.makeText(this, getString(R.string.image_caution), Toast.LENGTH_LONG).show();
                return;
            }
            shareImage();
        });
        findViewById(R.id.button_launch_camera).setOnClickListener(v -> checkPermission(CallAction.Camera));
        findViewById(R.id.button_launch_gallery).setOnClickListener(v -> checkPermission(CallAction.Gallery));

        checkPermission(CallAction.None);
    }


    /**
     *
     * Android 6.0以降でのパーミッション対応。
     * パーミッションに問題なければ引数のアクションを起動
     * http://dev.classmethod.jp/etc/android-marshmallow-permission/
     *
     * @param call
     * */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission(CallAction call) {
        // パーミッション対応は不要
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            doAction(call);
            return;
        }
        // 許可済み
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            doAction(call);
            return;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showPermissionExplainDialog(call);
            return;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstLaunch = preferences.getBoolean(KEY_FIRST_LAUNCH, true);
        // 初回起動時のみ
        if (isFirstLaunch) {
            // 初めてのPermission要求のときのみ
            preferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return;
        }

        // 初回以外でここに来るときは設定へ促す。
        Snackbar.make(image, getString(R.string.snack_title), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.snack_action), v -> {
                    openSettings();
                })
                .show();
    }

    private void doAction(CallAction callAction) {
        switch (callAction) {
            case Camera:
                launchCamera();
                break;
            case Gallery:
                launchGallery();
                break;
        }
    }

    private void launchEditor(Uri uri) {
        Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
                .setData(uri)
                .build();
        startActivityForResult(imageEditorIntent, REQUEST_CODE_EDITOR);
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Util.getOutputMediaFileUri(this, Util.MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    /**
     * https://developer.android.com/intl/ja/training/sharing/send.html
     * */
    private void shareImage() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, editedFileUri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void showPermissionExplainDialog(CallAction call) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.dialog_title));
        alertDialogBuilder.setMessage(getString(R.string.dialog_message));
        alertDialogBuilder.setPositiveButton(getString(R.string.dialog_ok),
                (dialog, which) -> {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            call == CallAction.Camera? REQUEST_CODE_CAMERA: REQUEST_CODE_GALLERY);
                });
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_EDITOR:
                    editedFileUri = data.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI);
                    Log.d(TAG, "editor editedFileUri : " + editedFileUri);
                    image.setImageURI(editedFileUri);
                    break;
                case REQUEST_CODE_CAMERA:
                    Log.d(TAG, "camera fileUri: " + fileUri);
                    launchEditor(fileUri);
                    break;
                case REQUEST_CODE_GALLERY:
                    fileUri = data.getData();
                    Log.d(TAG, "gallery fileUri: " + fileUri);
                    launchEditor(fileUri);
                    break;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                }
                break;
            case REQUEST_CODE_GALLERY:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchGallery();
                }
                break;
        }
    }
}
