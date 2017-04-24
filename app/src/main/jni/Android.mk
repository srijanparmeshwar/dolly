LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# Include static OpenCV libraries.
OPENCV_CAMERA_MODULES := off
OPENCV_INSTALL_MODULES := on
OPENCV_LIB_TYPE := STATIC
include $(OCV_ANDROID)/native/jni/OpenCV.mk

# Compile local source files and link.
NDK_MODULE_PATH = $(ANDROID_NDK)
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_C_INCLUDES += $(OCV_ANDROID)/native/jni/include
LOCAL_C_INCLUDES += $(GLM)
LOCAL_SRC_FILES += DepthEstimator.cpp
LOCAL_SRC_FILES += MeshGenerator.cpp
LOCAL_SRC_FILES += Renderer2.cpp
LOCAL_SRC_FILES += Renderer.cpp
LOCAL_SRC_FILES += dolly.cpp
LOCAL_LDLIBS += -llog
LOCAL_LDLIBS += -lGLESv3
LOCAL_MODULE := dolly

include $(BUILD_SHARED_LIBRARY)