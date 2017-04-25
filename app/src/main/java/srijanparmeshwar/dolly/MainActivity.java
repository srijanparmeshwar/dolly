package srijanparmeshwar.dolly;

import android.app.Dialog;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.cameraview.CameraView;

import camera.CameraCallback;
import camera.ImageState;

public class MainActivity extends AppCompatActivity {

    private PermissionsHandler permissionsHandler;
    private CameraView cameraView;
    private MediaActionSound shutter;
    private ImageState imageState;
    private FloatingActionButton button;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionsHandler = new PermissionsHandler(this);

        shutter = new MediaActionSound();

        ImageView left = (ImageView) findViewById(R.id.left_preview);
        ImageView right = (ImageView) findViewById(R.id.right_preview);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        cameraView = (CameraView) findViewById(R.id.camera);

        imageState = new ImageState(this, left, right, progressBar);

        cameraView.addCallback(new CameraCallback(imageState));

        button = (FloatingActionButton) findViewById(R.id.take_picture);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionsHandler.checkWritePermissions()) {
                    // Toggle autofocus for left and right views.
                    cameraView.setAutoFocus(!cameraView.getAutoFocus());
                    cameraView.takePicture();
                    shutter.play(MediaActionSound.SHUTTER_CLICK);
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void showDialog(String title, String filename) {
        Dialog dialog = TextDialog.create(this, title, filename);
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                // Help view.
                showDialog("Help", "HELP.md");
                break;
            case R.id.action_privacy:
                // Privacy view.
                showDialog("Privacy", "PRIVACY.md");
                break;
            case R.id.action_licenses:
                // Licenses view.
                showDialog("Licenses", "LICENSES.md");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionsHandler.checkCameraPermissions()) {
            cameraView.start();
            shutter.load(MediaActionSound.SHUTTER_CLICK);
        }
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        HandlerUtils.stop();
        shutter.release();
    }
}
