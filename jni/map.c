#include <jni.h>

#define LONGITUDE_F2I (float)(4294967296.0 / 360.0)
//#define LONGITUDE_I2F (float)(360.0 / 4294967296.0)

jlong Java_elf_map_Map_LongitudeFloatToLong(JNIEnv *env, jclass clazz, jfloat fLongitude){
  return (jlong)((180.0f + fLongitude) * LONGITUDE_F2I);
}

#define LATITUDE_F2I (float)(4294967296.0 / 360.0)
//#define LATITUDE_I2F (float)(360.0 / 4294967296.0)

jlong Java_elf_map_Map_LatitudeFloatToLong(JNIEnv *env, jclass clazz, jfloat fLatitude){
  return (jlong)((90.0f - fLatitude) * LATITUDE_F2I);
}
