package camera;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.DualTVL1OpticalFlow;
import org.opencv.video.Video;

import java.util.List;

/**
 * Created by Srijan on 04/04/2017.
 */
public class OpticalFlow {

    private final DualTVL1OpticalFlow flowAlgorithm;

    private static final boolean DEBUG = false;

    public OpticalFlow() {
        flowAlgorithm = Video.createOptFlow_DualTVL1();

        flowAlgorithm.setTau(0.25);
        flowAlgorithm.setLambda(0.025);
        flowAlgorithm.setEpsilon(0.05);
        flowAlgorithm.setTheta(0.3);
        flowAlgorithm.setScaleStep(0.5);
        flowAlgorithm.setScalesNumber(4);
        flowAlgorithm.setWarpingsNumber(3);
    }

    public Mat calculateFlow(Mat left, Mat right) {
        Mat flow = new Mat();
        Mat leftGray = ImageUtils.rgb2gray(left, 0.125);
        Mat rightGray = ImageUtils.rgb2gray(right, 0.125);
        flowAlgorithm.calc(leftGray, rightGray, flow);
        Imgproc.resize(flow, flow, new Size(), 8, 8, Imgproc.INTER_LINEAR);

        if (DEBUG) {
            /*List<Mat> channels = splitChannels(flow, 2);
            Mat normalisedFlowX = abs(channels.get(0));
            Mat normalisedFlowY = abs(channels.get(1));

            ImageUtils.save("testX.jpg", normalisedFlowX);
            ImageUtils.save("testY.jpg", normalisedFlowY);*/

            List<Mat> channels = ImageUtils.splitChannels(flow, 2);
            channels.add(Mat.zeros(channels.get(0).size(), channels.get(0).type()));

            Mat rgbFlow = new Mat();
            Core.merge(channels, rgbFlow);
            ImageUtils.save("optFlow.png", rgbFlow);
        }

        return flow;
    }
}
