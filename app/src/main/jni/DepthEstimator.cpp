#include "DepthEstimator.h"
#include "MeshGenerator.h"

using namespace cv;
using namespace cv::ximgproc;
using namespace std;

// Convert image to floating point grayscale.
Mat rgb2gray(Mat rgb) {
    Mat gray;
    normalize(rgb, gray, 0, 1, NORM_MINMAX, CV_32FC3);
    cvtColor(gray, gray, CV_RGB2GRAY);
    return gray;
}

// Convert float image to byte image and normalise to 0 - 255 range.
Mat discretise(Mat floatImage) {
    Mat byteImage;
    if (floatImage.channels() == 1) {
        normalize(floatImage, byteImage, 0, 255, NORM_MINMAX, CV_8UC1);
    } else if (floatImage.channels() == 3) {
        normalize(floatImage, byteImage, 0, 255, NORM_MINMAX, CV_8UC3);
    } else {
        byteImage = floatImage;
    }
    return byteImage;
}

Ptr<StereoSGBM> createMatcher(int numDisparities) {
    int minDisparity = - numDisparities / 2;
    int blockSize = 9;
    Ptr<StereoSGBM> matcher = StereoSGBM::create(minDisparity, numDisparities, blockSize);
    matcher->setP1(24 * blockSize * blockSize);
    matcher->setP2(96 * blockSize * blockSize);
    matcher->setPreFilterCap(63);
    return matcher;
}

Mat pad(Mat image, int n) {
    Rect roi(n / 2, 0, image.cols, image.rows);
    Mat paddedImage = Mat::zeros(Size(image.cols + n, image.rows), image.type());
    image.copyTo(paddedImage(roi));
    return paddedImage;
}

Mat unpad(Mat image, Size size, int n) {
    Rect roi(n / 2, 0, size.width, size.height);
    return image(roi);
}

Mat estimateDisparity(Mat A, Mat B) {
    Mat dA;
    Mat dB;
    Mat smoothDepth;
    Ptr<StereoMatcher> matcherA;
    Ptr<StereoMatcher> matcherB;
    Ptr<DisparityWLSFilter> wlsFilter;
    int numDisparities = 96;
    matcherA = createMatcher(numDisparities);
    matcherB = createRightMatcher(matcherA);
    wlsFilter = createDisparityWLSFilter(matcherA);
    wlsFilter->setLambda(8000);
    wlsFilter->setSigmaColor(5);
    Mat pA = pad(A, numDisparities);
    Mat pB = pad(B, numDisparities);
    matcherA->compute(pA, pB, dA);
    matcherB->compute(pB, pA, dB);
    wlsFilter->filter(dA, pA, smoothDepth, dB);
    return unpad(smoothDepth, A.size(), numDisparities);
}

void calculateCorrespondences(Mat A, Mat B, vector<Point2f>& pointsA, vector<Point2f>& pointsB) {
    size_t subsampling_ratio = 16;

    for (size_t y = 0; y < A.rows; y += subsampling_ratio) {
        for (size_t x = 0; x < A.cols; x += subsampling_ratio) {
            pointsA.push_back(Point2f(x, y));
        }
    }

    Mat status;
    Mat errors;
    calcOpticalFlowPyrLK(discretise(rgb2gray(A)), discretise(rgb2gray(B)), pointsA, pointsB, status, errors);
}

Mat disparityToDepth(Mat disparity) {
    Mat floatDisparity;
    normalize(disparity, floatDisparity, 0, 1, NORM_MINMAX, CV_32F);
    Mat depth = 1.0f / (floatDisparity + 0.01f);
    return depth;
}

Mat scaleHomography(Mat H, float sx, float sy) {
    Mat S = (Mat_<double>(3, 3)
            <<
            sx, 0, 0,
            0, sy, 0,
            0, 0, 1
    );
    Mat scaledH = S * H * S.inv();
    return scaledH;
}

void DepthEstimator::estimateDepth(Mat A, Mat B, Mat& colour, Mat& depth, float downsampleRatio, float renderRatio) {
    // Downsample frames for optical flow calculation.
    Mat downsampledA;
    Mat downsampledB;
    resize(A, downsampledA, Size(), downsampleRatio, downsampleRatio);
    resize(B, downsampledB, Size(), downsampleRatio, downsampleRatio);

    //// Find correspondences using optical flow.
    vector<Point2f> pointsA, pointsB;
    calculateCorrespondences(downsampledA, downsampledB, pointsA, pointsB);

    // Estimate fundamental matrix between camera views of static scene.
    Mat fundamentalMatrix = findFundamentalMat(pointsA, pointsB, CV_FM_RANSAC, 0.5);

    // Calculate rectification homographies.
    Mat H_A;
    Mat H_B;
    stereoRectifyUncalibrated(pointsA, pointsB, fundamentalMatrix, downsampledA.size(), H_A, H_B);

    // Rectify images.
    Mat rectifiedA;
    Mat rectifiedB;
    warpPerspective(downsampledA, rectifiedA, H_A, downsampledA.size());
    warpPerspective(downsampledB, rectifiedB, H_B, downsampledB.size());

    // Resize to render size.
    Mat disparity = estimateDisparity(rectifiedA, rectifiedB);
    Mat downsampledDepth = disparityToDepth(disparity);
    Mat scaledHomography = scaleHomography(H_A, renderRatio / downsampleRatio, renderRatio / downsampleRatio);
    resize(A, downsampledA, Size(), renderRatio, renderRatio);
    warpPerspective(downsampledA, colour, scaledHomography, downsampledA.size());
    resize(downsampledDepth, depth, colour.size());
}