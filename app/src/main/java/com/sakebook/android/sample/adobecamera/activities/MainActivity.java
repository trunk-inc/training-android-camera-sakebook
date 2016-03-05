package com.sakebook.android.sample.adobecamera.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.sakebook.android.sample.adobecamera.R;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.image_result);
        button = (Button) findViewById(R.id.button_launch_camera);
        button.setOnClickListener(v -> {
            // TODO launch camera
            Uri imageUri = Uri.parse("https://upload.wikimedia.org/wikipedia/en/2/24/Lenna.png");
            Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
                    .setData(imageUri)
                    .build();
            startActivityForResult(imageEditorIntent, 1);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Uri editedImageUri = data.getData();
                    image.setImageURI(editedImageUri);
                    break;
            }
        }
    }
}
