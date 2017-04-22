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
     * @param leftFrame Address of left frame.
     * @param rightFrame Address of right frame.
     * @param targetSize Target size of object to keep constant.
     * @param targetDistance Target distance of object.
     * @param fps Frame rate of video in frames per second.
     * @param length Length of video in seconds.
     * @param path Forwards or backwards (true for forwards, false for backwards).
     * @return Native address of Renderer.
     */
    public native static long create(long leftFrame, long rightFrame, float targetSize, float targetDistance, float fps, float length, boolean path);

    /**
     * Calculate the depth map from the input stereo views.
     * @param address Native address of Renderer.
     */
    public native static void process(long address);

    /**
     * Render and save video to given path.
     * @param address Native address of Renderer.
     * @param path File path to save the video.
     */
    public native static void render(long address, String path);

}
