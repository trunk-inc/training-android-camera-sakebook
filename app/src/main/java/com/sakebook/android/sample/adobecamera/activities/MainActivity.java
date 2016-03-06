package com.sakebook.android.sample.adobecamera.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.sakebook.android.sample.adobecamera.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static final int REQUEST_CODE_CAMERA = 100;
    public static final int REQUEST_CODE_EDITOR = 200;

    private Button button;
    private ImageView image;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.image_result);
        button = (Button) findViewById(R.id.button_launch_camera);
        button.setOnClickListener(v -> {
            // TODO launch camera
            launchCamera();

        });
    }

    private void launchEditor(Uri uri) {
//        Uri imageUri = Uri.parse("https://upload.wikimedia.org/wikipedia/en/2/24/Lenna.png");
        Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
                .setData(uri)
                .build();
        startActivityForResult(imageEditorIntent, 1);
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_EDITOR:
                    Uri editedImageUri = data.getData();
                    image.setImageURI(editedImageUri);
                    break;
                case REQUEST_CODE_CAMERA:
//                    Uri uri = data.getData();
                    launchEditor(fileUri);
                    break;
            }
        }
    }

    /**
     * https://developer.android.com/intl/ja/guide/topics/media/camera.html#saving-media
     * */


    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

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
