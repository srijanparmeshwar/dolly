#include "dolly.h"
#include "Renderer.h"

using namespace cv;
using namespace std;

jlong Java_camera_DollyJNI_create(JNIEnv* env, jclass clazz, jlong pointerA, jlong pointerB, jfloat targetSize, jfloat targetDistance, jfloat fps, jfloat length, jboolean path) {
    Renderer* renderer = new Renderer(
            *((Mat*) pointerA),
            *((Mat*) pointerB),
            targetSize,
            targetDistance,
            fps,
            length,
            path ? Renderer::FORWARD : Renderer::BACKWARD
    );
    return (jlong) renderer;
}

void Java_camera_DollyJNI_process(JNIEnv* env, jclass clazz, jlong address) {
    Renderer* renderer = (Renderer*) address;
    renderer->estimateDepth();
}


void Java_camera_DollyJNI_render(JNIEnv* env, jclass clazz, jlong address, jstring path) {
    Renderer* renderer = (Renderer*) address;
    const char* utfPath = env->GetStringUTFChars(path, 0);
    renderer->render(string(utfPath));
    env->ReleaseStringUTFChars(path, utfPath);
    delete renderer;
}