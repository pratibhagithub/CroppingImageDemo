package com.imagedemo;

/**
 * Created by pratibha on 3/2/16.
 */
public interface OnPictureSaveListner {
    public void onPictureSavedSuccess(String filePath);

    public void onPictureSavedFailed(String msg);
}

