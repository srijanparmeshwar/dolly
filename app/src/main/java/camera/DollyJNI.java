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
     * @param path - path to save the video.
     */
    public native static void render(long leftFrame, long rightFrame, String path);
}
