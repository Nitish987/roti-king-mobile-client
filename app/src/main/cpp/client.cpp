#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_rotiking_client_common_settings_ApiKey_getFirebaseAccessServerKey(JNIEnv *env, jclass clazz) {
    std::string KEY = "Zm5GZVRiZldrdDBuNmxMcm0wNFlEVlBydm1MUksxR0lpVk1kT2VGWm1yZUcweVRTeVJSeDdJdUVTZXZ3T2x3Zg==";
    return env -> NewStringUTF(KEY.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_rotiking_client_common_security_AES128_getNativeEncryptionKey(JNIEnv *env, jclass clazz) {
    std::string KEY = "ZGpzaV4/V307LmpsZnNqKigmKCYoJioqKCZKS0xKY2Q=";
    return env -> NewStringUTF(KEY.c_str());
}