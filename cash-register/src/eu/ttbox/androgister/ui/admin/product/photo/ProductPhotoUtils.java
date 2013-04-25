package eu.ttbox.androgister.ui.admin.product.photo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class ProductPhotoUtils {

    private static final String TAG = "ProductPhotoUtils";

    private static final String PHOTO_DATE_FORMAT = "'IMG'_yyyyMMdd_HHmmss";
    private static final String NEW_PHOTO_DIR_PATH =
            Environment.getExternalStorageDirectory() + "/DCIM/Camera";


    /**
     * Generate a new, unique file to be used as an out-of-band communication
     * channel, since hi-res Bitmaps are too big to serialize into a Bundle.
     * This file will be passed to other activities (such as the gallery/camera/cropper/etc.),
     * and read by us once they are finished writing it.
     */
    public static File generateTempPhotoFile(Context context) {
        return new File(pathForCroppedPhoto(context, generateTempPhotoFileName()));
    }

    public static String pathForCroppedPhoto(Context context, String fileName) {
        //http://developer.android.com/reference/android/content/Context.html#getExternalCacheDir%28%29
//        Environment.getExternalStorageDirectory()
        final File dir = new File(context.getExternalCacheDir() + "/tmp");
        dir.mkdirs();
        final File f = new File(dir, fileName);
        String result =  f.getAbsolutePath();
        Log.d(TAG, "pathForCroppedPhoto  : " + result);
        return result;
    }

    public static String pathForNewCameraPhoto(String fileName) {
        final File dir = new File(NEW_PHOTO_DIR_PATH);
        dir.mkdirs();
        final File f = new File(dir, fileName);
        String result =  f.getAbsolutePath();
        Log.d(TAG, "pathForNewCameraPhoto  : " + result);
        return result;
    }

    public static String generateTempPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PHOTO_DATE_FORMAT);
        String result = "ProductPhoto-" + dateFormat.format(date) + ".jpg"; 
        Log.d(TAG, "pathForNewCameraPhoto  : " + result);
        return result;
    }

    /**
     * Creates a byte[] containing the PNG-compressed bitmap, or null if
     * something goes wrong.
     */
    public static byte[] compressBitmap(Bitmap bitmap) {
        final int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        final ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            Log.w(TAG, "Unable to serialize photo: " + e.toString());
            return null;
        }
    }

    /**
     * Adds common extras to gallery intents.
     *
     * @param intent The intent to add extras to.
     * @param croppedPhotoUri The uri of the file to save the image to.
     * @param photoSize The size of the photo to scale to.
     */
    public static void addGalleryIntentExtras(Intent intent, Uri croppedPhotoUri, int photoSize) {
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", photoSize);
        intent.putExtra("outputY", photoSize);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, croppedPhotoUri);
    }
    
}
