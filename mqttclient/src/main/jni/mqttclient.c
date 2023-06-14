#include "net_farsystem_mqttsngatek_mqttclient_NativeMQTTClient.h"

JNIEXPORT void JNICALL Java_NativeMQTTClient_connect(JNIEnv *env, jobject thisObj) {

    jclass cls_connack = (*env)->FindClass(env, "net/farsystem/mqttsngatek/mqttclient/MQTTSNConnack");
    jmethodID cnstr_connack = (*env)->GetMethodID(env, cls_connack, "<init>", "(I)V");

    jobject obj_connack = (*env)->NewObject(env, cls_connack, cnstr_connack, connackrc);

}