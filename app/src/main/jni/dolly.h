#include <jni.h>

#ifndef DOLLY_H
#define DOLLY_H

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL Java_camera_DollyJNI_create
(JNIEnv *, jclass, jbyteArray, jbyteArray, jint, jint, jstring, jstring);

JNIEXPORT void JNICALL Java_camera_DollyJNI_onSurfaceChanged
        (JNIEnv *, jclass, jlong, jint, jint);

JNIEXPORT void JNICALL Java_camera_DollyJNI_draw
        (JNIEnv *, jclass, jlong, jfloat);

JNIEXPORT void JNICALL Java_camera_DollyJNI_delete
(JNIEnv *, jclass, jlong);

JNIEXPORT void JNICALL Java_camera_DollyJNI_test
(JNIEnv *, jclass, jbyteArray, jstring);

#ifdef __cplusplus
}
#endif

#endif //DOLLY_H
