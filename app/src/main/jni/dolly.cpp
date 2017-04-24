#include "dolly.h"
#include "Renderer.h"

using namespace std;

jlong Java_camera_DollyJNI_create(JNIEnv* env, jclass clazz, jlong pointerA, jlong pointerB, jfloat targetSize, jfloat targetDistance, jfloat fps, jfloat length, jboolean path) {
    Renderer* renderer = new Renderer(0, 0, targetSize, targetDistance);
    return (jlong) renderer;
}

void Java_camera_DollyJNI_process(JNIEnv* env, jclass clazz, jlong address) {
    Renderer* renderer = (Renderer*) address;
}


void Java_camera_DollyJNI_render(JNIEnv* env, jclass clazz, jlong address, jstring path) {
    Renderer* renderer = (Renderer*) address;
    delete renderer;
}