package com.imagedemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ProgressBar;


import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by pratibha on 3/2/16.
 */
public class SaveImageAsynctask extends AsyncTask<String, Void, String> {
    private Bitmap bitmapImage;
    private String mFileName;
    private OnPictureSaveListner onPictureSaveListner = null;
    private boolean isSave = false;
    ProgressBar progressBar;
    Activity activity;
    private ProgressDialog pd;
    Context mContext;
    String FILEPATH;


    public SaveImageAsynctask(final Bitmap bitmapImage, String fName, OnPictureSaveListner onPictureSaveListner,Activity activity) {
        this.activity = (activity) ;
        this.onPictureSaveListner = onPictureSaveListner;
        this.mFileName = fName;
        this.bitmapImage = bitmapImage;
    }

    @Override
    protected void onPreExecute() {
        //progressBar=new ProgressBar(activity);
        //progressBar.setVisibility(View.VISIBLE);
        pd = new ProgressDialog(activity);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        pd.show();

        super.onPreExecute();
        System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHH In Save Asynctask");
    }

    @Override
    protected String doInBackground(String... strings) {
        isSave = saveImage(bitmapImage);
        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (isSave) {
            System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHH In Save Asynctask Success");
            pd.dismiss();
            onPictureSaveListner.onPictureSavedSuccess(FILEPATH);
        } else {
            System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHH In Save Asynctask FAiled");
            pd.dismiss();
            onPictureSaveListner.onPictureSavedFailed("Sorry cant save file");
        }
    }

    private boolean saveImage(final Bitmap bitmapImage) {
        String mediaStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "DEMO";
        if (!new File(mediaStorageDir).exists()) {
            new File(mediaStorageDir).mkdirs();
        } else {

        }

        File mediaFile;
        String mFilePath = mediaStorageDir + "/" + mFileName + ".jpg";
        mediaFile = new File(mFilePath);
        if (mediaFile.exists()) {
            mediaFile.delete();
            try {
                mediaFile.createNewFile();

            } catch (Exception ex) {

            }

        } else {
            try {
                mediaFile.createNewFile();

            } catch (Exception ex) {

            }
        }
        try {
            FileOutputStream out = new FileOutputStream(mediaFile);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            System.out.println("calling save bitmap try");

            out.flush();
            out.close();
            isSave = true;

        } catch (Exception e) {
            System.out.println("calling  bitmap catch");
            e.printStackTrace();
            isSave = false;
        }
        FILEPATH = mediaFile.getAbsolutePath();

        return isSave;
    }
}
