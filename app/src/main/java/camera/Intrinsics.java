package camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.Log;

/**
 * Created by Srijan on 04/04/2017.
 */
public class Intrinsics {
    private float[] values;
    private static Intrinsics intrinsics;

    private static final String TAG = "Intrinsics";
    private static final float[] NEXUS_5_INTRINSICS = new float[] {2854, 2854, 0, 1224, 1632};

    private Intrinsics(Activity host) {
        this.values = NEXUS_5_INTRINSICS;
        try {
            CameraManager manager = (CameraManager) host.getSystemService(Context.CAMERA_SERVICE);
            for (String cameraID : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraID);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    float[] apiValues = characteristics.get(CameraCharacteristics.LENS_INTRINSIC_CALIBRATION);
                    if (apiValues == null) Log.e(TAG, "No intrinsic parameters found using camera2.");
                    else {
                        this.values = apiValues;
                    }
                }
            }
        } catch(CameraAccessException cameraAccessException) {
            Log.e(TAG, cameraAccessException.getMessage());
        }
    }

    public static float[] getIntrinsics(Activity host) {
        if (intrinsics == null) intrinsics = new Intrinsics(host);
        return intrinsics.values;
    }
}
