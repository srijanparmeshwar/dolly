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

	struct Parameters {
        float fps;
        float length;
		float targetDistance;
		float targetSize;
		float z_step;
		float dz;
	};

	Parameters parameters;

	// Render novel views along given path.
	/*void renderViews(
			std::string filename, Renderer::PATH path,
			float targetSize, float targetDistance, float fps, float videoLength
		);*/

	public:
        enum PATH {
            FORWARD,
            BACKWARD
        };

		Renderer(cv::Mat frameA,
                 cv::Mat frameB,
                 float targetDistance,
                 float targetSize,
                 float fps,
                 float length,
                 PATH path);
		~Renderer();
		// Estimate depth using stereo matching.
		void estimateDepth();
		cv::Mat grabFrame();
        void render(std::string path);
		//void render(std::string path);
};

#endif