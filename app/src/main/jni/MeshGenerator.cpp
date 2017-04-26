#include "MeshGenerator.h"

using namespace cv;
using namespace std;

MeshGenerator::MeshGenerator() {}
MeshGenerator::~MeshGenerator() {}

struct vec3 {
    float x;
    float y;
    float z;
    vec3(float u, float v, float zi) {
        z = zi;
        x = z * (u - 0.5);
        y = z * (v - 0.5);
    }
};

struct rgb {
    float r;
    float g;
    float b;
};

inline void push_vertex(vec3& vertex, vector<float>& vertices) {
    vertices.push_back(vertex.x);
    vertices.push_back(vertex.y);
    vertices.push_back(vertex.z);
}

inline void push_rgb(rgb& colour, vector<float>& colours) {
    colours.push_back(colour.r);
    colours.push_back(colour.g);
    colours.push_back(colour.b);
}

inline void push_vertices(
        float u1, float v1,
        float u2, float v2,
        float z_tl, float z_tr,
        float z_bl, float z_br,
        rgb& c_tl, rgb& c_tr,
        rgb& c_bl, rgb& c_br,
        vector<float>& vertices,
        vector<float>& colours
) {
    vec3 tl(u1, v1, z_tl);
    vec3 tr(u2, v1, z_tr);
    vec3 bl(u1, v2, z_bl);
    vec3 br(u2, v2, z_br);

    push_vertex(tl, vertices);
    push_vertex(tr, vertices);
    push_vertex(bl, vertices);
    push_vertex(bl, vertices);
    push_vertex(tr, vertices);
    push_vertex(br, vertices);

    push_rgb(c_tl, colours);
    push_rgb(c_tr, colours);
    push_rgb(c_bl, colours);
    push_rgb(c_bl, colours);
    push_rgb(c_tr, colours);
    push_rgb(c_br, colours);
}

// Creates a simple triangular mesh by linking neighbouring pixels.
void trimesh(Mat image, Mat depth, vector<float>& vertices, vector<float>& colours) {
    // Convert to float image for OpenGL.
    Mat floatImage;
    normalize(image, floatImage, 0, 1, NORM_MINMAX, CV_32FC3);

    // Offset gives span of triangles over pixels.
    size_t offset = 1;
    for (size_t v = 0; v < depth.rows - offset; v++) {
        for (size_t u = 0; u < depth.cols - offset; u++) {
            // Coordinates of pixels.
            float u1 = u / ((float) depth.cols);
            float v1 = v / ((float) depth.rows);
            float u2 = (u + offset) / ((float) depth.cols);
            float v2 = (v + offset) / ((float) depth.rows);

            // Depth values at pixels.
            float z_tl = depth.at<float>(v, u);
            float z_tr = depth.at<float>(v, u + offset);
            float z_bl = depth.at<float>(v + offset, u);
            float z_br = depth.at<float>(v + offset, u + offset);

            // RGB values at pixels.
            const float* values_tl = floatImage.ptr<float>(v, u);
            const float* values_tr = floatImage.ptr<float>(v, u + offset);
            const float* values_bl = floatImage.ptr<float>(v + offset, u);
            const float* values_br = floatImage.ptr<float>(v + offset, u + offset);

            rgb c_tl = { values_tl[2], values_tl[1], values_tl[0] };
            rgb c_tr = { values_tr[2], values_tr[1], values_tr[0] };
            rgb c_bl = { values_bl[2], values_bl[1], values_bl[0] };
            rgb c_br = { values_br[2], values_br[1], values_br[0] };

            push_vertices(u1, v1, u2, v2, z_tl, z_tr, z_bl, z_br, c_tl, c_tr, c_bl, c_br, vertices, colours);
        }
    }
}

void MeshGenerator::generate(Mat image, Mat depth, vector<float>& vertices, vector<float>& colours) {
    trimesh(image, depth, vertices, colours);
}