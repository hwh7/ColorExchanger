#include <string.h>
#include <jni.h>

jstring Java_com_example_colorexchanger_NativeCall_add(JNIEnv* env, jobject thiz) {
    return (*env)->NewStringUTF(env, "Hello from JNI !");
}
