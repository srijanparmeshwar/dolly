#include <jni.h>

#ifndef DOLLY_H
#define DOLLY_H

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Main JNI call to render the dolly zoom sequence given
 * the input frames.
 *
 * Class:     camera_DollyJNI
 * Method:    render
 * Signature: (JJLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_camera_DollyJNI_render
(JNIEnv *, jclass, jlong, jlong, jstring);

#ifdef __cplusplus
}
#endif

#endif //DOLLY_H
