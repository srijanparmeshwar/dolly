#include <jni.h>

#ifndef DOLLY_H
#define DOLLY_H

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL Java_camera_DollyJNI_create
(JNIEnv *, jclass, jlong, jlong, jfloat, jfloat, jfloat, jfloat, jboolean);

JNIEXPORT void JNICALL Java_camera_DollyJNI_process
        (JNIEnv *, jclass, jlong);

JNIEXPORT void JNICALL Java_camera_DollyJNI_render
        (JNIEnv *, jclass, jlong, jstring);

#ifdef __cplusplus
}
#endif

#endif //DOLLY_H
