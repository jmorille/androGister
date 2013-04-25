package eu.ttbox.androgister.ui.admin.product.photo;

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.ContactsContract.DisplayPhoto;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;
import eu.ttbox.androgister.R;

public abstract class PhotoSelectionHandler implements OnClickListener {

    private static final String TAG = "PhotoSelectionHandler";

    private static final int REQUEST_CODE_CAMERA_WITH_DATA = 1001;
    private static final int REQUEST_CODE_PHOTO_PICKED_WITH_DATA = 1002;

    protected final Context mContext;
    private final View mPhotoView;
    private final int mPhotoMode;
    private final int mPhotoPickSize;
    private final boolean mIsDirectoryContact;
    private ListPopupWindow mPopup;

    public PhotoSelectionHandler(Context context, View photoView, int photoMode, boolean isDirectoryContact) {
        mContext = context;
        mPhotoView = photoView;
        mPhotoMode = photoMode;
        mIsDirectoryContact = isDirectoryContact;
        mPhotoPickSize = getPhotoPickSize();
    }

    public void destroy() {
        if (mPopup != null) {
            mPopup.dismiss();
        }
    }

    public abstract PhotoActionListener getListener();

    @Override
    public void onClick(View v) {
        final PhotoActionListener listener = getListener();
        if (listener != null) {
            Log.d(TAG, "Create Photo Popup Menu");
            mPopup = PhotoActionPopup.createPopupMenu(mContext, mPhotoView, listener, mPhotoMode);
            mPopup.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    listener.onPhotoSelectionDismissed();
                }
            });
            Log.d(TAG, "Create Photo Popup Menu : Show");
            mPopup.show();

        }
    }

    /**
     * Attempts to handle the given activity result. Returns whether this
     * handler was able to process the result successfully.
     * 
     * @param requestCode
     *            The request code.
     * @param resultCode
     *            The result code.
     * @param data
     *            The intent that was returned.
     * @return Whether the handler was able to process the result.
     */
    public boolean handlePhotoActivityResult(int requestCode, int resultCode, Intent data) {
        final PhotoActionListener listener = getListener();
        Log.d(TAG, "handlePhotoActivityResult requestCode : " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            // Photo was chosen (either new or existing from gallery), and
            // cropped.
            case REQUEST_CODE_PHOTO_PICKED_WITH_DATA: {
                final String path = ProductPhotoUtils.pathForCroppedPhoto(mContext, listener.getCurrentPhotoFile());
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Log.d(TAG, "Bitmap decodeFile  : " + path + " ==> Bitmap : " + bitmap);
                listener.onPhotoSelected(bitmap);
                return true;
            }
            // Photo was successfully taken, now crop it.
            case REQUEST_CODE_CAMERA_WITH_DATA: {
                doCropPhoto(listener.getCurrentPhotoFile());
                return true;
            }
            }
        }
        return false;
    }

    /** Used by subclasses to delegate to their enclosing Activity or Fragment. */
    protected abstract void startPhotoActivity(Intent intent, int requestCode, String photoFile);

    /**
     * Sends a newly acquired photo to Gallery for cropping
     */
    private void doCropPhoto(String fileName) {
        Log.d(TAG, "doCropPhoto for : " + fileName);
        try {
            // Obtain the absolute paths for the newly-taken photo, and the
            // destination
            // for the soon-to-be-cropped photo.
            final String newPath = ProductPhotoUtils.pathForNewCameraPhoto(fileName);
            final String croppedPath = ProductPhotoUtils.pathForCroppedPhoto(mContext, fileName);

            Log.d(TAG, "doCropPhoto newPath : " + newPath);
            Log.d(TAG, "doCropPhoto croppedPath : " + croppedPath);

            // Add the image to the media store
            MediaScannerConnection.scanFile(mContext, new String[] { newPath }, new String[] { null }, null);

            // Launch gallery to crop the photo
            final Intent intent = getCropImageIntent(newPath, croppedPath);
            startPhotoActivity(intent, REQUEST_CODE_PHOTO_PICKED_WITH_DATA, fileName);
        } catch (Exception e) {
            Log.e(TAG, "Cannot crop image", e);
            Toast.makeText(mContext, R.string.photoPickerNotFoundText, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Should initiate an activity to take a photo using the camera.
     * 
     * @param photoFile
     *            The file path that will be used to store the photo. This is
     *            generally what should be returned by
     *            {@link PhotoSelectionHandler.PhotoActionListener#getCurrentPhotoFile()}
     *            .
     */
    private void startTakePhotoActivity(String photoFile) {
        final Intent intent = getTakePhotoIntent(photoFile);
        startPhotoActivity(intent, REQUEST_CODE_CAMERA_WITH_DATA, photoFile);
    }

    /**
     * Should initiate an activity pick a photo from the gallery.
     * 
     * @param photoFile
     *            The temporary file that the cropped image is written to before
     *            being stored by the content-provider.
     *            {@link PhotoSelectionHandler#handlePhotoActivityResult(int, int, Intent)}
     *            .
     */
    private void startPickFromGalleryActivity(String photoFile) {
        final Intent intent = getPhotoPickIntent(photoFile);
        startPhotoActivity(intent, REQUEST_CODE_PHOTO_PICKED_WITH_DATA, photoFile);
    }

    private int getPhotoPickSize() {
        return 256;
    }

    /**
     * Constructs an intent for picking a photo from Gallery, cropping it and
     * returning the bitmap.
     */
    private Intent getPhotoPickIntent(String photoFile) {
        final String croppedPhotoPath = ProductPhotoUtils.pathForCroppedPhoto(mContext, photoFile);
        final Uri croppedPhotoUri = Uri.fromFile(new File(croppedPhotoPath));
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        ProductPhotoUtils.addGalleryIntentExtras(intent, croppedPhotoUri, mPhotoPickSize);
        return intent;
    }

    /**
     * Constructs an intent for image cropping.
     */
    private Intent getCropImageIntent(String inputPhotoPath, String croppedPhotoPath) {
        final Uri inputPhotoUri = Uri.fromFile(new File(inputPhotoPath));
        final Uri croppedPhotoUri = Uri.fromFile(new File(croppedPhotoPath));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputPhotoUri, "image/*");
        ProductPhotoUtils.addGalleryIntentExtras(intent, croppedPhotoUri, mPhotoPickSize);
        return intent;
    }

    /**
     * Constructs an intent for capturing a photo and storing it in a temporary
     * file.
     */
    private static Intent getTakePhotoIntent(String fileName) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        final String newPhotoPath = ProductPhotoUtils.pathForNewCameraPhoto(fileName);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(newPhotoPath)));
        return intent;
    }

    public abstract class PhotoActionListener implements PhotoActionPopup.Listener {
        @Override
        public void onUseAsPrimaryChosen() {
            // No default implementation.
        }

        @Override
        public void onRemovePictureChosen() {
            // No default implementation.
        }

        @Override
        public void onTakePhotoChosen() {
            try {
                // Launch camera to take photo for selected contact
                startTakePhotoActivity(ProductPhotoUtils.generateTempPhotoFileName());
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, R.string.photoPickerNotFoundText, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onPickFromGalleryChosen() {
            try {
                // Launch picker to choose photo for selected contact
                startPickFromGalleryActivity(ProductPhotoUtils.generateTempPhotoFileName());
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, R.string.photoPickerNotFoundText, Toast.LENGTH_LONG).show();
            }
        }

        /**
         * Called when the user has completed selection of a photo.
         * 
         * @param bitmap
         *            The selected and cropped photo.
         */
        public abstract void onPhotoSelected(Bitmap bitmap);

        /**
         * Gets the current photo file that is being interacted with. It is the
         * activity or fragment's responsibility to maintain this in saved
         * state, since this handler instance will not survive rotation.
         */
        public abstract String getCurrentPhotoFile();

        /**
         * Called when the photo selection dialog is dismissed.
         */
        public abstract void onPhotoSelectionDismissed();
    }

}
