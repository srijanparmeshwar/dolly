package camera;

import android.graphics.Bitmap;
import android.os.Environment;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Srijan on 04/04/2017.
 */
public class ImageUtils {

    private static final DateFormat FILENAME_FORMAT = new SimpleDateFormat("'IMG_'yyyy-MM-dd-HH-mm-ss'.jpg'", Locale.UK);
    private static final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Dolly";

    public static Mat rgb2gray(Mat rgb, double scale) {
        Mat gray = new Mat();
        Imgproc.cvtColor(rgb, gray, Imgproc.COLOR_RGB2GRAY);
        gray.convertTo(gray, CvType.CV_32FC3);
        Imgproc.resize(gray, gray, new Size(), scale, scale, Imgproc.INTER_LINEAR);
        return gray;
    }

    public static List<Mat> splitChannels(Mat image, int N) {
        List<Mat> channels = new ArrayList<>(N);
        channels.add(new Mat());
        channels.add(new Mat());
        Core.split(image, channels);
        return channels;
    }

    public static Mat abs(Mat image) {
        Mat absImage = new Mat();
        Core.convertScaleAbs(image, absImage);
        return absImage;
    }

    public static Mat decode(byte[] data) {
        return Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    }

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
        String filename = FILENAME_FORMAT.format(new Date());
        save(filename, image);
    }

    public static void save(String filename, Mat image) {
        Imgcodecs.imwrite(directoryPath + "/" + filename, image);
    }

}
