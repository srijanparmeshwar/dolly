package camera;

/**
 * JNI interface for native library to render
 * dolly zoom effect.
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
     * @param targetSize Target size of object to keep constant.
     * @param targetDistance Target distance of object.
     * @param vertexShader Source code for vertex shader.
     * @param fragmentShader Source code for fragment shader.
     * @return Native address of Renderer.
     */
    public native static long create(byte[] frameA, byte[] frameB, int w, int h, float targetSize, float targetDistance, String vertexShader, String fragmentShader);

    public native static void onSurfaceChanged(long renderer, int w, int h);

    public native static void draw(long renderer, float dz);

    public native static void delete(long renderer);

}
