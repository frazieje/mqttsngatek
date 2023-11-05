#include <string.h>
#include "MQTTSNPacket.h"
#include "net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient.h"

void loadByteBuffer(JNIEnv *env, unsigned char *buf, int len, jobject *obj) {
    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)buf);

    jclass bufcls = (*env)->FindClass(env, "java/nio/ByteBuffer");
    jmethodID wrap = (*env)->GetStaticMethodID(env, bufcls, "wrap", "([B)Ljava/nio/ByteBuffer;");
    *obj = (jobject)(*env)->CallStaticObjectMethod(env, bufcls, wrap, bytes);

    (*env)->DeleteLocalRef(env, bytes);
}

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

/*
 * Class:     net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient
 * Method:    serializePublishNormal
 * Signature: (ZIZII)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializePublishNormal
  (JNIEnv *env, jobject thisObj, jboolean dup, jint qos, jboolean retained, jint messageId, jint topicId, jbyteArray payload) {

    unsigned short topicIdShort = (unsigned short)topicId;

    MQTTSN_topicid topicType;

    topicType.type = MQTTSN_TOPIC_TYPE_NORMAL;
    topicType.data.id = topicIdShort;

    unsigned char dupChar = (unsigned char)dup;
    unsigned char retainedChar = (unsigned char)retained;
    int qosInt = (int)qos;
    unsigned short messageIdShort = (unsigned short)messageId;

    jboolean isCopy;
    jbyte * payloadJBytes = (*env)->GetByteArrayElements(env, payload, &isCopy);

    int payloadLength = (*env)->GetArrayLength(env, payload);

    unsigned char *payloadBuf = (unsigned char *)payloadJBytes;

    int len = MQTTSNPacket_len(6 + payloadLength);

    unsigned char buf[len];

    MQTTSNSerialize_publish(buf, len, dupChar, qosInt, retainedChar, messageIdShort, topicType, payloadBuf, payloadLength);

    (*env)->ReleaseByteArrayElements(env, payload, payloadJBytes, 0);

    jobject buffer;
    loadByteBuffer(env, buf, len, &buffer);

    return buffer;
}

/*
 * Class:     net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient
 * Method:    serializePublishPredefined
 * Signature: (ZIZII)Ljava/nio/ByteBuffer;
 */
//JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializePublishPredefined
//  (JNIEnv *env, jobject thisObj, jboolean dup, jint qos, jboolean retained, jint messageId, jint topicId, jbyteArray payload) {
//
//}

/*
 * Class:     net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient
 * Method:    serializePublishShortName
 * Signature: (ZIZILjava/lang/String;)Ljava/nio/ByteBuffer;
 */
//JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_serializePublishShortName
//  (JNIEnv *env, jobject thisObj, jboolean dup, jint qos, jboolean retained, jint messageId, jstring topic, jbyteArray payload) {
//
//}


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
    JNIEnv *env, jobject thisObj, jint messageId, jstring topic) {

    MQTTSNString topicstr;

    topicstr.cstring = ((char *)((*env)->GetStringUTFChars(env, topic, 0)));
    topicstr.lenstring.len = strlen(topicstr.cstring);

    unsigned short topicIdShort = 0;
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

    (*env)->GetByteArrayRegion(env, bytes, 0, len, (jbyte *)buf);

    unsigned char gatewayId;
    unsigned short gatewayAddressLen;
    unsigned char *gatewayAddress;

    int rc = MQTTSNDeserialize_gwinfo(&gatewayId, &gatewayAddressLen, &gatewayAddress, buf, len);

    buf[len] = '\0';

    fflush(stdout);

    (*env)->DeleteLocalRef(env, bytes);

    jclass cls_gwinfo = (*env)->FindClass(env, "net/farsystem/mqttsngatek/mqttsnclient/MQTTSNGwInfo");
    jmethodID cnstr_gwinfo = (*env)->GetMethodID(env, cls_gwinfo, "<init>", "(ILjava/lang/String;)V");

    jstring gwaddress = (*env)->NewStringUTF(env, (char *)gatewayAddress);

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

    (*env)->GetByteArrayRegion(env, bytes, 0, len, (jbyte *)buf);

    int rc = MQTTSNDeserialize_pingresp(buf, len);

    (*env)->DeleteLocalRef(env, bytes);

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

    (*env)->GetByteArrayRegion(env, bytes, 0, len, (jbyte *)buf);

    int connackrc;

    int rc = MQTTSNDeserialize_connack(&connackrc, buf, len);

    (*env)->DeleteLocalRef(env, bytes);

    if (rc != 1) {
        return NULL;
    }

    jclass cls_connack = (*env)->FindClass(env, "net/farsystem/mqttsngatek/mqttsnclient/MQTTSNConnAck");
    jmethodID cnstr_connack = (*env)->GetMethodID(env, cls_connack, "<init>", "(I)V");

    jobject obj_connack = (*env)->NewObject(env, cls_connack, cnstr_connack, connackrc);

    return obj_connack;
}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_deserializePubAck
  (JNIEnv *env, jobject thisObj, jobject byteBuffer) {
    jclass cls_ByteBuffer = (*env)->GetObjectClass(env, byteBuffer);

    jmethodID limit = (*env)->GetMethodID(env, cls_ByteBuffer, "limit", "()I");
    jmethodID getBA = (*env)->GetMethodID(env, cls_ByteBuffer, "get", "([B)Ljava/nio/ByteBuffer;");

    int len = (int)(*env)->CallIntMethod(env, byteBuffer, limit);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    jobject obj_ByteBuffer = (*env)->CallObjectMethod(env, byteBuffer, getBA, bytes);

    unsigned char buf[len];

    (*env)->GetByteArrayRegion(env, bytes, 0, len, (jbyte *)buf);

    unsigned short topicId;
    unsigned short messageId;
    unsigned char returnCode;

    int rc = MQTTSNDeserialize_puback(&topicId, &messageId, &returnCode, buf, len);

    (*env)->DeleteLocalRef(env, bytes);

    if (rc != 1) {
        return NULL;
    }

    jclass cls_puback = (*env)->FindClass(env, "net/farsystem/mqttsngatek/mqttsnclient/MQTTSNPubAck");
    jmethodID cnstr_puback = (*env)->GetMethodID(env, cls_puback, "<init>", "(III)V");

    jobject obj_puback = (*env)->NewObject(env, cls_puback, cnstr_puback, (int)messageId, (int) topicId, (int)returnCode);

    return obj_puback;
}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_deserializeRegAck
  (JNIEnv *env, jobject thisObj, jobject byteBuffer) {
    jclass cls_ByteBuffer = (*env)->GetObjectClass(env, byteBuffer);

    jmethodID limit = (*env)->GetMethodID(env, cls_ByteBuffer, "limit", "()I");
    jmethodID getBA = (*env)->GetMethodID(env, cls_ByteBuffer, "get", "([B)Ljava/nio/ByteBuffer;");

    int len = (int)(*env)->CallIntMethod(env, byteBuffer, limit);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    jobject obj_ByteBuffer = (*env)->CallObjectMethod(env, byteBuffer, getBA, bytes);

    unsigned char buf[len];

    (*env)->GetByteArrayRegion(env, bytes, 0, len, (jbyte *)buf);

    unsigned short topicId;
    unsigned short messageId;
    unsigned char returnCode;

    int rc = MQTTSNDeserialize_regack(&topicId, &messageId, &returnCode, buf, len);

    (*env)->DeleteLocalRef(env, bytes);

    if (rc != 1) {
        return NULL;
    }

    jclass cls_regack = (*env)->FindClass(env, "net/farsystem/mqttsngatek/mqttsnclient/MQTTSNRegAck");
    jmethodID cnstr_regack = (*env)->GetMethodID(env, cls_regack, "<init>", "(III)V");

    jobject obj_regack = (*env)->NewObject(env, cls_regack, cnstr_regack, (int)messageId, (int) topicId, (int)returnCode);

    return obj_regack;
}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_deserializeSubAck
  (JNIEnv *env, jobject thisObj, jobject byteBuffer) {
    jclass cls_ByteBuffer = (*env)->GetObjectClass(env, byteBuffer);

    jmethodID limit = (*env)->GetMethodID(env, cls_ByteBuffer, "limit", "()I");
    jmethodID getBA = (*env)->GetMethodID(env, cls_ByteBuffer, "get", "([B)Ljava/nio/ByteBuffer;");

    int len = (int)(*env)->CallIntMethod(env, byteBuffer, limit);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    jobject obj_ByteBuffer = (*env)->CallObjectMethod(env, byteBuffer, getBA, bytes);

    unsigned char buf[len];

    (*env)->GetByteArrayRegion(env, bytes, 0, len, (jbyte *)buf);

    int qos;
    unsigned short topicId;
    unsigned short messageId;
    unsigned char returnCode;

    int rc = MQTTSNDeserialize_suback(&qos, &topicId, &messageId, &returnCode, buf, len);

    (*env)->DeleteLocalRef(env, bytes);

    if (rc != 1) {
        return NULL;
    }

    jclass cls_suback = (*env)->FindClass(env, "net/farsystem/mqttsngatek/mqttsnclient/MQTTSNSubAck");
    jmethodID cnstr_suback = (*env)->GetMethodID(env, cls_suback, "<init>", "(IIII)V");

    jobject obj_suback = (*env)->NewObject(env, cls_suback, cnstr_suback, qos, (int)messageId, (int) topicId, (int)returnCode);

    return obj_suback;
}

