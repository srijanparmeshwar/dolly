package camera;

import android.app.Activity;
import android.media.Image;
import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Srijan on 04/04/2017.
 */
public class Stereo {

    private static final String TAG = "Stereo";
    private final float[] intrinsics;
    private final Mat intrinsicMatrix;

    public Stereo(Activity host) {
        this.intrinsics = Intrinsics.getIntrinsics(host);
        this.intrinsicMatrix = calculateIntrinsics();
    }

    private Mat calculateEssentialMatrix(Mat opticalFlow) {
        int subsamplingRatio = 10;
        int width = opticalFlow.width();
        int height = opticalFlow.height();

        List<Mat> channels = ImageUtils.splitChannels(opticalFlow, 2);
        Mat dx = channels.get(0);
        Mat dy = channels.get(1);

        List<Point> cameraA = new ArrayList<>();
        List<Point> cameraB = new ArrayList<>();

        for (int y = 0; y < height; y += subsamplingRatio) {
            Mat cv_row_dx = dx.row(y);
            Mat cv_row_dy = dy.row(y);

            float[] row_dx = new float[cv_row_dx.cols()];
            float[] row_dy = new float[cv_row_dy.cols()];

            cv_row_dx.get(0, 0, row_dx);
            cv_row_dy.get(0, 0, row_dy);

            for (int x = 0; x < width; x += subsamplingRatio) {
                cameraA.add(new Point(x, y));
                cameraB.add(new Point(x + row_dx[x], y + row_dy[y]));
            }
        }

        MatOfPoint2f cameraAPoints = new MatOfPoint2f();
        cameraAPoints.fromList(cameraA);
        MatOfPoint2f cameraBPoints = new MatOfPoint2f();
        cameraBPoints.fromList(cameraB);

        return Calib3d.findEssentialMat(cameraAPoints, cameraBPoints, calculateIntrinsics());
    }

    private Mat calculateIntrinsics() {
        Mat intrinsicMatrix = new Mat(new Size(3, 3), CvType.CV_32F);
        intrinsicMatrix.put(0, 0, new float[]
                {
                        intrinsics[0], intrinsics[4], intrinsics[2],
                        0, intrinsics[1], intrinsics[3],
                        0, 0, 1
                }
        );
        return intrinsicMatrix;
    }

    public List<Mat> calculateExtrinsics(Mat opticalFlow) {
        Log.d(TAG, "Calculating extrinsics.");

        int subsamplingRatio = 36;
        int width = opticalFlow.width();
        int height = opticalFlow.height();

        List<Mat> channels = ImageUtils.splitChannels(opticalFlow, 2);
        Mat dx = channels.get(0);
        Mat dy = channels.get(1);

        List<Point> cameraA = new ArrayList<>();
        List<Point> cameraB = new ArrayList<>();

        for (int y = 0; y < height; y += subsamplingRatio) {
            Mat cv_row_dx = dx.row(y);
            Mat cv_row_dy = dy.row(y);

            float[] row_dx = new float[cv_row_dx.cols()];
            float[] row_dy = new float[cv_row_dy.cols()];

            cv_row_dx.get(0, 0, row_dx);
            cv_row_dy.get(0, 0, row_dy);

            for (int x = 0; x < width; x += subsamplingRatio) {
                cameraA.add(new Point(x, y));
                cameraB.add(new Point(x + row_dx[x], y + row_dy[x]));
            }
        }

        MatOfPoint2f cameraAPoints = new MatOfPoint2f();
        cameraAPoints.fromList(cameraA);
        MatOfPoint2f cameraBPoints = new MatOfPoint2f();
        cameraBPoints.fromList(cameraB);

        List<Mat> extrinsics = new ArrayList<>();
        Mat R = new Mat();
        Mat t = new Mat();
        Mat essentialMatrix = Calib3d.findEssentialMat(cameraAPoints, cameraBPoints, intrinsicMatrix);
        Calib3d.recoverPose(essentialMatrix, cameraAPoints, cameraBPoints, intrinsicMatrix, R, t);

        Log.d(TAG, R.dump());
        Log.d(TAG, t.dump());

        extrinsics.add(R);
        extrinsics.add(t);

        return extrinsics;
    }
}
