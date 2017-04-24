#include "Renderer.h"

inline float calculate_fov(float s, float z, float dz) {
    return 2.0f * atan(s / (2  * (z - dz)));
}

glm::mat4 Renderer::projectionMatrix(float fov) {
    glm::mat4 camera = glm::lookAt(glm::vec3(0, 0, 0), glm::vec3(0, 0, 1), glm::vec3(0, -1, 0));
    glm::mat4 projection = glm::perspective(fov, ((float) width) / ((float) height), 0.1f, 5.0f);
    return projection * camera;
}

void Renderer::init(std::string vertexShader, std::string fragmentShader) {
    vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
    fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);

    const char* vertexSource = vertexShader.c_str();
    glShaderSource(vertexShaderID, 1, &vertexSource, NULL);
    glCompileShader(vertexShaderID);

    const char* fragmentSource = vertexShader.c_str();
    glShaderSource(fragmentShaderID, 1, &fragmentSource, NULL);
    glCompileShader(fragmentShaderID);

    programID = glCreateProgram();
    glAttachShader(programID, vertexShaderID);
    glAttachShader(programID, fragmentShaderID);
    glLinkProgram(programID);

    glDetachShader(programID, vertexShaderID);
    glDetachShader(programID, fragmentShaderID);

    glDeleteShader(vertexShaderID);
    glDeleteShader(fragmentShaderID);

    projectionID = glGetUniformLocation(programID, "projectionMatrix");
    zID = glGetUniformLocation(programID, "z");

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

    N = vertices.size() / 3;
}

void Renderer::draw(float dz) {
    float fov = calculate_fov(targetSize, targetDistance, z);
    glm::mat4 glmProjectionMatrix = projectionMatrix(fov);

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glUseProgram(programID);

    glUniformMatrix4fv(projectionID, 1, GL_FALSE, glm::value_ptr(glmProjectionMatrix));
    glUniform1fv(zID, 1, &z);

    glEnableVertexAttribArray(0);
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glVertexAttribPointer(
            0,
            3,
            GL_FLOAT,
            GL_FALSE,
            0,
            0
    );

    glEnableVertexAttribArray(1);
    glBindBuffer(GL_ARRAY_BUFFER, colourBuffer);
    glVertexAttribPointer(
            1,
            3,
            GL_FLOAT,
            GL_FALSE,
            0,
            0
    );

    glDrawArrays(GL_TRIANGLES, 0, N / 3);

    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);

    z += dz;
}
