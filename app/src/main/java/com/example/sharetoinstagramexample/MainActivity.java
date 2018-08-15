package com.example.sharetoinstagramexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.share_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String backgroundFilePath = loadFileFromAssets("video.mp4");

                // use sticker again to test image instead
                // String backgroundFilePath = loadFileFromAssets("sticker.jpg");

                String stickerFilePath = loadFileFromAssets("sticker.jpg");

                Uri backgroundUri = grantAccessToFile(backgroundFilePath);
                Uri stickerUri = grantAccessToFile(stickerFilePath);

                shareToInstagram(backgroundUri, stickerUri);
            }
        });
    }

    private void shareToInstagram(Uri background, Uri sticker) {
        // Instantiate implicit intent with ADD_TO_STORY action,
        // background asset, sticker asset, and attribution link
        Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndTypeAndNormalize(background, getMimeType(background));
        intent.putExtra("interactive_asset_uri", sticker);

        // Instantiate activity and verify it will resolve implicit intent
        grantUriPermission("com.instagram.android", sticker, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (getPackageManager().resolveActivity(intent, 0) != null) {
            startActivityForResult(intent, 0);
        }
    }

    public String getMimeType(Uri uri) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(String.valueOf(uri));
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private String loadFileFromAssets(String filename) {
        // get cache
        String filePath = getApplicationContext().getExternalCacheDir() + File.separator + filename;

        File outputFile = new File(filePath);

        try {
            InputStream inputStream = getAssets().open(filename);
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

            byte[] bytes = new byte[inputStream.available()];
            for (int read = inputStream.read(bytes); read != -1; read = inputStream.read(bytes)) {
                fileOutputStream.write(bytes, 0, read);
            }

            inputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();

            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Uri grantAccessToFile(String filePath) {
        return FileProvider.getUriForFile(getApplicationContext(), "com.example.sharetoinstagramexample.fileprovider", new File(filePath));
    }
}