package camera;

import java.io.IOException;

public class Renderer {
    public static void render(long leftFrame, long rightFrame, String path) throws IOException {
        float fps = 30;
        float length = 2;
        int N = (int) (fps * length);

        // Create renderer.
        long address = DollyJNI.create(leftFrame, rightFrame, 3, 3, fps, length, true);

        // Calculate depth.
        DollyJNI.process(address);

        // Render views.
        DollyJNI.render(address, path);
    }
}
