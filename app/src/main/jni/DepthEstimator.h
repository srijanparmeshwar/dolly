#ifndef DEPTH_ESTIMATOR_H
#define DEPTH_ESTIMATOR_H

#include <opencv2/opencv.hpp>
#include <opencv2/ximgproc/edge_filter.hpp>
#include <opencv2/ximgproc/disparity_filter.hpp>

#include <string>
#include <vector>

class DepthEstimator {
    cv::Mat colour;
    cv::Mat depth;

public:
    DepthEstimator();
    ~DepthEstimator();
    void estimateDepth(cv::Mat A, cv::Mat B, float downsampleRatio, float renderRatio);
    cv::Mat getColour();
    cv::Mat getDepth();
};

#endif