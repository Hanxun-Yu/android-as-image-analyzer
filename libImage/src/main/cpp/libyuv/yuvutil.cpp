
#include <jni.h>
#include <string>
#include "libJniHelper/JniHelper.h"

extern "C" {
#include "libyuv/libyuv.h"

}

void bgra_to_ARGB(uint8 *bgraArr, int width, int height);
void swap_br_in_RGBA_BGRA(uint8 *bgraArr, int width, int height);



JNIEXPORT jint JNICALL bgra8888ToYuvNV12
        (JNIEnv *env, jclass clazz, jbyteArray src, jbyteArray target,
         jint width, jint height) {
    jbyte *src_data = env->GetByteArrayElements(src, JNI_FALSE);
    jbyte *dst_data = env->GetByteArrayElements(target, JNI_FALSE);

    uint8 *bgra = reinterpret_cast<uint8 *>(src_data);
    uint8 *nv12_y = reinterpret_cast<uint8 *>(dst_data);
    int nv12_y_size = width * height;
    uint8 *nv12_vu = nv12_y + nv12_y_size;


//    int ARGBToNV21(const uint8_t* src_argb,
//                   int src_stride_argb,
//                   uint8_t* dst_y,
//                   int dst_stride_y,
//                   uint8_t* dst_vu,
//                   int dst_stride_vu,
//                   int width,
//                   int height);

    //libyuv::ARGBToNV12 接收BGRA
    libyuv::ARGBToNV12(bgra, width * 4,
                       nv12_y, width,
                       nv12_vu, width,
                       width, height);

    env->ReleaseByteArrayElements(src, src_data, NULL);
    env->ReleaseByteArrayElements(target, dst_data, NULL);
    return JNI_OK;
}

JNIEXPORT jint JNICALL bgra8888ToYuvNV21
        (JNIEnv *env, jclass clazz, jbyteArray src, jbyteArray target,
         jint width, jint height) {
    jbyte *src_data = env->GetByteArrayElements(src, JNI_FALSE);
    jbyte *dst_data = env->GetByteArrayElements(target, JNI_FALSE);

    uint8 *bgra = reinterpret_cast<uint8 *>(src_data);
    uint8 *nv21_y = reinterpret_cast<uint8 *>(dst_data);
    int nv21_y_size = width * height;
    uint8 *nv21_vu = nv21_y + nv21_y_size;


//    int ARGBToNV21(const uint8_t* src_argb,
//                   int src_stride_argb,
//                   uint8_t* dst_y,
//                   int dst_stride_y,
//                   uint8_t* dst_vu,
//                   int dst_stride_vu,
//                   int width,
//                   int height);

    //libyuv::ARGBToNV21 接收BGRA
    libyuv::ARGBToNV21(bgra, width * 4,
                       nv21_y, width,
                       nv21_vu, width,
                       width, height);

    env->ReleaseByteArrayElements(src, src_data, NULL);
    env->ReleaseByteArrayElements(target, dst_data, NULL);
    return JNI_OK;
}

JNIEXPORT void JNICALL yuvNV12ToRGBA8888(JNIEnv *env, jclass type, jbyteArray nv12,
                                         jbyteArray argb8888,
                                         jint width, jint height) {

    jbyte *src_nv12_data = env->GetByteArrayElements(nv12, JNI_FALSE);
    jbyte *tar_rgb_data = env->GetByteArrayElements(argb8888, JNI_FALSE);

    jint src_nv12_y_size = width * height;
    jbyte *src_nv12_y_data = src_nv12_data;
    jbyte *src_nv12_uv_data = src_nv12_data + src_nv12_y_size;

    libyuv::NV12ToARGB(
            (uint8 *) src_nv12_y_data, width,
            (uint8 *) src_nv12_uv_data, width,
            (uint8 *) tar_rgb_data, width * 4,
            width, height);

    //The ARGB of libyuv is BGRA in memory
    swap_br_in_RGBA_BGRA(reinterpret_cast<uint8 *>(tar_rgb_data), width, height);

    env->ReleaseByteArrayElements(argb8888, tar_rgb_data, NULL);
    env->ReleaseByteArrayElements(nv12, src_nv12_data, NULL);
}

/**
 *
 * @param env
 * @param type
 * @param nv21
 * @param argb8888
 * @param width
 * @param height
 */
