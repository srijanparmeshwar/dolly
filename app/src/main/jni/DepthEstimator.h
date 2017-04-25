#ifndef DEPTH_ESTIMATOR_H
#define DEPTH_ESTIMATOR_H

#include <opencv2/opencv.hpp>
#include <opencv2/ximgproc/edge_filter.hpp>
#include <opencv2/ximgproc/disparity_filter.hpp>

#include <string>
#include <vector>

class DepthEstimator {
    public:
        DepthEstimator() {}
        ~DepthEstimator() {}
        static void estimateDepth(cv::Mat A, cv::Mat B, cv::Mat& colour, cv::Mat& depth, float downsampleRatio = 0.1f, float renderRatio = 0.25f);
};

#endif