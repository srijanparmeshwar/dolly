package camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.opencv.core.Mat;

import java.util.List;

/**
 * Created by Srijan on 04/04/2017.
 */
public class ImageState {
    private final Activity host;
    private final ImageView leftPreview;
    private final ImageView rightPreview;
    private final ProgressBar progressBar;
    private final OpticalFlow opticalFlow;
    private final Stereo stereo;

    private int progress;
    private Mat leftFrame;
    private Mat rightFrame;

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private static final String TAG = "ImageState";

    public ImageState(Activity host, ImageView leftPreview, ImageView rightPreview, ProgressBar progressBar) {
        this.host = host;
        this.leftPreview = leftPreview;
        this.rightPreview = rightPreview;
        this.progressBar = progressBar;
        this.opticalFlow = new OpticalFlow();
        this.stereo = new Stereo(host);

        this.progress = LEFT;

        this.leftPreview.setAlpha(0.65f);
        this.rightPreview.setAlpha(0.65f);
    }

    public void processFrame(byte[] data) {
        // Decode and save image.
        Mat frame = ImageUtils.decode(data);
        ImageUtils.save(frame);

        // Update UI and state.
        switch (progress) {
            case LEFT:
                handleLeftFrame(frame);
                progress = RIGHT;
                break;
            case RIGHT:
                handleRightFrame(frame);
                progress = LEFT;
                break;
            default:
                break;
        }
    }

    private void setVisibility(final View view, final boolean visibility) {
        host.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    private void hideAll() {
        setVisibility(leftPreview, false);
        setVisibility(rightPreview, false);
        setVisibility(progressBar, false);
    }

    private void showLeft() {
        setVisibility(leftPreview, true);
    }

    private void showRight() {
        setVisibility(rightPreview, true);
    }

    private void showProgress() {
        setVisibility(progressBar, true);
    }

    private void updatePreview(final ImageView view, final Bitmap bitmap) {
        host.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setImageBitmap(bitmap);
            }
        });
    }

    private void handleLeftFrame(Mat frame) {
        leftFrame = frame;
        updatePreview(leftPreview, ImageUtils.convertToBitmap(ImageUtils.preview(leftFrame)));
        showLeft();
    }

    private void handleRightFrame(Mat frame) {
        rightFrame = frame;
        updatePreview(rightPreview, ImageUtils.convertToBitmap(ImageUtils.preview(rightFrame)));
        showRight();
        showProgress();
        Mat flow = opticalFlow.calculateFlow(leftFrame, rightFrame);
        List<Mat> extrinsics = stereo.calculateExtrinsics(flow);
        hideAll();
    }
}
