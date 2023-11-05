PROJECT_NAME := mqttsnclient

export OUTPUT_FILENAME
#MAKEFILE_NAME := $(CURDIR)/$(word $(words $(MAKEFILE_LIST)),$(MAKEFILE_LIST))
MAKEFILE_NAME := $(MAKEFILE_LIST)
MAKEFILE_DIR := $(dir $(MAKEFILE_NAME) )

GNU_INSTALL_ROOT := /usr

JAVA_INCLUDE_PATH := "$(JAVA_HOME)/include"
JAVA_LINUX_INCLUDE_PATH := "$(JAVA_HOME)/include/linux"
JAVA_DARWIN_INCLUDE_PATH := "$(JAVA_HOME)/include/darwin"

MK := mkdir
RM := rm -rf

#echo suspend
ifeq ("$(VERBOSE)","1")
NO_ECHO :=
else
NO_ECHO := @
endif

# Toolchain commands
CC              := '$(GNU_INSTALL_ROOT)/bin/gcc'
AS              := '$(GNU_INSTALL_ROOT)/bin/as'
AR              := '$(GNU_INSTALL_ROOT)/bin/ar' -r
LD              := '$(GNU_INSTALL_ROOT)/bin/ld'
NM              := '$(GNU_INSTALL_ROOT)/bin/nm'
OBJDUMP         := '$(GNU_INSTALL_ROOT)/bin/objdump'
OBJCOPY         := '$(GNU_INSTALL_ROOT)/bin/objcopy'
SIZE            := '$(GNU_INSTALL_ROOT)/bin/size'

C_SOURCE_FILES += \
$(abspath ../../../paho.mqtt-sn.embedded-c/MQTTSNPacket/src/MQTTSNPacket.c) \
$(abspath ../../../paho.mqtt-sn.embedded-c/MQTTSNPacket/src/MQTTSNConnectClient.c) \
$(abspath ../../../paho.mqtt-sn.embedded-c/MQTTSNPacket/src/MQTTSNDeserializePublish.c) \
$(abspath ../../../paho.mqtt-sn.embedded-c/MQTTSNPacket/src/MQTTSNSerializePublish.c) \
$(abspath ../../../paho.mqtt-sn.embedded-c/MQTTSNPacket/src/MQTTSNSubscribeClient.c) \
$(abspath ../../../paho.mqtt-sn.embedded-c/MQTTSNPacket/src/MQTTSNSearchClient.c) \
$(abspath ../../../src/main/jni/mqttsnclient.c) \

INC_PATHS  = -I$(JAVA_INCLUDE_PATH)
ifeq ("$(PLATFORM)", "linux")
OUTPUT_LIB := $(abspath ../../../build/native/libmqttsnclient.so)
INC_PATHS += -I$(JAVA_LINUX_INCLUDE_PATH)
endif
ifeq ("$(PLATFORM)", "darwin")
OUTPUT_LIB := $(abspath ../../../build/native/libmqttsnclient.dylib)
INC_PATHS += -I$(JAVA_DARWIN_INCLUDE_PATH)
endif
INC_PATHS += -I$(abspath ../../../paho.mqtt-sn.embedded-c/MQTTSNPacket/src)
INC_PATHS += -I$(abspath ../../../src/main/jni/include)

all:
	$(CC) -o $(OUTPUT_LIB) -lc -shared -fPIC $(INC_PATHS) $(C_SOURCE_FILES)