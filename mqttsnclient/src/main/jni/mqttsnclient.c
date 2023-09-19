#include <string.h>
#include "MQTTSNPacket.h"
#include "net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient.h"

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeConnect(
    JNIEnv *env, jobject thisObj, jstring clientId, jint duration, jboolean isCleanSession, jboolean isWillFlag) {

    MQTTSNPacket_connectData options = MQTTSNPacket_connectData_initializer;

    options.clientID.cstring = ((char *)((*env)->GetStringUTFChars(env, clientId, 0)));
    options.duration = (unsigned short)duration;
    options.cleansession = (unsigned char)isCleanSession;
    options.willFlag = (unsigned char)isWillFlag;

    int len = MQTTSNPacket_len(5 + MQTTSNstrlen(options.clientID));

    unsigned char buf[len];

    MQTTSNSerialize_connect(buf, len, &options);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)buf);

    jclass bufcls = (*env)->FindClass(env, "java/nio/ByteBuffer");
    jmethodID wrap = (*env)->GetStaticMethodID(env, bufcls, "wrap", "([B)Ljava/nio/ByteBuffer;");
    jobject buffer = (jobject)(*env)->CallStaticObjectMethod(env, bufcls, wrap, bytes);

    (*env)->DeleteLocalRef(env, bytes);

    return buffer;
}

int subscribeLength(MQTTSN_topicid* topicFilter)
{
	int len = 4;

	if (topicFilter->type == MQTTSN_TOPIC_TYPE_NORMAL)
		len += topicFilter->data.long_.len;
	else if (topicFilter->type == MQTTSN_TOPIC_TYPE_SHORT || topicFilter->type == MQTTSN_TOPIC_TYPE_PREDEFINED)
		len += 2;

	return len;
}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeSubscribeNormal(
    JNIEnv *env, jobject thisObj, jboolean dup, jint qos, jint messageId, jstring topic) {

    MQTTSN_topicid topicType;

    topicType.type = MQTTSN_TOPIC_TYPE_NORMAL;
    topicType.data.long_.name = ((char *)((*env)->GetStringUTFChars(env, topic, 0)));
    topicType.data.long_.len = strlen(topicType.data.long_.name);

    unsigned char dupChar = (unsigned char)dup;
    int qosInt = (int)qos;
    unsigned short messageIdShort = (unsigned short)messageId;
    int topicLen = subscribeLength(&topicType);

    int len = MQTTSNPacket_len(topicLen);

    unsigned char buf[len];

    MQTTSNSerialize_subscribe(buf, len, dupChar, qosInt, messageIdShort, &topicType);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)buf);

    jclass bufcls = (*env)->FindClass(env, "java/nio/ByteBuffer");
    jmethodID wrap = (*env)->GetStaticMethodID(env, bufcls, "wrap", "([B)Ljava/nio/ByteBuffer;");
    jobject buffer = (jobject)(*env)->CallStaticObjectMethod(env, bufcls, wrap, bytes);

    (*env)->DeleteLocalRef(env, bytes);

    return buffer;

}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeSubscribeShortName(
    JNIEnv *env, jobject thisObj, jboolean dup, jint qos, jint messageId, jstring topicShortName) {

    MQTTSN_topicid topicType;

    topicType.type = MQTTSN_TOPIC_TYPE_SHORT;

    const char *strChars = (*env)->GetStringUTFChars(env, topicShortName, 0);
    topicType.data.short_name[0] = strChars[0];
    topicType.data.short_name[1] = strChars[1];

    unsigned char dupChar = (unsigned char)dup;
    int qosInt = (int)qos;
    unsigned short messageIdShort = (unsigned short)messageId;
    int topicLen = subscribeLength(&topicType);

    int len = MQTTSNPacket_len(topicLen);

    unsigned char buf[len];

    MQTTSNSerialize_subscribe(buf, len, dupChar, qosInt, messageIdShort, &topicType);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)buf);

    jclass bufcls = (*env)->FindClass(env, "java/nio/ByteBuffer");
    jmethodID wrap = (*env)->GetStaticMethodID(env, bufcls, "wrap", "([B)Ljava/nio/ByteBuffer;");
    jobject buffer = (jobject)(*env)->CallStaticObjectMethod(env, bufcls, wrap, bytes);

    (*env)->DeleteLocalRef(env, bytes);

    return buffer;

}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeSubscribePredefined(
    JNIEnv *env, jobject thisObj, jboolean dup, jint qos, jint messageId, jint topicId) {

    MQTTSN_topicid topicType;

    topicType.type = MQTTSN_TOPIC_TYPE_PREDEFINED;
    topicType.data.id = (unsigned short)topicId;

    unsigned char dupChar = (unsigned char)dup;
    int qosInt = (int)qos;
    unsigned short messageIdShort = (unsigned short)messageId;
    int topicLen = subscribeLength(&topicType);

    int len = MQTTSNPacket_len(topicLen);

    unsigned char buf[len];

    MQTTSNSerialize_subscribe(buf, len, dupChar, qosInt, messageIdShort, &topicType);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)buf);

    jclass bufcls = (*env)->FindClass(env, "java/nio/ByteBuffer");
    jmethodID wrap = (*env)->GetStaticMethodID(env, bufcls, "wrap", "([B)Ljava/nio/ByteBuffer;");
    jobject buffer = (jobject)(*env)->CallStaticObjectMethod(env, bufcls, wrap, bytes);

    (*env)->DeleteLocalRef(env, bytes);

    return buffer;
}

