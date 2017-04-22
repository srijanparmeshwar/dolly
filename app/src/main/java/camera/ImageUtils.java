package camera;

import android.graphics.Bitmap;
import android.os.Environment;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * OpenCV image utility functions in Java.
 */
public class ImageUtils {

    // Filename constants to create new file names.
    private static final DateFormat IMAGE_FILENAME_FORMAT = new SimpleDateFormat("'IMG_'yyyy-MM-dd-HH-mm-ss'.jpg'", Locale.UK);
    private static final DateFormat VIDEO_FILENAME_FORMAT = new SimpleDateFormat("'VID_'yyyy-MM-dd-HH-mm-ss'.avi'", Locale.UK);
    private static final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Dolly";

    /**
     * Convert byte array format of an image into OpenCV {@link Mat}.
     * @param data Byte array of image.
     * @return {@link Mat} of image.
     */
    public static Mat decode(byte[] data) {
        return Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    }

    /**
     * Create small preview of image.
     * @param frame Original size image.
     * @return Reduced size image.
     */
    public static Mat preview(Mat frame) {
        int width = frame.width();
        int height = frame.height();

        Rect roi = new Rect();
        if (width > height) {
            roi.x = (width - height) / 2;
            roi.y = 0;
            roi.width = height;
            roi.height = height;
        } else if (height > width) {
            roi.x = 0;
            roi.y = (height - width) / 2;
            roi.width = width;
            roi.height = width;
        }

        if (width != height) {
            frame = new Mat(frame, roi);
        }

        Mat preview = new Mat();
        Imgproc.resize(frame, preview, new Size(48, 48));
        return preview;
    }

    public static Bitmap convertToBitmap(Mat frame) {
        Bitmap bitmap = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(frame, bitmap);
        return bitmap;
    }

    public static boolean prepareDirectory() {
        File directory = new File(directoryPath);
        return directory.exists() || directory.mkdir();
    }

    public static void save(Mat image) {
        Imgcodecs.imwrite(getImagePath(), image);
    }

    public static void save(String filename, Mat image) {
        Imgcodecs.imwrite(getPath(filename), image);
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
