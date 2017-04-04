package srijanparmeshwar.dolly;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by Srijan on 03/04/2017.
 */
public class HandlerUtils {
    private static Handler backgroundHandler;

    public static Handler getHandler() {
        if (backgroundHandler == null) {
            HandlerThread thread = new HandlerThread("Background");
            thread.start();
            backgroundHandler = new Handler(thread.getLooper());
        }
        return backgroundHandler;
    }

    public static void stop() {
        if (backgroundHandler != null) {
            backgroundHandler.getLooper().quitSafely();
            backgroundHandler = null;
        }
    }
}
