package camera;

import android.util.Log;

import com.google.android.cameraview.CameraView;

import srijanparmeshwar.dolly.HandlerUtils;

/**
 * Handles CameraView callbacks.
 */
public class CameraCallback extends CameraView.Callback {

    private final ImageState imageState;
    private static final String TAG = "Camera";

    public CameraCallback(ImageState imageState) {
        this.imageState = imageState;

        // Create directory for app.
        ImageUtils.prepareDirectory();
    }

    @Override
    public void onCameraOpened(CameraView cameraView) {
        Log.d(TAG, "Camera opened.");
    }
    @Override
    public void onCameraClosed(CameraView cameraView) {
        Log.d(TAG, "Camera closed.");
    }

    @Override
    public void onPictureTaken(CameraView cameraView, final byte[] data) {
        // Process frame in background.
        HandlerUtils.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (ImageUtils.prepareDirectory()) {
                    imageState.processFrame(data);
                }
            }
        });
    }

}