JNIEXPORT void JNICALL yuvNV21ToBGRA8888(JNIEnv *env, jclass type, jbyteArray nv21,
                                         jbyteArray argb8888,
                                         jint width, jint height) {

    jbyte *src_nv21_data = env->GetByteArrayElements(nv21, JNI_FALSE);
    jbyte *tar_rgb_data = env->GetByteArrayElements(argb8888, JNI_FALSE);

//    LOGD("yuvNV21ToARGB8888 nv21 src_nv21_data:%d", env->GetArrayLength(nv21));

    jint src_nv21_y_size = width * height;
    jbyte *src_nv21_y_data = src_nv21_data;
    jbyte *src_nv21_uv_data = src_nv21_data + src_nv21_y_size;

    libyuv::NV21ToARGB(
            (uint8 *) src_nv21_y_data, width,
            (uint8 *) src_nv21_uv_data, width,
            (uint8 *) tar_rgb_data, width * 4,
            width, height);

    env->ReleaseByteArrayElements(argb8888, tar_rgb_data, NULL);
    env->ReleaseByteArrayElements(nv21, src_nv21_data, NULL);
}

JNIEXPORT void JNICALL yuvNV12ToBGRA8888(JNIEnv *env, jclass type, jbyteArray nv21,
                                         jbyteArray argb8888,
                                         jint width, jint height) {

    jbyte *src_nv12_data = env->GetByteArrayElements(nv21, JNI_FALSE);
    jbyte *tar_rgb_data = env->GetByteArrayElements(argb8888, JNI_FALSE);


    jint src_nv12_y_size = width * height;
    jbyte *src_nv12_y_data = src_nv12_data;
    jbyte *src_nv12_uv_data = src_nv12_data + src_nv12_y_size;

    //这里返回的是bgra
    libyuv::NV12ToARGB(
            (uint8 *) src_nv12_y_data, width,
            (uint8 *) src_nv12_uv_data, width,
            (uint8 *) tar_rgb_data, width * 4,
            width, height);

    env->ReleaseByteArrayElements(argb8888, tar_rgb_data, NULL);
    env->ReleaseByteArrayElements(nv21, src_nv12_data, NULL);
}


/**
 *
 * @param env
 * @param type
 * @param nv21
 * @param argb8888
 * @param width
 * @param height
 */
JNIEXPORT void JNICALL yuvNV21ToRGBA8888(JNIEnv *env, jclass type, jbyteArray nv21,
                                         jbyteArray argb8888,
                                         jint width, jint height) {

    jbyte *src_nv21_data = env->GetByteArrayElements(nv21, JNI_FALSE);
    jbyte *tar_rgb_data = env->GetByteArrayElements(argb8888, JNI_FALSE);

    jint src_nv21_y_size = width * height;
    jbyte *src_nv21_y_data = src_nv21_data;
    jbyte *src_nv21_uv_data = src_nv21_data + src_nv21_y_size;

    //return BGRA
    libyuv::NV21ToARGB(
            (uint8 *) src_nv21_y_data, width,
            (uint8 *) src_nv21_uv_data, width,
            (uint8 *) tar_rgb_data, width * 4,
            width, height);

    swap_br_in_RGBA_BGRA(reinterpret_cast<uint8 *>(tar_rgb_data), width, height);
    env->ReleaseByteArrayElements(argb8888, tar_rgb_data, NULL);
    env->ReleaseByteArrayElements(nv21, src_nv21_data, NULL);
}

void bgra_to_ARGB(uint8 *bgraArr, int width, int height) {
    for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
            int index = 4 * (width * row + col);

            uint8 a = bgraArr[index + 3];
            uint8 r = bgraArr[index + 2];

            bgraArr[index + 3] = bgraArr[index]; // B -> A
            bgraArr[index + 2] = bgraArr[index + 1]; //G -> R
            bgraArr[index] = a;
            bgraArr[index + 1] = r;
        }
    }
}

/**
 * swap b to r, r to b
 * @param dataArr
 * @param width
 * @param height
 */
void swap_br_in_RGBA_BGRA(uint8 *dataArr, int width, int height) {
    for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
            int index = 4 * (width * row + col);

            uint8 b = dataArr[index + 2];

            dataArr[index + 2] = dataArr[index]; // r -> b
            dataArr[index] = b; // b -> r
        }
    }
}

JNINativeMethod nativeMethod[] = {
        {"bgra8888ToYuvNV21", "([B[BII)V", (void *) bgra8888ToYuvNV21},
        {"bgra8888ToYuvNV12", "([B[BII)V", (void *) bgra8888ToYuvNV12},
        {"yuvNV12ToRGBA8888", "([B[BII)V", (void *) yuvNV12ToRGBA8888},
        {"yuvNV21ToRGBA8888", "([B[BII)V", (void *) yuvNV21ToRGBA8888},
        {"yuvNV12ToBGRA8888", "([B[BII)V", (void *) yuvNV12ToBGRA8888},
        {"yuvNV21ToBGRA8888", "([B[BII)V", (void *) yuvNV21ToBGRA8888},
};


std::string myClassName = "org/yuhanxun/imageanalyzer/libimage/libyuv/YuvJni";

JNIEXPORT jint
JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {

    return JniHelper::handleJNILoad(vm, reserved, myClassName,
                                    nativeMethod, sizeof(nativeMethod) / sizeof(nativeMethod[0]));
}