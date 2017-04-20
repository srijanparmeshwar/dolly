#include "Renderer.h"

#include <cmath>

using namespace cv;
using namespace cv::ximgproc;
using namespace std;

// Image sizes.
const int ASPECT_WIDTH = 3;
const int ASPECT_HEIGHT = 4;
const int DOWNSAMPLE_SIZE = 96;

const int WIDTH = DOWNSAMPLE_SIZE * ASPECT_WIDTH;
const int HEIGHT = DOWNSAMPLE_SIZE * ASPECT_HEIGHT;

const int RENDER_SIZE = DOWNSAMPLE_SIZE * 3;
const int RENDER_WIDTH = RENDER_SIZE * ASPECT_WIDTH;
const int RENDER_HEIGHT = RENDER_SIZE * ASPECT_HEIGHT;

const float RATIO = ((float) RENDER_SIZE) / ((float) DOWNSAMPLE_SIZE);

// Constructor and destructor.
Renderer::Renderer(Mat frameA, Mat frameB)
	: A(frameA), B(frameB) {
}

Renderer::~Renderer() {
}

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

Mat jointFilter(Mat colour, Mat disparity) {
	Mat smoothDisparity;
	Mat smoothColour;
	edgePreservingFilter(colour, smoothColour);
	dtFilter(smoothColour, disparity, smoothDisparity, 15, 7);
	return smoothDisparity;
}

Ptr<StereoSGBM> createMatcher() {
	int minDisparity = -24;
	int numDisparities = 48;
	int blockSize = 5;
	Ptr<StereoSGBM> matcher = StereoSGBM::create(minDisparity, numDisparities, blockSize);
	matcher->setP1(24 * blockSize * blockSize);
	matcher->setP2(96 * blockSize * blockSize);
	matcher->setPreFilterCap(31);
	return matcher;
}

Mat estimateDisparity(Mat A, Mat B) {
	Mat dA;
	Mat dB;
	Mat smoothDepth;
	Ptr<StereoMatcher> matcherA;
	Ptr<StereoMatcher> matcherB;
	Ptr<DisparityWLSFilter> wlsFilter;
	matcherA = createMatcher();
	matcherB = createRightMatcher(matcherA);
	wlsFilter = createDisparityWLSFilter(matcherA);
	wlsFilter->setLambda(8000);
	wlsFilter->setSigmaColor(2);
	matcherA->compute(A, B, dA);
	matcherB->compute(B, A, dB);
	wlsFilter->filter(dA, A, smoothDepth, dB);
	return smoothDepth;
}

