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
     * Renders a video created from a left frame and right frame, and saves it to the specified
     * path.
     * @param leftFrame - address of left frame.
     * @param rightFrame - address of right frame.
     */
    public native static long create(long leftFrame, long rightFrame, float targetSize, float targetDistance, float fps, float length, boolean path);

    public native static void process(long address);

    public native static void render(long address, String path);

}
