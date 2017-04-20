//
// Created by Srijan on 07/04/2017.
//

#include "dolly.h"

#include "Renderer.h"

using namespace cv;
using namespace std;

void Java_camera_DollyJNI_render(JNIEnv* env, jclass clazz, jlong pointerA, jlong pointerB, jstring filename) {
    const char* charFilename = env->GetStringUTFChars(filename, 0);
    string stringFilename(charFilename);

    Renderer renderer(*((Mat*) pointerA), *((Mat*) pointerB));
    renderer.render(stringFilename);

    env->ReleaseStringUTFChars(filename, charFilename);
}