JNIEXPORT jobject JNICALL Java_net_farsystem_mqttsngatek_mqttsnclient_NativeMQTTSNClient_deserializePublish
  (JNIEnv *env, jobject thisObj, jobject byteBuffer) {
    jclass cls_ByteBuffer = (*env)->GetObjectClass(env, byteBuffer);

    jmethodID limit = (*env)->GetMethodID(env, cls_ByteBuffer, "limit", "()I");
    jmethodID getBA = (*env)->GetMethodID(env, cls_ByteBuffer, "get", "([B)Ljava/nio/ByteBuffer;");

    int len = (int)(*env)->CallIntMethod(env, byteBuffer, limit);

    jbyteArray bytes = (jbyteArray)(*env)->NewByteArray(env, len);

    jobject obj_ByteBuffer = (*env)->CallObjectMethod(env, byteBuffer, getBA, bytes);

    unsigned char buf[len];

    (*env)->GetByteArrayRegion(env, bytes, 0, len, (jbyte *)buf);

    unsigned char dup;
    int qos;
    unsigned char retained;
    unsigned short topicId;
    unsigned short messageId;
    MQTTSN_topicid topicType;
    unsigned char *payload;
    int payloadLen;
    jstring topic = NULL;

    int rc = MQTTSNDeserialize_publish(&dup, &qos, &retained, &messageId, &topicType, &payload, &payloadLen, buf, len);

    if (topicType.type == MQTTSN_TOPIC_TYPE_NORMAL || topicType.type == MQTTSN_TOPIC_TYPE_PREDEFINED) {
        topicId = topicType.data.id;
    } else if (topicType.type == MQTTSN_TOPIC_TYPE_SHORT) {
        topic = (*env)->NewStringUTF(env, (char *)topicType.data.short_name);
    }

    (*env)->DeleteLocalRef(env, bytes);

    jbyteArray payloadBytes = (jbyteArray)(*env)->NewByteArray(env, payloadLen);

    (*env)->SetByteArrayRegion(env, payloadBytes, 0, payloadLen, (jbyte*)payload);

    jclass cls_publish = (*env)->FindClass(env, "net/farsystem/mqttsngatek/mqttsnclient/MQTTSNPublish");
    jmethodID cnstr_publish = (*env)->GetMethodID(env, cls_publish, "<init>", "(ZIZILjava/lang/String;I[B)V");

    jobject obj_publish = (*env)->NewObject(env, cls_publish, cnstr_publish, dup, qos, retained, (int)messageId, topic, (int)topicId, payloadBytes);

    (*env)->DeleteLocalRef(env, payloadBytes);

    return obj_publish;
}