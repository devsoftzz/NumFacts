package com.devsoftzz.numfacts.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static com.devsoftzz.numfacts.utils.Constants.SD_CARD_DIR_NAME;

public class BitmapIO {

    public static boolean saveBitmapToInternalStorage(Context context, Bitmap bitmap, String filename) {

        //Internal Storage can't accessed by others
        try {
            // Use the compress method on the Bitmap object to write image to the OutputStream
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            // Writing the bitmap to the output stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            //Closing the stream
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
        }
        return false;

    }

    public static String saveBitmapToExternalStorage(Context context, Bitmap bitmap, String filename) {


        //External Storage
        String locationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + SD_CARD_DIR_NAME;

        try {
            // Getting Directory Instance
            File directory = new File(locationPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(locationPath, filename);
            file.createNewFile();

            OutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            MediaScannerConnection.scanFile(context, new String[]{locationPath + "/" + filename}, new String[]{"image/jpeg"}, null);
            return locationPath + "/" + filename;

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
        }

        return null;
    }

    public static Bitmap getImageFromExternalStorage(Context context, String filePath) {

        String locationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + SD_CARD_DIR_NAME;
        Bitmap Image = null;

        // Look for the file on the external storage
        try {
            if (isSDReadable()) {
                Image = BitmapFactory.decodeFile(filePath);
            }
        } catch (Exception e) {
            Log.e("getThumbnail()", e.getMessage());
        }
        return Image;
    }

    public static Bitmap getImageFromInternalStorage(Context context, String filename) {
        Bitmap Image = null;
        // Look for the file on the Internal storage
        try {
            File filePath = context.getFileStreamPath(filename);
            FileInputStream fis = new FileInputStream(filePath);
            Image = BitmapFactory.decodeStream(fis);
        } catch (Exception ex) {
            Log.e("getThumbnail() on ", ex.getMessage());
        }
        return Image;
    }

    private static boolean isSDReadable() {

        boolean mExternalStorageAvailable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
        }
        return mExternalStorageAvailable;
    }

}
