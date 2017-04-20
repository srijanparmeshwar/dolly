#ifndef RENDERER_H
#define RENDERER_H

#include <opencv2/opencv.hpp>
#include <opencv2/ximgproc/edge_filter.hpp>
#include <opencv2/ximgproc/disparity_filter.hpp>

#include <string>
#include <vector>

class Renderer {
	// Two different views of a static scene.
	cv::Mat A;
	cv::Mat B;

	// Rectification homographies.
	cv::Mat H_A;
	cv::Mat H_B;

	cv::Mat rectifiedA;
	cv::Mat rectifiedB;

	cv::Mat depth;

	enum PATH {
		FORWARD,
		BACKWARD
	};

	// Estimate depth using stereo matching.
	void estimateDepth();
	// Render novel views along given path.
	void renderViews(
			std::string filename, Renderer::PATH path,
			float targetSize, float targetDistance, float fps, float videoLength
		);

	public:
		Renderer(cv::Mat frameA, cv::Mat frameB);
		~Renderer();
		void render(std::string path);
};

#endif
