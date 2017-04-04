package camera;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.util.Log;

import com.google.android.cameraview.CameraView;

import srijanparmeshwar.dolly.HandlerUtils;

/**
 * Created by Srijan on 03/04/2017.
 */
public class CameraCallback extends CameraView.Callback {

    private final ImageState imageState;

    private static final String TAG = "Camera";

    public CameraCallback(ImageState imageState) {
        this.imageState = imageState;

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
        HandlerUtils.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (ImageUtils.prepareDirectory()) {
                    imageState.processFrame(data);
                }
            }
        });
    }

    /*
    private void processFrame(byte[] data) {
        // Decode data into OpenCV frame.

        imageState.updateState(frame);

        if (STATE == RIGHT) {
            rightImage = frame;
            STATE = LEFT;

            setImageCounter(2);
            setProgressVisibility(View.VISIBLE);
            processPair();
            setProgressVisibility(View.INVISIBLE);
            setImageCounter(0);
        } else {
            setImageCounter(1);
            leftImage = frame;
            STATE = RIGHT;
        }
        //File file = new File(directoryPath, filename);
        /*try (OutputStream os = new FileOutputStream(file)) {
            os.write(data);
            os.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }*/

    /*
    private static void processPair() {
        Mat flow = new Mat();
        Mat grayLeft = new Mat();
        Mat grayRight = new Mat();
        grayLeft.convertTo(grayLeft, CvType.CV_32FC3);
        grayRight.convertTo(grayRight, CvType.CV_32FC3);
        Imgproc.cvtColor(leftImage, grayLeft, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(rightImage, grayRight, Imgproc.COLOR_RGB2GRAY);
        Imgproc.resize(grayLeft, grayLeft, new Size(), 0.125, 0.125, Imgproc.INTER_LINEAR);
        Imgproc.resize(grayRight, grayRight, new Size(), 0.125, 0.125, Imgproc.INTER_LINEAR);
        DualTVL1OpticalFlow flowAlgorithm = Video.createOptFlow_DualTVL1();
        flowAlgorithm.setTau(0.25);
        flowAlgorithm.setLambda(0.2);
        flowAlgorithm.setScalesNumber(1);
        flowAlgorithm.setScaleStep(0.5);
        flowAlgorithm.setEpsilon(0.05);
        flowAlgorithm.setTheta(0.3);
        flowAlgorithm.setWarpingsNumber(3);
        flowAlgorithm.calc(grayLeft, grayRight, flow);
        List<Mat> channels = new ArrayList<>();
        channels.add(new Mat());
        channels.add(new Mat());
        Core.split(flow, channels);
        Mat normalisedFlowX = new Mat();
        Mat normalisedFlowY = new Mat();
        Core.convertScaleAbs(channels.get(0), normalisedFlowX);
        Core.convertScaleAbs(channels.get(1), normalisedFlowY);
        Imgcodecs.imwrite(directoryPath + "/testX.jpg", normalisedFlowX);
        Imgcodecs.imwrite(directoryPath + "/testY.jpg", normalisedFlowX);
        Log.i(TAG, "Saved test images.jpg");
    }
    */

}