Mat smooth(Mat disparity) {
	Mat byteDisparity = discretise(disparity);
	Mat smoothDisparity;
	inpaint(byteDisparity, (byteDisparity < 16) | (byteDisparity + 16 > 255), smoothDisparity, 25, CV_INPAINT_TELEA);
	medianBlur(smoothDisparity, smoothDisparity, 7);
	return smoothDisparity;
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

void calculateCorrespondences(Mat opticalFlow, vector<Point2f>& pointsA, vector<Point2f>& pointsB) {
	// Subsample optical flow estimates for correspondences.
	size_t subsamplingRatio = 1;
	size_t width = opticalFlow.cols;
	size_t height = opticalFlow.rows;

	for (size_t y = 0; y < height; y += subsamplingRatio) {
		for (size_t x = 0; x < width; x += subsamplingRatio) {
			const float* pixel = opticalFlow.ptr<float>(y, x);
			// Optical flow values for this pixel.
			float dx = pixel[0];
			float dy = pixel[1];

			// Left image points.
			pointsA.push_back(Point2f(x, y));
			// Right image points.
			pointsB.push_back(Point2f(x + dx, y + dy));
		}
	}

}

Mat disparityToDepth(Mat disparity) {
	Mat floatDisparity;
	normalize(disparity, floatDisparity, 0, 1, NORM_MINMAX, CV_32F);
	Mat depth = 1.0f / (floatDisparity + 0.1f);
	return depth;
}

float pixelDistance(int x, int y, float u, float v) {
	float a = max(0.0f, min(1.0f, (1 - abs(u - x))));
	float b = max(0.0f, min(1.0f, (1 - abs(v - y))));
	return a * b;
}

void copyValues(const uchar* src, uchar* dst, float weight) {
	dst[0] = src[0];
	dst[1] = src[1];
	dst[2] = src[2];
}

Mat renderView(Mat colourImage, Mat depth, float f, float dz) {
	Mat map(colourImage.size(), CV_32FC2);
	Mat image = Mat::zeros(colourImage.size(), colourImage.type());

	for (size_t y = 0; y < depth.rows; y++) {
		for (size_t x = 0; x < depth.cols; x++) {
			float* values = map.ptr<float>(y, x);
			float u = x / ((float) RENDER_WIDTH);
			float v = y / ((float) RENDER_HEIGHT);
			float z = depth.at<float>(y, x);

			float nu = 0.5 + f * z * (u - 0.5) / (z - dz);
			float nv = 0.5 + f * z * (v - 0.5) / (z - dz);

			values[0] = nu * RENDER_WIDTH;
			values[1] = nv * RENDER_HEIGHT;

			int m = floor(values[0]);
			int n = floor(values[1]);
			for (int dy = -1; dy < 2; dy++) {
				for (int dx = -1; dx < 2; dx++) {
					int cx = m + dx;
					int cy = n + dy;
					if (cx > 0 && cy > 0 && cx < image.cols && cy < image.rows) {
						uchar* vals = image.ptr<uchar>(n, m);
						copyValues(colourImage.ptr<uchar>(y, x), image.ptr<uchar>(cy, cx), pixelDistance(cx, cy, values[0], values[1]));
					}
				}
			}

		}
	}
	
	return image;
}

Mat scaleHomography(Mat H, float sx, float sy) {
	Mat S = (Mat_<double>(3, 3)
		<<
		sx, 0, 0,
		0, sy, 0,
		0, 0, 1);
	Mat scaledH = S * H * S.inv();
	return scaledH;
}

void Renderer::estimateDepth() {
	// Downsample frames for optical flow calculation.
	Mat downsampledA;
	Mat downsampledB;
	resize(A, downsampledA, Size(WIDTH, HEIGHT));
	resize(B, downsampledB, Size(WIDTH, HEIGHT));

	//// Find correspondences using optical flow.
	vector<Point2f> pointsA, pointsB;
	//calculateCorrespondences(opticalFlow, pointsA, pointsB);
	calculateCorrespondences(downsampledA, downsampledB, pointsA, pointsB);

	// Estimate fundamental matrix between camera views of static scene.
	Mat fundamentalMatrix = findFundamentalMat(pointsA, pointsB, CV_FM_RANSAC, 0.5);

	// Calculate rectification homographies.
	stereoRectifyUncalibrated(pointsA, pointsB, fundamentalMatrix, downsampledA.size(), H_A, H_B);

	// Rectify images.
	warpPerspective(downsampledA, rectifiedA, H_A, downsampledA.size());
	warpPerspective(downsampledB, rectifiedB, H_B, downsampledB.size());

	Mat disparity = estimateDisparity(rectifiedA, rectifiedB);
	Mat downsampledDepth = disparityToDepth(disparity);

	resize(downsampledDepth, depth, Size(RENDER_WIDTH, RENDER_HEIGHT));
	resize(A, downsampledA, Size(RENDER_WIDTH, RENDER_HEIGHT));
	resize(B, downsampledB, Size(RENDER_WIDTH, RENDER_HEIGHT));

	warpPerspective(downsampledA, rectifiedA, scaleHomography(H_A, RATIO, RATIO), downsampledA.size());
	warpPerspective(downsampledB, rectifiedB, scaleHomography(H_B, RATIO, RATIO), downsampledB.size());
}

void Renderer::renderViews(
		string filename, Renderer::PATH renderPath = Renderer::FORWARD,
		float targetSize = 3, float targetDistance = 3, float fps = 1, float videoLength = 2
	) {
	int N = (int) fps * videoLength;
	float cameraDistance = targetDistance / 5;

	float z_step;
	float dz;
	switch (renderPath) {
		case Renderer::FORWARD:
			z_step = cameraDistance / N;
			dz = 0;
			break;
		case Renderer::BACKWARD:
			z_step = - cameraDistance / N;
			dz = cameraDistance;
			break;
	}

	float f = (targetDistance - dz) / targetSize;

	Mat unrectifiedView;
	for (int frame = 0; frame < N; frame++) {
		Mat rectifiedView = renderView(rectifiedA, depth, f, dz);
		warpPerspective(rectifiedView, unrectifiedView, scaleHomography(H_A.inv(), RATIO, RATIO), rectifiedView.size());
		dz += z_step;
		f = (targetDistance - dz) / targetSize;
	}
	imwrite(filename, unrectifiedView);
}


void Renderer::render(string path) {
	estimateDepth();
	renderViews(path, Renderer::FORWARD, 3, 3);
}