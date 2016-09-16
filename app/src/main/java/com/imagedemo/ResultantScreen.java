package com.imagedemo;
/***
 * Class to show the cropped image
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class ResultantScreen extends AppCompatActivity {
private int RESULTFROMRESULTSNTSCREEN=51;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultant_screen);
        ImageView ivCoverPic=(ImageView)  findViewById(R.id.iv_result);
        String coverPicPathName =getIntent().getStringExtra("CROPPEDIMAGEPATH");
        File image = new File(coverPicPathName);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        ivCoverPic.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULTFROMRESULTSNTSCREEN);
        super.onBackPressed();
    }
}
