/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient */

#ifndef _Included_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient
#define _Included_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient
 * Method:    serializeConnect
 * Signature: (Ljava/lang/String;IZZ)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeConnect
  (JNIEnv *, jobject, jstring, jint, jboolean, jboolean);

/*
 * Class:     net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient
 * Method:    serializeSubscribeNormal
 * Signature: (ZIILjava/lang/String;)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeSubscribeNormal
  (JNIEnv *, jobject, jboolean, jint, jint, jstring);

/*
 * Class:     net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient
 * Method:    serializeSubscribeShortName
 * Signature: (ZIILjava/lang/String;)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeSubscribeShortName
  (JNIEnv *, jobject, jboolean, jint, jint, jstring);

/*
 * Class:     net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient
 * Method:    serializeSubscribePredefined
 * Signature: (ZIII)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeSubscribePredefined
  (JNIEnv *, jobject, jboolean, jint, jint, jint);

/*
 * Class:     net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient
 * Method:    serializeRegister
 * Signature: (IILjava/lang/String;)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeRegister
  (JNIEnv *, jobject, jint, jint, jstring);

/*
 * Class:     net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient
 * Method:    serializeSearchGW
 * Signature: (I)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeSearchGW
  (JNIEnv *, jobject, jint);

/*
 * Class:     net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient
 * Method:    serializeRegAck
 * Signature: (III)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeRegAck
  (JNIEnv *, jobject, jint, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
