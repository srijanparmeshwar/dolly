package srijanparmeshwar.dolly;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RenderActivity extends AppCompatActivity {

    private RenderView renderView;

    private static final String TAG = "RenderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render);

        Toolbar toolbar = (Toolbar) findViewById(R.id.render_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.app_name);
        }

        renderView = (RenderView) findViewById(R.id.render_view);
        renderView.setup(loadShader("vertex.shader"), loadShader("fragment.shader"));
    }

    private String loadShader(String filename) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return builder.toString();
    }

    public void onBackPressed() {
        renderView.delete();
        super.onBackPressed();
    }
}
