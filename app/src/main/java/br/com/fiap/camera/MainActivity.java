package br.com.fiap.camera;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button btnTakePic;
    ImageView imageView;
    String pathToFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTakePic = findViewById(R.id.btnTakePic);
        if(Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPictureTakeAction();
            }
        });
        imageView = findViewById(R.id.image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == 1) {

                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                if(bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }else {
                    Uri selectImage = data.getData();
                    imageView.setImageURI(selectImage);
                }
            }
        }


    }

    private void dispatchPictureTakeAction() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePic.putExtra("camera", 0);

        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        galleryintent.putExtra("storage", 1);


        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        pickIntent.putExtra("galery", 2);

        if(takePic.resolveActivity(getPackageManager()) != null) {

            try {
                File photoFile = null;
                photoFile = createPhotoFile();

                if(photoFile != null) {
                    pathToFile = photoFile.getAbsolutePath();
                    Uri photoUri = FileProvider.getUriForFile(MainActivity.this, "br.com.fiap.camera.fileprovider", photoFile);
                    takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                    Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                    chooser.putExtra(Intent.EXTRA_INTENT, galleryintent);
                    chooser.putExtra(Intent.EXTRA_TITLE, "Select:");

                    Intent[] intentArray = {pickIntent, takePic};
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                    Bundle extras = new Bundle();

                    startActivityForResult(chooser, 1);
                }

            }catch (Exception e ) {

            }
        }
    }

    private File createPhotoFile() {
        String nane = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(nane, ".jpg", storageDir);
        }catch (IOException e) {
            Log.d("mylog", "Excep: "+ e.toString());
            e.printStackTrace();
        }
        return image;
    }
}
