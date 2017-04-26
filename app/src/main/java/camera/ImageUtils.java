package camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Image utility functions.
 */
public class ImageUtils {

    // Filename constants to create image and video filenames using current time.
    private static final DateFormat IMAGE_FILENAME_FORMAT = new SimpleDateFormat("'IMG_'yyyy-MM-dd-HH-mm-ss'.jpg'", Locale.UK);
    private static final DateFormat VIDEO_FILENAME_FORMAT = new SimpleDateFormat("'VID_'yyyy-MM-dd-HH-mm-ss'.avi'", Locale.UK);
    private static final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Dolly";

    private static final String TAG = "ImageUtils";

    public static Bitmap getPreview(byte[] data) {
        return getPreview(BitmapFactory.decodeByteArray(data, 0, data.length));
    }

    private static Bitmap getPreview(Bitmap frame) {
        int width = frame.getWidth();
        int height = frame.getHeight();

        int x;
        int y;
        int cropWidth;
        int cropHeight;
        if (width > height) {
            x = (width - height) / 2;
            y = 0;
            cropWidth = height;
            cropHeight = height;
        } else {
            x = 0;
            y = (height - width) / 2;
            cropWidth = width;
            cropHeight = width;
        }
        Bitmap croppedBitmap;
        if (width != height) {
            croppedBitmap = Bitmap.createBitmap(frame, x, y, cropWidth, cropHeight);
        } else {
            croppedBitmap = frame;
        }

        frame.recycle();
        return Bitmap.createScaledBitmap(croppedBitmap, 48, 48, false);
    }

    public static boolean prepareDirectory() {
        File directory = new File(directoryPath);
        return directory.exists() || directory.mkdir();
    }

    public static void save(String filename, Bitmap image) {
        File imageFile = new File(filename);
        try (FileOutputStream fileOutputStream = new FileOutputStream(imageFile)){
            image.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
        } catch (IOException ioException) {
            Log.e(TAG, ioException.getMessage());
        }
    }

    public static void save(Bitmap image) {
        save(getImagePath(), image);
    }

    public static String getImagePath() {
        return getPath(IMAGE_FILENAME_FORMAT.format(new Date()));
    }

    public static String getVideoPath() {
        return getPath(VIDEO_FILENAME_FORMAT.format(new Date()));
    }

    public static String getPath(String filename) {
        return directoryPath + "/" + filename;
    }
}
