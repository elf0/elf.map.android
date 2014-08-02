LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := map

LOCAL_SRC_FILES := map.c

include $(BUILD_SHARED_LIBRARY)
