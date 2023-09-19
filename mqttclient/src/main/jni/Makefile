PROJECT_NAME := mqttclient

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

BUILD_DIR = $(abspath ../../../build/native)

PAHO_SOURCE_DIR = $(abspath ../../../paho.mqtt.c/src)

PAHO_SOURCE_FILES = $(wildcard $(PAHO_SOURCE_DIR)/*.c)
PAHO_SOURCE_FILES_AS = $(filter-out $(PAHO_SOURCE_DIR)/MQTTClient.c $(PAHO_SOURCE_DIR)/MQTTVersion.c, $(PAHO_SOURCE_FILES))

PAHO_MAJOR_VERSION := $(shell cat $(abspath ../../../paho.mqtt.c/version.major))
PAHO_MINOR_VERSION := $(shell cat $(abspath ../../../paho.mqtt.c/version.minor))
PAHO_PATCH_VERSION := $(shell cat $(abspath ../../../paho.mqtt.c/version.patch))

ifndef release.version
  release.version = $(MAJOR_VERSION).$(MINOR_VERSION).$(PATCH_VERSION)
endif

build.level = $(shell date)

C_SOURCE_FILES += \
$(PAHO_SOURCE_FILES_AS) \
$(abspath ../../../src/main/jni/mqttclient.c) \
$(abspath ../../../src/main/jni/mqttclientfactory.c) \

INC_PATHS  = -I$(JAVA_INCLUDE_PATH)

CCFLAGS_SO = -g -fPIC $(CFLAGS) -D_GNU_SOURCE -Os -Wall -fvisibility=hidden -I$(BUILD_DIR) -DPAHO_MQTT_EXPORTS=1
LDFLAGS_AS = $(LDFLAGS) -shared $(START_GROUP) -lpthread $(GAI_LIB) $(EXTRA_LIB) -lssl -lcrypto $(END_GROUP) -Wl,-init,$(MQTTASYNC_INIT)

ifeq ("$(PLATFORM)", "linux")
OUTPUT_LIB := $(abspath ../../../build/native/libmqttclient.so)
INC_PATHS += -I$(JAVA_LINUX_INCLUDE_PATH)
MQTTCLIENT_INIT = MQTTClient_init
MQTTASYNC_INIT = MQTTAsync_init
START_GROUP = -Wl,--start-group
END_GROUP = -Wl,--end-group
GAI_LIB = -lanl
EXTRA_LIB = -ldl
LDFLAGS_AS += -Wl,-soname,lib${MQTTLIB_AS}.so.${MAJOR_VERSION} -Wl,-no-whole-archive
endif
ifeq ("$(PLATFORM)", "darwin")
OUTPUT_LIB := $(abspath ../../../build/native/libmqttclient.dylib)
INC_PATHS += -I$(JAVA_DARWIN_INCLUDE_PATH)
MQTTCLIENT_INIT = _MQTTClient_init
MQTTASYNC_INIT = _MQTTAsync_init
START_GROUP =
END_GROUP =
GAI_LIB =
EXTRA_LIB = -ldl
CCFLAGS_SO += -Wno-deprecated-declarations -DOSX
LDFLAGS_AS += -Wl,-install_name,lib${MQTTLIB_AS}.so.${MAJOR_VERSION}
endif
INC_PATHS += -I$(abspath ../../../paho.mqtt.c/src)
INC_PATHS += -I$(abspath ../../../src/main/jni/include)

SED_COMMAND = sed \
    -e "s/@CLIENT_VERSION@/${release.version}/g" \
    -e "s/@BUILD_TIMESTAMP@/${build.level}/g"

all: library

clean:
	rm -rf ${BUILD_DIR}/*

$(BUILD_DIR)/VersionInfo.h: $(PAHO_SOURCE_DIR)/VersionInfo.h.in
	-mkdir -p $(BUILD_DIR)
	$(SED_COMMAND) $< > $@

library: $(BUILD_DIR)/VersionInfo.h
	${CC} ${CCFLAGS_SO} ${INC_PATHS} -o ${OUTPUT_LIB} ${C_SOURCE_FILES} -DOPENSSL ${LDFLAGS_AS}