int registerLength(MQTTSNString *topicname)
{
    int topicnamelen = (topicname->cstring) ? strlen(topicname->cstring) : topicname->lenstring.len;
	return topicnamelen + 5;
}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeRegister(
    JNIEnv *env, jobject thisObj, jint topicId, jint messageId, jstring topic) {

    MQTTSNString topicstr;

    topicstr.cstring = ((char *)((*env)->GetStringUTFChars(env, topic, 0)));
    topicstr.lenstring.len = strlen(topicstr.cstring);

    unsigned short topicIdShort = (unsigned short)topicId;
    unsigned short messageIdShort = (unsigned short)messageId;

    int len = MQTTSNPacket_len(registerLength(&topicstr));

    unsigned char buf[len];

    MQTTSNSerialize_register(buf, len, topicIdShort, messageIdShort, &topicstr);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)buf);

    jclass bufcls = (*env)->FindClass(env, "java/nio/ByteBuffer");
    jmethodID wrap = (*env)->GetStaticMethodID(env, bufcls, "wrap", "([B)Ljava/nio/ByteBuffer;");
    jobject buffer = (jobject)(*env)->CallStaticObjectMethod(env, bufcls, wrap, bytes);

    (*env)->DeleteLocalRef(env, bytes);

    return buffer;

}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeSearchGW
  (JNIEnv *env, jobject thisObj, jint radius) {

    int len = MQTTSNPacket_len(2);

    unsigned char buf[len];

    unsigned char radiusChar = (unsigned char)radius;

    MQTTSNSerialize_searchgw(buf, len, radiusChar);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)buf);

    jclass bufcls = (*env)->FindClass(env, "java/nio/ByteBuffer");
    jmethodID wrap = (*env)->GetStaticMethodID(env, bufcls, "wrap", "([B)Ljava/nio/ByteBuffer;");
    jobject buffer = (jobject)(*env)->CallStaticObjectMethod(env, bufcls, wrap, bytes);

    (*env)->DeleteLocalRef(env, bytes);

    return buffer;

}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializeRegAck
  (JNIEnv *env, jobject thisObj, jint topicId, jint messageId, jint returnCode) {

    int len = MQTTSNPacket_len(6);

    unsigned char buf[len];

    unsigned short topicIdShort = (unsigned short)topicId;
    unsigned short messageIdShort = (unsigned short)messageId;
    unsigned char returnCodeChar = (unsigned char)returnCode;

    MQTTSNSerialize_regack(buf, len, topicIdShort, messageIdShort, returnCodeChar);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)buf);

    jclass bufcls = (*env)->FindClass(env, "java/nio/ByteBuffer");
    jmethodID wrap = (*env)->GetStaticMethodID(env, bufcls, "wrap", "([B)Ljava/nio/ByteBuffer;");
    jobject buffer = (jobject)(*env)->CallStaticObjectMethod(env, bufcls, wrap, bytes);

    (*env)->DeleteLocalRef(env, bytes);

    return buffer;

}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_deserializeGwInfo
  (JNIEnv *env, jobject thisObj, jobject byteBuffer) {

    jclass cls_ByteBuffer = (*env)->GetObjectClass(env, byteBuffer);

    jmethodID limit = (*env)->GetMethodID(env, cls_ByteBuffer, "limit", "()I");
    jmethodID getBA = (*env)->GetMethodID(env, cls_ByteBuffer, "get", "([B)Ljava/nio/ByteBuffer;");

    int len = (int)(*env)->CallIntMethod(env, byteBuffer, limit);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    jobject obj_ByteBuffer = (*env)->CallObjectMethod(env, byteBuffer, getBA, bytes);

    unsigned char buf[len];

    (*env)->GetByteArrayRegion(env, bytes, 0, len, buf);

    unsigned char gatewayId;
    unsigned short gatewayAddressLen;
    unsigned char *gatewayAddress;

    int rc = MQTTSNDeserialize_gwinfo(&gatewayId, &gatewayAddressLen, &gatewayAddress, buf, len);

    buf[len] = '\0';

    fflush(stdout);

    (*env)->DeleteLocalRef(env, bytes);

    jclass cls_gwinfo = (*env)->FindClass(env, "net/farsystem/mqttsngatek/mqttsnclient/MQTTSNGwInfo");
    jmethodID cnstr_gwinfo = (*env)->GetMethodID(env, cls_gwinfo, "<init>", "(ILjava/lang/String;)V");

    jstring gwaddress = (*env)->NewStringUTF(env, gatewayAddress);

    jobject obj_gwinfo = (*env)->NewObject(env, cls_gwinfo, cnstr_gwinfo, (int)gatewayId, gwaddress);

    return obj_gwinfo;
}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializePingReq
  (JNIEnv *env, jobject thisObj, jstring clientId) {

    MQTTSNString clientid = MQTTSNString_initializer;

    int len;

    if ((*env)->IsSameObject(env, clientId, NULL)) {
        len = MQTTSNPacket_len(1);
    } else {
        clientid.cstring = ((char *)((*env)->GetStringUTFChars(env, clientId, 0)));
        len = MQTTSNPacket_len(MQTTSNstrlen(clientid) + 1);
    }

    unsigned char buf[len];

    int rc = MQTTSNSerialize_pingreq(buf, len, clientid);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)buf);

    jclass bufcls = (*env)->FindClass(env, "java/nio/ByteBuffer");
    jmethodID wrap = (*env)->GetStaticMethodID(env, bufcls, "wrap", "([B)Ljava/nio/ByteBuffer;");
    jobject buffer = (jobject)(*env)->CallStaticObjectMethod(env, bufcls, wrap, bytes);

    (*env)->DeleteLocalRef(env, bytes);

    return buffer;

}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_deserializePingResp
  (JNIEnv *env, jobject thisObj, jobject byteBuffer) {

    jclass cls_ByteBuffer = (*env)->GetObjectClass(env, byteBuffer);

    jmethodID limit = (*env)->GetMethodID(env, cls_ByteBuffer, "limit", "()I");
    jmethodID getBA = (*env)->GetMethodID(env, cls_ByteBuffer, "get", "([B)Ljava/nio/ByteBuffer;");

    int len = (int)(*env)->CallIntMethod(env, byteBuffer, limit);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    jobject obj_ByteBuffer = (*env)->CallObjectMethod(env, byteBuffer, getBA, bytes);

    unsigned char buf[len];

    (*env)->GetByteArrayRegion(env, bytes, 0, len, buf);

    int rc = MQTTSNDeserialize_pingresp(buf, len);

    if (rc != 1) {
        return NULL;
    }

    jclass cls_pingresp = (*env)->FindClass(env, "net/farsystem/mqttsngatek/mqttsnclient/MQTTSNPingResp");
    jmethodID cnstr_pingresp = (*env)->GetMethodID(env, cls_pingresp, "<init>", "()V");

    jobject obj_pingresp = (*env)->NewObject(env, cls_pingresp, cnstr_pingresp);

    return obj_pingresp;

}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_deserializeConnAck
  (JNIEnv *env, jobject thisObj, jobject byteBuffer) {

    jclass cls_ByteBuffer = (*env)->GetObjectClass(env, byteBuffer);

    jmethodID limit = (*env)->GetMethodID(env, cls_ByteBuffer, "limit", "()I");
    jmethodID getBA = (*env)->GetMethodID(env, cls_ByteBuffer, "get", "([B)Ljava/nio/ByteBuffer;");

    int len = (int)(*env)->CallIntMethod(env, byteBuffer, limit);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    jobject obj_ByteBuffer = (*env)->CallObjectMethod(env, byteBuffer, getBA, bytes);

    unsigned char buf[len];

    (*env)->GetByteArrayRegion(env, bytes, 0, len, buf);

    int connackrc;

    int rc = MQTTSNDeserialize_connack(&connackrc, buf, len);

    if (rc != 1) {
        return NULL;
    }

    jclass cls_connack = (*env)->FindClass(env, "net/farsystem/mqttsngatek/mqttsnclient/MQTTSNConnAck");
    jmethodID cnstr_connack = (*env)->GetMethodID(env, cls_connack, "<init>", "(I)V");

    jobject obj_connack = (*env)->NewObject(env, cls_connack, cnstr_connack, connackrc);

    return obj_connack;
}