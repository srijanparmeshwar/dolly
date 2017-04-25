#ifndef MESH_GENERATOR_H
#define MESH_GENERATOR_H

#include <vector>

#include <opencv2/opencv.hpp>

class MeshGenerator {
    public:
        MeshGenerator();
        ~MeshGenerator();
        static void generate(cv::Mat image, cv::Mat depth, std::vector<float>& vertices, std::vector<float>& colours);
};

#endif