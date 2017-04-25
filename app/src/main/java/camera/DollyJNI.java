package camera;

/**
 * JNI interface for native library for OpenCV and OpenGL functions.
 */
public class DollyJNI {
    static {
        // Load native library.
        System.loadLibrary("dolly");
    }

    /**
     * Creates Renderer object using JNI.
     * @param frameA Bytes of first frame.
     * @param frameB Bytes of second frame.
     * @param w Width of GL view.
     * @param h Height of GL view.
     * @param vertexShader Source code for vertex shader.
     * @param fragmentShader Source code for fragment shader.
     * @return Native address of renderer.
     */
    public native static long create(byte[] frameA, byte[] frameB, int w, int h, String vertexShader, String fragmentShader);

    /**
     * Sets the width and height for the perspective matrix.
     * @param renderer Native address of renderer.
     * @param w New width.
     * @param h New height.
     */
    public native static void onSurfaceChanged(long renderer, int w, int h);

    /**
     * Main render function for OpenGL.
     * @param renderer Native address of renderer.
     * @param dz Change in z parameter.
     */
    public native static void draw(long renderer, float dz);

    /**
     * Release resources of renderer object.
     * @param renderer Native address of renderer.
     */
    public native static void delete(long renderer);

}
