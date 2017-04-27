#include "Renderer.h"
#include "shaders.h"

#include <cmath>

void Renderer::init(std::string vertexShader, std::string fragmentShader) {
    // Create shader locations.
    GLuint vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
    GLuint fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);

    // Compile and check vertex shader.
    const char* vertexSource = vertexShader.c_str();
    glShaderSource(vertexShaderID, 1, &vertexSource, NULL);
    glCompileShader(vertexShaderID);
    checkShader(vertexShaderID);

    // Compile and check fragment shader.
    const char* fragmentSource = fragmentShader.c_str();
    glShaderSource(fragmentShaderID, 1, &fragmentSource, NULL);
    glCompileShader(fragmentShaderID);
    checkShader(fragmentShaderID);

    // Create, compile, link and check program.
    programID = glCreateProgram();
    glAttachShader(programID, vertexShaderID);
    glAttachShader(programID, fragmentShaderID);
    glLinkProgram(programID);
    checkProgram(programID);

    // Remove used shaders after linking.
    glDetachShader(programID, vertexShaderID);
    glDetachShader(programID, fragmentShaderID);

    glDeleteShader(vertexShaderID);
    glDeleteShader(fragmentShaderID);

    // Find variable locations.
    vertexID = glGetAttribLocation(programID, "vPos");
    colourID = glGetAttribLocation(programID, "vCol");
    projectionID = glGetUniformLocation(programID, "projectionMatrix");
    zID = glGetUniformLocation(programID, "z");

    // Enable depth buffering.
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LESS);
}

void Renderer::setMesh(std::vector<float> vertices, std::vector<float> colours) {
    glGenBuffers(1, &vertexBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, sizeof(float) * vertices.size(), &(vertices.front()), GL_STATIC_DRAW);

    glGenBuffers(1, &colourBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, colourBuffer);
    glBufferData(GL_ARRAY_BUFFER, sizeof(float) * colours.size(), &(colours.front()), GL_STATIC_DRAW);

    // Number of triangles.
    N = vertices.size() / 3;
}

void Renderer::onSurfaceChanged(int w, int h) {
    width = w;
    height = h;
}

#define PI 3.1415926f

inline float smooth_dolly(float x) {
    // ~ 0.8 * (1 + sin(PI * (1.5 - x)) / 2 -> min = 0, max = 0.8
    return 0.79f * (1.0f + sin(PI * (1.5f - x))) / 2.0f;
}

inline float calculate_focal_length(float d1, float d2) {
    return d2 / d1;
}

inline float calculate_fov(float f) {
    return atan(sqrt(2.0f) / f);
}

glm::mat4 Renderer::projectionMatrix(float z) {
    float d1 = targetDistance;
    float d2 = targetDistance - z;
    float f2 = calculate_focal_length(d1, d2);
    float fov = calculate_fov(f2);
    glm::mat4 camera = glm::lookAt(glm::vec3(0.0f, 0.0f, 0.0f), glm::vec3(0.0f, 0.0f, 1.0f), glm::vec3(0.0f, -1.0f, 0.0f));
    glm::mat4 projection = glm::perspective(fov, ((float) width) / ((float) height), f2, 255.0f);
    return projection * camera;
}

void Renderer::draw(float dollyStep) {
    float z = smooth_dolly(dolly);
    glm::mat4 glmProjectionMatrix = projectionMatrix(z);

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glUseProgram(programID);

    glUniformMatrix4fv(projectionID, 1, GL_FALSE, glm::value_ptr(glmProjectionMatrix));
    glUniform1fv(zID, 1, &z);

    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glVertexAttribPointer(
            vertexID,
            3,
            GL_FLOAT,
            GL_FALSE,
            0,
            0
    );
    glEnableVertexAttribArray(vertexID);

    glBindBuffer(GL_ARRAY_BUFFER, colourBuffer);
    glVertexAttribPointer(
            colourID,
            3,
            GL_FLOAT,
            GL_FALSE,
            0,
            0
    );
    glEnableVertexAttribArray(colourID);

    glDrawArrays(GL_TRIANGLES, 0, N);

    glDisableVertexAttribArray(vertexID);
    glDisableVertexAttribArray(colourID);

    dolly += dollyStep;
}