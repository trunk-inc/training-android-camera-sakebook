package com.sakebook.android.sample.adobecamera.activities;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.sakebook.android.sample.adobecamera.R;
import com.sakebook.android.sample.adobecamera.utils.Util;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CAMERA = 100;
    public static final int REQUEST_CODE_EDITOR = 200;
    public static final int REQUEST_CODE_GALLERY = 300;

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
        findViewById(R.id.button_launch_camera).setOnClickListener(v -> launchCamera());
        findViewById(R.id.button_launch_gallery).setOnClickListener(v -> launchGallery());

    }

    private void launchEditor(Uri uri) {
        Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
                .setData(uri)
                .build();
        startActivityForResult(imageEditorIntent, REQUEST_CODE_EDITOR);
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Util.getOutputMediaFileUri(Util.MEDIA_TYPE_IMAGE); // create a file to save the image
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_EDITOR:
                    editedFileUri = data.getData();
                    Log.d(TAG, "editedFileUri: " + editedFileUri);
                    image.setImageURI(editedFileUri);
                    break;
                case REQUEST_CODE_CAMERA:
                    Log.d(TAG, "fileUri: " + fileUri);
                    launchEditor(fileUri);
                    break;
                case REQUEST_CODE_GALLERY:
                    Log.d(TAG, "fileUri: " + fileUri);
                    fileUri = data.getData();
                    launchEditor(fileUri);
                    break;
            }
        }
    }
}
