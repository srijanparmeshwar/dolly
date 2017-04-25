package srijanparmeshwar.dolly;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import camera.DollyJNI;

import static camera.ImageState.frameA;
import static camera.ImageState.frameB;

public class RenderView extends GLSurfaceView {

    private int width;
    private int height;
    private Renderer renderer;
    private String vertexShader;
    private String fragmentShader;

    public RenderView(Context context) {
        super(context);
    }

    public RenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setup(String vs, String fs) {
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        vertexShader = vs;
        fragmentShader = fs;
        setEGLConfigChooser(8, 8, 8, 0, 16, 0);
        setEGLContextClientVersion(2);
        renderer = new Renderer();
        setRenderer(renderer);
    }

    public void delete() {
        DollyJNI.delete(renderer.renderer);
    }

    private class Renderer implements GLSurfaceView.Renderer {

        private long renderer;

        public Renderer() {}

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            this.renderer = DollyJNI.create(frameA, frameB, width, height, 1, 1, vertexShader, fragmentShader);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h) {
            DollyJNI.onSurfaceChanged(renderer, w, h);
            width = w;
            height = h;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            DollyJNI.draw(renderer, 0.0f);
        }
    }
}
