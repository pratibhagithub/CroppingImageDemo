package com.imagedemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * This classs helps you to crop the image on selecting
 * from gallery and zooming it in and zooming out
 **/


public class CoverPicZooming extends Activity implements OnPictureSaveListner {
    private TouchImageView zoomCoverImageView;
    static RectF rect;
    private TextView tvSelectImage, tvDone;
    private int COVERPICZoomRESULTCODE = 101;
    private LinearLayout linearLayoutBorder;
    private RelativeLayout rlContainer;
    private int REQUESTFORRESULTSNTSCREEN = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom);
        init();
    }

    private void init() {
        linearLayoutBorder = (LinearLayout) findViewById(R.id.border);
        tvSelectImage = (TextView) findViewById(R.id.txt_selectImage);
        rlContainer = (RelativeLayout) findViewById(R.id.zoomcontainer);
        tvDone = (TextView) findViewById(R.id.txt_done);
        linearLayoutBorder.bringToFront();
        zoomCoverImageView = (TouchImageView) findViewById(R.id.iv_cover_pic_zoom);
        zoomCoverImageView.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {
            @Override
            public void onMove() {
                PointF point = zoomCoverImageView.getScrollPosition();
                rect = zoomCoverImageView.getZoomedRect();
                float currentZoom = zoomCoverImageView.getCurrentZoom();
                boolean isZoomed = zoomCoverImageView.isZoomed();
            }
        });
        tvSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageFromGallery();
            }
        });
        /*saving image */
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImagefromBorder();
            }
        });
    }


    @Override
    public void onPictureSavedSuccess(String filePath) {
        String ImagePath = filePath;
        Intent intent = new Intent(CoverPicZooming.this, ResultantScreen.class);
        intent.putExtra("CROPPEDIMAGEPATH", ImagePath);
        rlContainer.setDrawingCacheEnabled(false);
        startActivityForResult(intent, REQUESTFORRESULTSNTSCREEN);
    }

    @Override
    public void onPictureSavedFailed(String msg) {
        Toast.makeText(CoverPicZooming.this, "Picture cropped failed, please try again", Toast.LENGTH_LONG).show();
    }


    /**
     * Select Image From Gallery
     **/
    public void selectImageFromGallery() {
        Intent intent;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        }
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), COVERPICZoomRESULTCODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * getting selected image path
         **/
        if (requestCode == COVERPICZoomRESULTCODE) {
            if (data != null) {
                /*Uri selectedImage = data.getData();
                zoomCoverImageView.setImageURI(selectedImage);
                rlContainer.setDrawingCacheEnabled(true);*/
                Uri selectedImage = data.getData();
                String coverPicPathName = getPath(selectedImage, CoverPicZooming.this);
                if (coverPicPathName == null) {
                    Toast.makeText(getApplicationContext(), "Invalid Folder", Toast.LENGTH_SHORT).show();
                } else {
                    Bitmap selecteImageBitmap = correctingImageForGallery(coverPicPathName);
                    rlContainer.setDrawingCacheEnabled(true);
                    zoomCoverImageView.setImageBitmap(selecteImageBitmap);
                }
            } else {
                System.out.println("Data is null");
                Toast.makeText(getApplicationContext(), "please retry", Toast.LENGTH_SHORT).show();
            }
        }
        /**
         * coming back from resultant screen
         **/if (requestCode == REQUESTFORRESULTSNTSCREEN) {
            zoomCoverImageView.setImageDrawable(null);
        }


    }

    /**
     * Copping image that lies under the square box
     * on getting its width height , x cordinate, y cordinate
     * and saving it.
     **/
    private void getImagefromBorder() {
        try {
            Bitmap newBitmap = Bitmap.createBitmap(rlContainer.getDrawingCache(), 0, linearLayoutBorder.getTop(), linearLayoutBorder.getWidth(), linearLayoutBorder.getHeight());
            SaveImageAsynctask saveImageAsynctask = new SaveImageAsynctask(newBitmap, "cropped_image", CoverPicZooming.this, CoverPicZooming.this);
            saveImageAsynctask.execute();
        } catch (Exception ex) {
            System.out.println("Exception occured" + ex);
        }
    }

    /**
     * getting path for selected Image from gallery
     **/
    public static String getPath(android.net.Uri uri, Context context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            return uri.getPath();
        } else {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String res = cursor.getString(column_index);
            return res;
        }

    }


    /**
     * rotating the image
     **/
    public static Bitmap correctingImageForGallery(String path) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:

                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:

                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:

                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:

                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:

                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:

                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}
