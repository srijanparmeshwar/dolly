package srijanparmeshwar.dolly;

import android.media.MediaActionSound;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.cameraview.CameraView;

import org.opencv.android.OpenCVLoader;

import camera.CameraCallback;
import camera.ImageState;
import camera.Stereo;

public class MainActivity extends AppCompatActivity {

    private PermissionsHandler permissionsHandler;
    private CameraView cameraView;
    private MediaActionSound shutter;
    private ImageState imageState;
    private FloatingActionButton button;

    private static final String TAG = "MainActivity";

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(R.string.app_name);
        //setActionBar(toolbar);

        permissionsHandler = new PermissionsHandler(this);

        shutter = new MediaActionSound();

        ImageView left = (ImageView) findViewById(R.id.left_preview);
        ImageView right = (ImageView) findViewById(R.id.right_preview);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageState = new ImageState(this, left, right, progressBar);

        cameraView = (CameraView) findViewById(R.id.camera);
        cameraView.addCallback(new CameraCallback(imageState));

        button = (FloatingActionButton) findViewById(R.id.take_picture);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionsHandler.checkWritePermissions()) {
                    cameraView.takePicture();
                    shutter.play(MediaActionSound.SHUTTER_CLICK);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
