package com.sakebook.android.sample.adobecamera.activities;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.sakebook.android.sample.adobecamera.R;
import com.sakebook.android.sample.adobecamera.utils.Util;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CAMERA = 100;
    public static final int REQUEST_CODE_EDITOR = 200;

    public static final String TAG = "AdobeCamera";

    private Button button;
    private ImageView image;
    private Uri fileUri;
    private Uri editedFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.image_result);
        image.setOnClickListener(v -> {
            shareImage();
        });
        button = (Button) findViewById(R.id.button_launch_camera);
        button.setOnClickListener(v -> {
            launchCamera();
        });
    }

    private void launchEditor(Uri uri) {
//        Uri imageUri = Uri.parse("https://upload.wikimedia.org/wikipedia/en/2/24/Lenna.png");
        Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
                .setData(uri)
                .build();
        startActivityForResult(imageEditorIntent, REQUEST_CODE_EDITOR);
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = Util.getOutputMediaFileUri(Util.MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, REQUEST_CODE_CAMERA);

//        String filename = System.currentTimeMillis() + ".jpg";
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, filename);
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        imageUri = getContentResolver()
//                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        startActivityForResult(intentCamera, 2);
    }

    /**
     *
     * https://developer.android.com/intl/ja/training/sharing/send.html
     * */
    private void shareImage() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, editedFileUri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
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
//                    Uri uri = data.getData();
                    Log.d(TAG, "fileUri: " + fileUri);
                    launchEditor(fileUri);
                    break;
            }
        }
    }
}
