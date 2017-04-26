package camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import srijanparmeshwar.dolly.RenderActivity;

/**
 * Handles the current state of the application.
 */
public class ImageState {
    private final Activity host;
    private final ImageView previewA;
    private final ImageView previewB;
    private final ProgressBar progressBar;

    private int progress;
    public static byte[] frameA;
    public static byte[] frameB;

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    public ImageState(Activity host, ImageView leftPreview, ImageView rightPreview, ProgressBar progressBar) {
        this.host = host;
        this.previewA = leftPreview;
        this.previewB = rightPreview;
        this.progressBar = progressBar;

        // Initialise state as prepared for capture of left view.
        this.progress = LEFT;
    }

    public void processFrame(byte[] data) {
        ImageUtils.save(BitmapFactory.decodeByteArray(data, 0, data.length));

        // Update UI and state.
        switch (progress) {
            case LEFT:
                handleFrameA(data);
                progress = RIGHT;
                break;
            case RIGHT:
                handleFrameB(data);
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
        setVisibility(previewA, false);
        setVisibility(previewB, false);
        setVisibility(progressBar, false);
    }

    private void showA() {
        setVisibility(previewA, true);
    }

    private void showB() {
        setVisibility(previewB, true);
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

    private void handleFrameA(byte[] frame) {
        // Update left frame.
        frameA = frame;
        updatePreview(previewA, ImageUtils.getPreview(frameA));
        showA();
    }

    private void handleFrameB(byte[] frame) {
        // Update right frame.
        frameB = frame;
        updatePreview(previewB, ImageUtils.getPreview(frameB));
        showB();

        // Show loading bar.
        showProgress();

        Intent renderIntent = new Intent(host, RenderActivity.class);
        host.startActivity(renderIntent);

        // Hide loading bar and left and right previews on completion.
        hideAll();
    }
}
