#include "dolly.h"
#include "DepthEstimator.h"
#include "MeshGenerator.h"
#include "Renderer.h"

using namespace std;

cv::Mat decode(JNIEnv* env, jbyteArray jbytes) {
    jbyte* cbytes = env->GetByteArrayElements(jbytes, NULL);
    jsize length = env->GetArrayLength(jbytes);

    vector<uchar> data(cbytes, cbytes + length);
    cv::Mat image = cv::imdecode(data, CV_LOAD_IMAGE_UNCHANGED);
    env->ReleaseByteArrayElements(jbytes, cbytes, JNI_ABORT);
    return image;
}

jlong Java_camera_DollyJNI_create(JNIEnv* env, jclass clazz, jbyteArray bytesA, jbyteArray bytesB, jint w, jint h, jfloat targetSize, jfloat targetDistance, jstring jvertexShader, jstring jfragmentShader) {
    Renderer* renderer = new Renderer(w, h, targetSize, targetDistance);
    DepthEstimator depthEstimator;
    depthEstimator.estimateDepth(decode(env, bytesA), decode(env, bytesB), 0.1, 0.25);
    vector<float> vertices;
    vector<float> colours;
    MeshGenerator::generate(depthEstimator.getColour(), depthEstimator.getDepth(), vertices, colours);

    const char* cvertexShader = env->GetStringUTFChars(jvertexShader, 0);
    const char* cfragmentShader = env->GetStringUTFChars(jfragmentShader, 0);
    renderer->init(string(cvertexShader), string(cfragmentShader));
    env->ReleaseStringUTFChars(jvertexShader, cvertexShader);
    env->ReleaseStringUTFChars(jfragmentShader, cfragmentShader);

    renderer->setMesh(vertices, colours);
    return (jlong) renderer;
}

void Java_camera_DollyJNI_onSurfaceChanged(JNIEnv* env, jclass clazz, jlong address, jint w, jint h) {
    Renderer* renderer = (Renderer*) address;
    renderer->onSurfaceChanged(w, h);
}

void Java_camera_DollyJNI_draw(JNIEnv* env, jclass clazz, jlong address, jfloat dz) {
    Renderer* renderer = (Renderer*) address;
    renderer->draw(dz);
}

void Java_camera_DollyJNI_delete(JNIEnv* env, jclass clazz, jlong address) {
    Renderer* renderer = (Renderer*) address;
    delete renderer;
}