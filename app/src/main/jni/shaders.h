#ifndef SHADERS_H
#define SHADERS_H

#include <android/log.h>

#include <GLES2/gl2.h>
#include <EGL/egl.h>

void checkShader(GLuint shaderID) {
    GLint result = GL_FALSE;
    GLint logLength = -1;

    glGetShaderiv(shaderID, GL_COMPILE_STATUS, &result);
    glGetShaderiv(shaderID, GL_INFO_LOG_LENGTH, &logLength);
    if (logLength > 0) {
        std::vector<char> errorMessage(logLength + 1);
        glGetShaderInfoLog(shaderID, logLength, NULL, &errorMessage[0]);
        if (result != GL_FALSE) {
            __android_log_print(ANDROID_LOG_INFO, "Renderer", "%s", &errorMessage[0]);
        } else {
            __android_log_print(ANDROID_LOG_ERROR, "Renderer", "%s", &errorMessage[0]);
        }
    }
}

void checkProgram(GLuint programID) {
    GLint result = GL_FALSE;
    GLint logLength = -1;

    glGetProgramiv(programID, GL_LINK_STATUS, &result);
    glGetProgramiv(programID, GL_INFO_LOG_LENGTH, &logLength);
    if (logLength > 0) {
        std::vector<char> errorMessage(logLength + 1);
        glGetProgramInfoLog(programID, logLength, NULL, &errorMessage[0]);
        if (result != GL_FALSE) {
            __android_log_print(ANDROID_LOG_INFO, "Renderer", "%s", &errorMessage[0]);
        } else {
            __android_log_print(ANDROID_LOG_ERROR, "Renderer", "%s", &errorMessage[0]);
        }
    }
}

#endif