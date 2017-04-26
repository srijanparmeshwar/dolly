#ifndef RENDERER_H
#define RENDERER_H

#include <string>
#include <vector>

#include <glm/vec3.hpp>
#include <glm/mat4x4.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>

#include <GLES2/gl2.h>
#include <EGL/egl.h>

class Renderer {
    int width;
    int height;

    float targetSize;
    float targetDistance;
    float z;

    GLuint programID;

    GLint vertexID;
    GLint colourID;
    GLint projectionID;
    GLint zID;

    GLuint vertexBuffer;
    GLuint colourBuffer;

    int N;

    glm::mat4 projectionMatrix(float fov);

    public:
        Renderer(int w, int h, float s, float d) : width(w), height(w), targetSize(s), targetDistance(d), z(0.0f) {}
        ~Renderer() {
            glDeleteBuffers(1, &vertexBuffer);
            glDeleteBuffers(1, &colourBuffer);
            glDeleteProgram(programID);
        }
        void init(std::string vertexShader, std::string fragmentShader);
        void setMesh(std::vector<float> vertices, std::vector<float> colours);
        void onSurfaceChanged(int w, int h);
        void draw(float dz);
};

#endif