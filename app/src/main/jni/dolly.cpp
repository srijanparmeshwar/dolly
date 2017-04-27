#include "dolly.h"
#include "DepthEstimator.h"
#include "MeshGenerator.h"
#include "Renderer.h"

// Decodes a Java byte array into an OpenCV Mat.
cv::Mat decode(JNIEnv* env, jbyteArray jbytes) {
    jbyte* cbytes = env->GetByteArrayElements(jbytes, NULL);
    jsize length = env->GetArrayLength(jbytes);

    std::vector<uchar> data;
    data.reserve(length);
    for (size_t index = 0; index < length; index++) {
        data.push_back(cbytes[index]);
    }

    cv::Mat image = cv::imdecode(data, CV_LOAD_IMAGE_UNCHANGED);
    env->ReleaseByteArrayElements(jbytes, cbytes, JNI_ABORT);
    return image;
}

jlong Java_camera_DollyJNI_create(JNIEnv* env, jclass clazz, jbyteArray bytesA, jbyteArray bytesB, jint w, jint h, jstring jvertexShader, jstring jfragmentShader) {
    // Decode byte array into OpenCV Mat.
    cv::Mat A = decode(env, bytesA);
    cv::Mat B = decode(env, bytesB);

    // Calculate depth map and rectify colour image.
    cv::Mat depth;
    cv::Mat colour;
    DepthEstimator::estimateDepth(A, B, colour, depth);

    // Generate triangle mesh.
    std::vector<float> vertices;
    std::vector<float> colours;
    MeshGenerator::generate(colour, depth, vertices, colours);

    // Set up renderer and shaders.
    Renderer* renderer = new Renderer(w, h, 0.8f);

    const char* cvertexShader = env->GetStringUTFChars(jvertexShader, 0);
    const char* cfragmentShader = env->GetStringUTFChars(jfragmentShader, 0);

    renderer->init(std::string(cvertexShader), std::string(cfragmentShader));

    env->ReleaseStringUTFChars(jvertexShader, cvertexShader);
    env->ReleaseStringUTFChars(jfragmentShader, cfragmentShader);

    // Upload mesh.
    renderer->setMesh(vertices, colours);

    return (jlong) renderer;
}

// Set the new width and height of the viewport.
void Java_camera_DollyJNI_onSurfaceChanged(JNIEnv* env, jclass clazz, jlong address, jint w, jint h) {
    Renderer* renderer = (Renderer*) address;
    renderer->onSurfaceChanged(w, h);
}

// Draw the frame.
void Java_camera_DollyJNI_draw(JNIEnv* env, jclass clazz, jlong address, jfloat dollyStep) {
    Renderer* renderer = (Renderer*) address;
    renderer->draw(dollyStep);
}

// Delete renderer object.
void Java_camera_DollyJNI_delete(JNIEnv* env, jclass clazz, jlong address) {
    Renderer* renderer = (Renderer*) address;
    delete renderer;
}