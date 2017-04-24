package camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.cameraview.CameraView;

import org.opencv.core.Mat;

import srijanparmeshwar.dolly.RenderActivity;

/**
 * Handles the current state of the application.
 */
public class ImageState {
    private final Activity host;
    private final ImageView leftPreview;
    private final ImageView rightPreview;
    private final ProgressBar progressBar;

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

        // Initialise state as prepared for capture of left view.
        this.progress = LEFT;
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
        // Update left frame.
        leftFrame = frame;
        updatePreview(leftPreview, ImageUtils.convertToBitmap(ImageUtils.preview(leftFrame)));
        showLeft();
    }

    private void handleRightFrame(Mat frame) {
        // Update right frame.
        rightFrame = frame;
        updatePreview(rightPreview, ImageUtils.convertToBitmap(ImageUtils.preview(rightFrame)));
        showRight();

        // Show loading bar.
        showProgress();

        // Render sequence.
        Renderer.render(leftFrame.getNativeObjAddr(), rightFrame.getNativeObjAddr(), ImageUtils.getVideoPath());

        // Hide loading bar and left and right previews on completion.
        hideAll();

        host.startActivity(new Intent(host, RenderActivity.class));
    }
}
